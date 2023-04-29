package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.IProcessEdit;
import edu.wpi.fishfolk.database.TableEntry.Node;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.geometry.Point2D;
import lombok.Getter;

public class NodeDAO implements IDAO<Node>, IProcessEdit {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<Integer, Node> tableMap;
  private final DataEditQueue<Node> dataEditQueue;

  private final Stack<Integer> freeIDs;
  private int maxID = 4000;
  @Getter private int numNodes;

  /** DAO for Node table in PostgreSQL database. */
  public NodeDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "node";
    this.headers = new ArrayList<>(List.of("id", "x", "y", "floor", "building"));
    this.tableMap = new HashMap<>(800); // a little more than (3000-100)/5 * 4/3 = 730
    this.dataEditQueue = new DataEditQueue<>();
    freeIDs = new Stack<>();

    init(false);
    populateLocalTable();
  }

  @Override
  public void init(boolean drop) {

    for (int i = maxID; i >= 100; i -= 5) {
      freeIDs.add(i);
    }

    try {
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + dbConnection.getSchema()
              + "' AND tablename = '"
              + tableName
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      // drop if a table already exists with this name and drop is true
      if (results.getBoolean("exists")) {

        if (drop) { // drop and replace with new table
          query = "DROP TABLE " + tableName + ";";
          statement.execute(query);
        }
        // exists but dont drop - do nothing

      } else {
        // doesnt exist, create as usual
        query =
            "CREATE TABLE "
                + tableName
                + " (id SMALLINT PRIMARY KEY," // 2 bytes: -2^15 to 2^15-1
                + " x REAL," // 4 bytes
                + " y REAL,"
                + " floor VARCHAR(2)," // 2 characters
                + " building VARCHAR(16));"; // 16 characters
        statement.executeUpdate(query);
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void populateLocalTable() {

    try {

      // Prepare SQL query to select all nodes from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      HashSet<Integer> takenIDs = new HashSet<>();

      // For each Node in the results, create a new Node object and put it in the local table
      while (results.next()) {
        Node node =
            new Node(
                results.getInt(1),
                new Point2D(results.getDouble(2), results.getDouble(3)),
                results.getString(4),
                results.getString(5));
        tableMap.put(node.getNodeID(), node);
        takenIDs.add(node.getNodeID());
        numNodes++;
      }

      freeIDs.removeIf(takenIDs::contains);

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void processEdit(DataEdit<Object> edit) {

    switch (edit.getType()) {
      case INSERT:
        insertEntry((Node) edit.getNewEntry());
        break;
      case REMOVE:
        removeEntry((int) edit.getNewEntry());
        break;
      case UPDATE:
        updateEntry((Node) edit.getNewEntry());
        break;
    }
  }

  @Override
  public boolean insertEntry(Node entry) {

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

    // ensure that ID is taken if not already
    freeIDs.remove((Integer) entry.getNodeID());
    numNodes++;

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Put entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(Node entry) {

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getNodeID()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof Integer)) {
      System.out.println(
          "[NodeDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    Integer nodeID = (Integer) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(nodeID)) {
      System.out.println(
          "[NodeDAO.removeEntry]: Identifier " + nodeID + " does not exist in local table.");
      return false;
    }

    // free up id

    freeIDs.push(nodeID);
    numNodes--;

    // Get entry from local table
    Node entry = tableMap.get(nodeID);

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push a REMOVE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.REMOVE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Remove entry Node from local table
    tableMap.remove(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public Node getEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof Integer)) {
      System.out.println("[NodeDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    Integer nodeID = (Integer) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(nodeID)) {
      System.out.println(
          "[NodeDAO.getEntry]: Identifier " + nodeID + " does not exist in local table.");
      return null;
    }

    // Return Node object from local table
    return tableMap.get(nodeID);
  }

  @Override
  public ArrayList<Node> getAllEntries() {
    ArrayList<Node> allNodes = new ArrayList<>();

    // Add all Nodes in local table to a list
    for (int nodeID : tableMap.keySet()) {
      allNodes.add(tableMap.get(nodeID));
    }

    return allNodes;
  }

  public void undoChange() {

    // Pop the top item of the data edit stack
    DataEdit<Node> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getNodeID());
        break;

      case UPDATE:

        // UPDATE the entry if it was an UPDATE
        updateEntry(dataEdit.getOldEntry());
        break;

      case REMOVE:

        // INSERT the entry if it was a REMOVE
        insertEntry(dataEdit.getNewEntry());
        break;
    }

    // Pop the record of the undo from the data edits stack
    dataEditQueue.removeRecent();
  }

  @Override
  public boolean updateDatabase(boolean updateAll) {

    try {

      // Print active thread (DEBUG)
      System.out.println(
          "[NodeDAO.updateDatabase]: Updating database in " + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?);";

      String update =
          "UPDATE "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " SET "
              + headers.get(0)
              + " = ?, "
              + headers.get(1)
              + " = ?, "
              + headers.get(2)
              + " = ?, "
              + headers.get(3)
              + " = ?, "
              + headers.get(4)
              + " = ? WHERE "
              + headers.get(0)
              + " = ?;";

      String remove =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " WHERE "
              + headers.get(0)
              + " = ?;";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);
      PreparedStatement preparedUpdate = dbConnection.prepareStatement(update);
      PreparedStatement preparedRemove = dbConnection.prepareStatement(remove);

      // For each data edit in the data edit stack, perform the indicated update to the db table
      // while (dataEditQueue.hasNext()) {
      for (int i = 0; (i < dataEditQueue.getBatchLimit()) || updateAll; i++) {

        // If there is no data edit to use, break for loop
        if (!dataEditQueue.hasNext()) break;

        // Get the next data edit in the queue
        DataEdit<Node> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[NodeDAO.updateDatabase]: "
                + dataEdit.getType()
                + " Node "
                + dataEdit.getNewEntry().getNodeID());

        Node newNodeEntry = dataEdit.getNewEntry();

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Node's data into the prepared query
            preparedInsert.setInt(1, newNodeEntry.getNodeID());
            preparedInsert.setDouble(2, newNodeEntry.getPoint().getX());
            preparedInsert.setDouble(3, newNodeEntry.getPoint().getY());
            preparedInsert.setString(4, newNodeEntry.getFloor());
            preparedInsert.setString(5, newNodeEntry.getBuilding());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Node's data into the prepared query
            preparedUpdate.setInt(1, newNodeEntry.getNodeID());
            preparedUpdate.setDouble(2, newNodeEntry.getX());
            preparedUpdate.setDouble(3, newNodeEntry.getY());
            preparedUpdate.setString(4, newNodeEntry.getFloor());
            preparedUpdate.setString(5, newNodeEntry.getBuilding());
            preparedUpdate.setInt(6, newNodeEntry.getNodeID());

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Node's data into the prepared query
            preparedRemove.setInt(1, newNodeEntry.getNodeID());

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Node in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getNodeID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[NodeDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[NodeDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  @Override
  public boolean importCSV(String filepath, boolean backup) {

    String[] pathArr = filepath.split("/");

    if (backup) {

      // filepath except for last part (actual file name)
      StringBuilder folder = new StringBuilder();
      for (int i = 0; i < pathArr.length - 1; i++) {
        folder.append(pathArr[i]).append("/");
      }
      exportCSV(folder.toString());
    }

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(filepath)))) {

      // delete the old data
      dbConnection
          .createStatement()
          .executeUpdate("DELETE FROM " + dbConnection.getSchema() + "." + tableName + ";");

      // skip first row of csv which has the headers
      String line = br.readLine();

      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        Node n =
            new Node(
                Integer.parseInt(parts[0]),
                new Point2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2])),
                parts[3],
                parts[4]);

        // record that id is taken
        freeIDs.remove(n.getNodeID());

        // store node in local table
        tableMap.put(n.getNodeID(), n);

        insertPS.setInt(1, n.getNodeID());
        insertPS.setDouble(2, n.getX());
        insertPS.setDouble(3, n.getY());
        insertPS.setString(4, n.getFloor());
        insertPS.setString(5, n.getBuilding());

        insertPS.executeUpdate();
      }

      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public boolean exportCSV(String directory) {

    LocalDateTime dateTime = LocalDateTime.now();

    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofPattern-java.lang.String-
    String filename =
        tableName + "_" + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm")) + ".csv";

    try {
      PrintStream out = new PrintStream(new FileOutputStream(directory + "\\" + filename));

      out.println(String.join(",", headers));

      for (Map.Entry<Integer, Node> entry : tableMap.entrySet()) {
        Node n = entry.getValue();
        out.println(
            n.getNodeID()
                + ","
                + n.getX()
                + ","
                + n.getY()
                + ","
                + n.getFloor()
                + ","
                + n.getBuilding());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * Gets the next free id, removing it from usage. IDs are only put back when the node is removed.
   *
   * @return a unique id
   */
  public int getNextID() {

    if (freeIDs.isEmpty()) {
      // add 200 new ids if empty
      for (int i = maxID + 1000; i >= maxID; i -= 5) {
        freeIDs.push(i);
      }
      maxID += 1000;
    }
    return freeIDs.pop();
  }
}
