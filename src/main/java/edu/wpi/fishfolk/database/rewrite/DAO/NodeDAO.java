package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.DataEditQueue;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.geometry.Point2D;

public class NodeDAO implements IDAO<Node> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<Integer, Node> tableMap;
  private final DataEditQueue<Node> dataEditQueue;

  /** DAO for Node table in PostgreSQL database. */
  public NodeDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "micronode";
    this.headers = new ArrayList<>(List.of("id", "x", "y", "floor", "building"));
    this.tableMap = new HashMap<>();
    this.dataEditQueue = new DataEditQueue<>();

    populateLocalTable();
  }

  public void populateLocalTable() {

    try {

      // Prepare SQL query to select all nodes from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // For each Node in the results, create a new Node object and put it in the local table
      while (results.next()) {
        Node node =
            new Node(
                Integer.parseInt(results.getString(headers.get(0))),
                new Point2D(results.getDouble(headers.get(1)), results.getDouble(headers.get(2))),
                results.getString(headers.get(3)),
                results.getString(headers.get(4)));
        tableMap.put(node.getNodeID(), node);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(Node entry) {

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

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

        // REMOVE the entry if it was an INSERT
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

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Node's data into the prepared query
            preparedInsert.setInt(1, dataEdit.getNewEntry().getNodeID());
            preparedInsert.setDouble(2, dataEdit.getNewEntry().getPoint().getX());
            preparedInsert.setDouble(3, dataEdit.getNewEntry().getPoint().getY());
            preparedInsert.setString(4, dataEdit.getNewEntry().getFloor());
            preparedInsert.setString(5, dataEdit.getNewEntry().getBuilding());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Node's data into the prepared query
            preparedUpdate.setString(1, String.valueOf(dataEdit.getNewEntry().getNodeID()));
            preparedUpdate.setDouble(2, dataEdit.getNewEntry().getPoint().getX());
            preparedUpdate.setDouble(3, dataEdit.getNewEntry().getPoint().getY());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getFloor());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getBuilding());
            preparedUpdate.setString(6, String.valueOf(dataEdit.getNewEntry().getNodeID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Node's data into the prepared query
            preparedRemove.setString(1, String.valueOf(dataEdit.getNewEntry().getNodeID()));

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
}
