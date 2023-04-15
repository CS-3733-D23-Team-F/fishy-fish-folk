package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.DataEditQueue;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Location;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationDAO implements IDAO<Location> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<String, Location> tableMap;
  private final DataEditQueue<Location> dataEditQueue;

  /** DAO for Location table in PostgreSQL database. */
  public LocationDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "location";
    this.headers = new ArrayList<>(List.of("longname", "shortname", "type"));
    this.tableMap = new HashMap<>();
    this.dataEditQueue = new DataEditQueue<>();

    populateLocalTable();
  }

  @Override
  public void init(boolean drop){

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
        query = "CREATE TABLE " + tableName
                + " (longname VARCHAR(64) PRIMARY KEY," // 64 char
                + " x REAL," //4 bytes
                + " y REAL,"
                + " floor VARCHAR(2)," //2 characters
                + " building VARCHAR(16));"; //16 characters
        statement.executeUpdate(query);
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }

  @Override
  public void populateLocalTable() {

    try {

      // Prepare SQL query to select all Locations from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // For each Location in the results, create a new Location object and put it in the local
      // table
      while (results.next()) {
        Location location =
            new Location(
                results.getString(headers.get(0)),
                results.getString(headers.get(1)),
                NodeType.valueOf(results.getString(headers.get(2))));
        tableMap.put(location.getLongName(), location);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(Location entry) {

    // Mark entry Location status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Put entry Location in local table
    tableMap.put(entry.getLongName(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(Location entry) {

    // Mark entry Location status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getLongName()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry Location in local table
    tableMap.put(entry.getLongName(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[LocationDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    String locationID = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(locationID)) {
      System.out.println(
          "[LocationDAO.removeEntry]: Identifier "
              + locationID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    Location entry = tableMap.get(locationID);

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
    tableMap.remove(entry.getLongName(), entry);

    return true;
  }

  @Override
  public Location getEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[LocationDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    String locationID = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(locationID)) {
      System.out.println(
          "[LocationDAO.getEntry]: Identifier " + locationID + " does not exist in local table.");
      return null;
    }

    // Return Node object from local table
    return tableMap.get(locationID);
  }

  @Override
  public ArrayList<Location> getAllEntries() {
    ArrayList<Location> allNodes = new ArrayList<>();

    // Add all Locations in local table to a list
    for (String longName : tableMap.keySet()) {
      allNodes.add(tableMap.get(longName));
    }

    return allNodes;
  }

  @Override
  public void undoChange() {

    // Pop the top item of the data edit stack
    DataEdit<Location> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getLongName());
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
          "[LocationDAO.updateDatabase]: Updating database in " + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO " + dbConnection.getSchema() + "." + this.tableName + " VALUES (?, ?, ?);";

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
        DataEdit<Location> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[LocationDAO.updateDatabase]: "
                + dataEdit.getType()
                + " Node "
                + dataEdit.getNewEntry().getLongName());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Location's data into the prepared query
            preparedInsert.setString(1, dataEdit.getNewEntry().getLongName());
            preparedInsert.setString(2, dataEdit.getNewEntry().getShortName());
            preparedInsert.setString(3, dataEdit.getNewEntry().getNodeType().toString());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Location's data into the prepared query
            preparedUpdate.setString(1, dataEdit.getNewEntry().getLongName());
            preparedUpdate.setString(2, dataEdit.getNewEntry().getShortName());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getNodeType().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getLongName());

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Location's data into the prepared query
            preparedRemove.setString(1, dataEdit.getNewEntry().getLongName());

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Location in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getLongName(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[LocationDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[LocationDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  @Override
  public boolean importCSV(String filepath, boolean backup) {
    return false;
  }

  @Override
  public boolean exportCSV(String directory) {
    return false;
  }
}
