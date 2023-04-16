package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.TableEntry.Edge;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EdgeDAO implements IDAO<Edge> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashSet<Edge> edgeSet;
  private final DataEditQueue<Edge> dataEditQueue;

  /** DAO for Edge table in PostgreSQL database. */
  public EdgeDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "edge";
    this.headers = new ArrayList<>(List.of("startnode", "endnode"));
    this.edgeSet = new HashSet<>();
    this.dataEditQueue = new DataEditQueue<>();

    init(false);
    populateLocalTable();
  }

  @Override
  public void init(boolean drop) {

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
                + " (startnode SMALLINT," // 2 bytes, same as Node id
                + " endnode SMALLINT);"; // 2 bytes -2^15 to 2^15-1
        statement.executeUpdate(query);
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void populateLocalTable() {

    try {

      // Prepare SQL query to select all entries from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // Create a new Edge object for each result and put it in the local table
      while (results.next()) {
        Edge edge = new Edge(results.getInt(1), results.getInt(2));
        edgeSet.add(edge);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(Edge entry) {

    // Mark entry Edge status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Store edge in set
    edgeSet.add(entry);

    return true;
  }

  // update is meaningless for edges
  // map editor removes and adds
  @Override
  public boolean updateEntry(Edge entry) {
    return false;
  }

  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    // must be edge
    if (!(identifier instanceof Edge)) {
      System.out.println(
          "[EdgeDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    Edge edge = (Edge) identifier;

    // Check if local table contains identifier
    if (!edgeSet.contains(edge)) {
      System.out.println(
          "[EdgeDAO.removeEntry]: Identifier " + edge + " does not exist in local table.");
      return false;
    }

    // Mark edge status as NEW
    edge.setStatus(EntryStatus.NEW);

    // Push a REMOVE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(edge, DataEditType.REMOVE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Remove edge from set
    edgeSet.remove(edge);

    return true;
  }

  // get is also meaningless
  @Override
  public Edge getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<Edge> getAllEntries() {

    // hashset has no specified order so the list will be scrambled
    return new ArrayList<>(edgeSet);
  }

  @Override
  public void undoChange() {

    // Pop the top item of the data edit stack
    DataEdit<Edge> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry());
        break;

      case UPDATE:

        // do nothing for update
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
          "[EdgeDAO.updateDatabase]: Updating database in " + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO "
                  + dbConnection.getSchema()
                  + "." + this.tableName
                  + " VALUES (?, ?);";

      String remove =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " WHERE "
              + headers.get(0)
              + " = ? AND "
              + headers.get(1)
              + " = ?;";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);
      PreparedStatement preparedRemove = dbConnection.prepareStatement(remove);

      // For each data edit in the data edit stack, perform the indicated update to the db table
      // while (dataEditQueue.hasNext()) {
      for (int i = 0; (i < dataEditQueue.getBatchLimit()) || updateAll; i++) {

        // If there is no data edit to use, break for loop
        if (!dataEditQueue.hasNext()) break;

        // Get the next data edit in the queue
        DataEdit<Edge> dataEdit = dataEditQueue.next();
        Edge newEdgeEntry = dataEdit.getNewEntry();

        // Print update info (DEBUG)
        System.out.println(
            "[EdgeDAO.updateDatabase]: "
                + dataEdit.getType()
                + " Edge "
                + dataEdit.getNewEntry().toString());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Edge's data into the prepared query
            preparedInsert.setInt(1, newEdgeEntry.getStartNode());
            preparedInsert.setInt(2, newEdgeEntry.getEndNode());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // do nothing for update
            break;

          case REMOVE:

            // Put the new Edge's data into the prepared query
            preparedRemove.setInt(1, newEdgeEntry.getStartNode());
            preparedRemove.setInt(2, newEdgeEntry.getEndNode());

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Edge in local table as OLD
        edgeSet.remove(newEdgeEntry);
        newEdgeEntry.setStatus(EntryStatus.OLD);
        edgeSet.add(newEdgeEntry);
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[EdgeDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[EdgeDAO.updateDatabase]: Finished updating database in "
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
          "INSERT INTO " + dbConnection.getSchema() + "." + this.tableName + " VALUES (?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        Edge e = new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

        edgeSet.add(e);

        insertPS.setInt(1, e.getStartNode());
        insertPS.setInt(2, e.getEndNode());

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

      edgeSet.forEach(
          e -> {
            out.println(e.getStartNode() + "," + e.getEndNode());
          });

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

}
