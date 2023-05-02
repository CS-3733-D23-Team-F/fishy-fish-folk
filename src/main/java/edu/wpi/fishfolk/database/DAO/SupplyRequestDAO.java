package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.TableEntry.SupplyRequest;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.postgresql.PGConnection;
import org.postgresql.util.PSQLException;

public class SupplyRequestDAO
    implements IDAO<SupplyRequest>, IHasSubtable<SupplyItem>, ICSVWithSubtable {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, SupplyRequest> tableMap;
  private final DataEditQueue<SupplyRequest> dataEditQueue;

  /** DAO for Supply Request table in PostgreSQL database. */
  public SupplyRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "supplyrequest";
    this.headers =
        new ArrayList<>(
            List.of("id", "assignee", "status", "notes", "link", "roomnumber", "supplies"));
    this.tableMap = new HashMap<>();
    this.dataEditQueue = new DataEditQueue<>();
    this.dataEditQueue.setBatchLimit(1);

    init(false);
    initSubtable(false);
    prepareListener();
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
                + " (id TIMESTAMP PRIMARY KEY,"
                + "assignee VARCHAR(64),"
                + "status VARCHAR(12),"
                + "notes VARCHAR(256),"
                + "link VARCHAR(512),"
                + "roomnumber VARCHAR(64),"
                + "supplies SERIAL"
                + ");";
        statement.executeUpdate(query);
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void populateLocalTable() {
    try {

      // Prepare SQL query to select all nodes from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // For each in the results, create a new SupplyRequest object and put it in the local table
      while (results.next()) {
        SupplyRequest supplyRequest =
            new SupplyRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                results.getString(headers.get(4)),
                results.getString(headers.get(5)),
                getSubtableItems(results.getInt(headers.get(6))));
        tableMap.put(supplyRequest.getSupplyRequestID(), supplyRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void prepareListener() {

    try {

      dbListener = edu.wpi.fishfolk.database.ConnectionBuilder.buildConnection();

      if (dbListener == null) {
        System.out.println("[SupplyRequestDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifySupplyRequest() RETURNS TRIGGER AS $supplyrequest$"
                  + "BEGIN "
                  + "NOTIFY supplyrequest;"
                  + "RETURN NULL;"
                  + "END; $supplyrequest$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER supplyRequestUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "supplyrequest FOR EACH STATEMENT EXECUTE FUNCTION notifySupplyRequest()")
          .execute();

      // Start listener
      reListen();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void reListen() {
    try {
      dbListener.prepareStatement("LISTEN supplyrequest").execute();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void verifyLocalTable() {

    try {

      // Check for notifications on the table
      PGConnection driver = dbListener.unwrap(PGConnection.class);

      // See if there is a notification
      if (driver.getNotifications().length > 0) {
        System.out.println("[SupplyRequestDAO.verifyLocalTable]: Notification received!");
        tableMap.clear();
        populateLocalTable();
      }

      // Catch a timeout and reset refresh local table
    } catch (PSQLException e) {

      dbListener = ConnectionBuilder.buildConnection();
      reListen();
      populateLocalTable();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(SupplyRequest entry) {

    // Check if the entry already exists. Unlikely conflicts.
    if (tableMap.containsKey(entry.getSupplyRequestID())) return false;

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Put entry in local table
    tableMap.put(entry.getSupplyRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(SupplyRequest entry) {

    // Check if the entry already exists.
    if (!tableMap.containsKey(entry.getSupplyRequestID())) return false;

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getSupplyRequestID()), entry, DataEditType.UPDATE),
        true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getSupplyRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[SupplyRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime supplyRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(supplyRequestID)) {
      System.out.println(
          "[SupplyRequestDAO.removeEntry]: Identifier "
              + supplyRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    SupplyRequest entry = tableMap.get(supplyRequestID);

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push a REMOVE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.REMOVE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Remove entry from local table
    tableMap.remove(entry.getSupplyRequestID(), entry);

    return true;
  }

  @Override
  public SupplyRequest getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[SupplyRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime supplyRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(supplyRequestID)) {
      System.out.println(
          "[SupplyRequestDAO.getEntry]: Identifier "
              + supplyRequestID
              + " does not exist in local table.");
      return null;
    }

    // Return entry objects from local table
    return tableMap.get(supplyRequestID);
  }

  @Override
  public ArrayList<SupplyRequest> getAllEntries() {

    verifyLocalTable();

    ArrayList<SupplyRequest> allSupplyRequests = new ArrayList<>();

    // Add all entries in local table to a list
    for (LocalDateTime supplyRequestID : tableMap.keySet()) {
      allSupplyRequests.add(tableMap.get(supplyRequestID));
    }

    return allSupplyRequests;
  }

  @Override
  public void undoChange() {
    // Pop the top item of the data edit stack
    DataEdit<SupplyRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getSupplyRequestID());
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
          "[SupplyRequestDAO.updateDatabase]: Updating database in "
              + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?, ?);";

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
              + " = ?, "
              + headers.get(5)
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
        DataEdit<SupplyRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[SupplyRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " SupplyRequest "
                + dataEdit.getNewEntry().getSupplyRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new entry's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getSupplyRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedInsert.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedInsert.setString(4, dataEdit.getNewEntry().getNotes());
            preparedInsert.setString(5, dataEdit.getNewEntry().getLink());
            preparedInsert.setString(6, dataEdit.getNewEntry().getRoomNumber());

            // Execute the query
            preparedInsert.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getSupplyRequestID(), dataEdit.getNewEntry().getSupplies());

            break;

          case UPDATE:

            // Put the new entry's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getSupplyRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getLink());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getRoomNumber());
            preparedUpdate.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getSupplyRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getSupplyRequestID(), dataEdit.getNewEntry().getSupplies());

            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getSupplyRequestID()));

            // Execute the query
            deleteAllSubtableItems(dataEdit.getNewEntry().getSupplyRequestID());
            preparedRemove.executeUpdate();
            break;
        }

        // Mark entry in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getSupplyRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[SupplyRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[SupplyRequestDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  @Override
  public boolean importCSV(String tableFilepath, String subtableFilepath, boolean backup) {
    String[] pathArr = tableFilepath.split("/");

    final HashMap<LocalDateTime, SupplyRequest> backupMap = new HashMap<>(tableMap);

    if (backup) {

      // tableFilepath except for last part (actual file name)
      StringBuilder folder = new StringBuilder();
      for (int i = 0; i < pathArr.length - 1; i++) {
        folder.append(pathArr[i]).append("/");
      }
      exportCSV(folder.toString());
    }

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(tableFilepath)))) {

      // delete the old data
      dbConnection
          .createStatement()
          .executeUpdate(
              "DELETE FROM "
                  + dbConnection.getSchema()
                  + "."
                  + tableName
                  + ";"
                  + "ALTER SEQUENCE supplyrequest_supplies_seq RESTART WITH 1;");

      // Get map representation of subtable
      HashMap<Integer, ArrayList<SupplyItem>> subtable = importSubtable(subtableFilepath);

      // skip first row of csv which has the headers
      String line = br.readLine();

      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        SupplyRequest supplyRequest =
            new SupplyRequest(
                LocalDateTime.parse(
                    parts[0].replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parts[1].replace("\"", ""),
                FormStatus.valueOf(parts[2]),
                parts[3].replace("\"", ""),
                parts[4].replace("\"", ""),
                parts[5].replace("\"", ""),
                subtable.get(Integer.parseInt(parts[6])));

        tableMap.put(supplyRequest.getSupplyRequestID(), supplyRequest);

        insertPS.setTimestamp(1, Timestamp.valueOf(supplyRequest.getSupplyRequestID()));
        insertPS.setString(2, supplyRequest.getAssignee());
        insertPS.setString(3, supplyRequest.getFormStatus().toString());
        insertPS.setString(4, supplyRequest.getNotes());
        insertPS.setString(5, supplyRequest.getLink());
        insertPS.setString(6, supplyRequest.getRoomNumber());

        insertPS.executeUpdate();

        setSubtableItems(supplyRequest.getSupplyRequestID(), supplyRequest.getSupplies());
      }

      return true;

    } catch (Exception e) {
      System.out.println("Error importing CSV: " + e.getMessage() + "  -->  Restoring backup...");

      // something went wrong:

      // revert local copy to backup made before inserting
      tableMap.clear();
      tableMap.putAll(backupMap);

      // clear the database
      try {
        dbConnection
            .createStatement()
            .executeUpdate(
                "DELETE FROM "
                    + dbConnection.getSchema()
                    + "."
                    + tableName
                    + ";"
                    + "ALTER SEQUENCE supplyrequest_supplies_seq RESTART WITH 1;");

        String insert =
            "INSERT INTO "
                + dbConnection.getSchema()
                + "."
                + this.tableName
                + " VALUES (?, ?, ?, ?, ?, ?);";

        PreparedStatement insertPS = dbConnection.prepareStatement(insert);

        // fill in database from backup
        for (Map.Entry<LocalDateTime, SupplyRequest> entry : backupMap.entrySet()) {
          tableMap.put(entry.getValue().getSupplyRequestID(), entry.getValue());

          insertPS.setTimestamp(1, Timestamp.valueOf(entry.getValue().getSupplyRequestID()));
          insertPS.setString(2, entry.getValue().getAssignee());
          insertPS.setString(3, entry.getValue().getFormStatus().toString());
          insertPS.setString(4, entry.getValue().getNotes());
          insertPS.setString(5, entry.getValue().getLink());
          insertPS.setString(6, entry.getValue().getRoomNumber());

          insertPS.executeUpdate();

          setSubtableItems(entry.getValue().getSupplyRequestID(), entry.getValue().getSupplies());
        }

      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }

      // failed to import correctly
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

      for (Map.Entry<LocalDateTime, SupplyRequest> entry : tableMap.entrySet()) {
        SupplyRequest sr = entry.getValue();
        out.println(
            sr.getSupplyRequestID()
                + ","
                + sr.getAssignee()
                + ","
                + sr.getFormStatus()
                + ","
                + sr.getNotes()
                + ","
                + sr.getLink()
                + ","
                + sr.getRoomNumber()
                + ","
                + getSubtableItemsID(sr.getSupplyRequestID()));
      }

      out.close();

      exportSubtable(directory);

      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public void initSubtable(boolean drop) {

    try {

      // STEP 1: Check if the subtable exists
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + dbConnection.getSchema()
              + "' AND tablename = '"
              + tableName
              + "supplyitems"
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      // STEP 2: If instructed to drop the table, drop it
      if (drop) {
        query = "DROP TABLE " + dbConnection.getSchema() + "." + tableName + "supplyitems" + ";";
        statement.execute(query);
      }

      // STEP 3: If it does not exist OR the table was dropped, make it exist
      if (!results.getBoolean("exists") || drop) {
        query =
            "CREATE TABLE "
                + tableName
                + "supplyitems"
                + " ("
                + "itemkey INT,"
                + "itemname VARCHAR(64)"
                + ");";
        statement.executeUpdate(query);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public int getSubtableItemsID(LocalDateTime requestID) {

    try {

      // Setup query to read ID stored in tied column of main table
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT supplies FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE id = '"
              + Timestamp.valueOf(requestID)
              + "';";

      // Run that shit
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // Return the stored ID
      results.next();
      return results.getInt("supplies");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // Possible TODO: Check for ID = -1
      return -1;
    }
  }

  @Override
  public List<SupplyItem> getSubtableItems(int subtableID) {

    try {

      // Create empty list so store all items
      ArrayList<SupplyItem> allSupplyItems = new ArrayList<>();

      // Query the subtable to return only items with the specified subtableID
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "supplyitems "
              + "WHERE itemkey = '"
              + subtableID
              + "';";

      // Run the query
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // For each result, create a new item and put it in the list
      while (results.next()) {
        allSupplyItems.add(new SupplyItem(results.getString("itemname"), 0));
      }

      // Return the list
      return allSupplyItems;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public void setSubtableItems(LocalDateTime requestID, List<SupplyItem> items) {

    try {

      // Remove all food items in the subtable with a matching subtableID
      deleteAllSubtableItems(requestID);

      // Query to insert one new of subtable entries
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + "supplyitems"
              + " VALUES (?, ?);";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);

      // Run the query for each item to insert
      for (SupplyItem item : items) {
        preparedInsert.setInt(1, getSubtableItemsID(requestID));
        preparedInsert.setString(2, item.supplyName);
        preparedInsert.executeUpdate();
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void deleteAllSubtableItems(LocalDateTime requestID) {

    try {

      // Query to delete items with matching subtableID
      String deleteAll =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "supplyitems"
              + " WHERE itemkey = ?;";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(deleteAll);

      // Setup the statement with subtableID and run it
      preparedDeleteAll.setInt(1, getSubtableItemsID(requestID));
      preparedDeleteAll.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public HashMap<Integer, ArrayList<SupplyItem>> importSubtable(String subtableFilepath) {

    // Create empty output
    HashMap<Integer, ArrayList<SupplyItem>> subtable = new HashMap<>();

    // Create buffered reader for CSV
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(subtableFilepath)))) {

      // Delete the old data
      dbConnection
          .createStatement()
          .executeUpdate(
              "DELETE FROM " + dbConnection.getSchema() + "." + tableName + "supplyitems" + ";");

      // Iterate through each line of the CSV
      String line = br.readLine();
      while ((line = br.readLine()) != null) {

        // Split the input row into parts
        String[] parts = line.split(",");

        // Read the item ID (table link)
        int itemID = Integer.parseInt(parts[0]);

        // Make new list entry from row
        SupplyItem supplyItem = new SupplyItem(parts[1], 0);

        // If there is no list at the item ID in the map, make one
        if (!subtable.containsKey(itemID)) {
          subtable.put(itemID, new ArrayList<>());
        }

        // Add the entry to the list at the item ID in the map
        subtable.get(itemID).add(supplyItem);
      }

      // Return the map representation of the subtable
      return subtable;

    } catch (Exception e) {
      System.out.println(e.getMessage());

      // Return empty map on error, rather than null
      return new HashMap<>();
    }
  }

  public void exportSubtable(String directory) {

    LocalDateTime dateTime = LocalDateTime.now();

    try {

      String subtableFilename =
          tableName
              + "supplyitems"
              + "_"
              + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm"))
              + ".csv";

      PrintStream subtableOut =
          new PrintStream(new FileOutputStream(directory + "\\" + subtableFilename));

      subtableOut.println("itemkey,itemname");

      for (Map.Entry<LocalDateTime, SupplyRequest> entry : tableMap.entrySet()) {

        List<SupplyItem> supplyItems = entry.getValue().getSupplies();
        int subtableID = getSubtableItemsID(entry.getValue().getSupplyRequestID());

        for (SupplyItem item : supplyItems) {
          subtableOut.println(subtableID + "," + item.supplyName);
        }
      }

      subtableOut.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
