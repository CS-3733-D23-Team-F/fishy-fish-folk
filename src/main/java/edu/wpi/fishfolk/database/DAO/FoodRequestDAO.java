package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.TableEntry.FoodRequest;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.NewFoodItem;
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

public class FoodRequestDAO
    implements IDAO<FoodRequest>, IHasSubtable<NewFoodItem>, ICSVWithSubtable {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, FoodRequest> tableMap;
  private final DataEditQueue<FoodRequest> dataEditQueue;

  /** DAO for Food Request table in PostgreSQL database. */
  public FoodRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "foodrequest";
    this.headers =
        new ArrayList<>(
            List.of(
                "id",
                "assignee",
                "status",
                "notes",
                "totalprice",
                "deliveryroom",
                "deliverytime",
                "recipientname",
                "fooditems"));
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
                + " ("
                + "id TIMESTAMP PRIMARY KEY,"
                + "assignee VARCHAR(64),"
                + "status VARCHAR(12),"
                + "notes VARCHAR(256),"
                + "totalprice REAL,"
                + "deliveryroom VARCHAR(64),"
                + "deliverytime TIMESTAMP,"
                + "recipientname VARCHAR(64),"
                + "fooditems SERIAL"
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

      // Prepare SQL query to select all food requests from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // For each result, create a new FoodRequest object and put it in the local
      // table
      while (results.next()) {
        FoodRequest foodRequest =
            new FoodRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                results.getFloat(headers.get(4)),
                results.getString(headers.get(5)),
                results.getTimestamp(headers.get(6)).toLocalDateTime(),
                results.getString(headers.get(7)),
                getSubtableItems(results.getInt(headers.get(8))));
        tableMap.put(foodRequest.getFoodRequestID(), foodRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void prepareListener() {

    try {

      dbListener = ConnectionBuilder.buildConnection();

      if (dbListener == null) {
        System.out.println("[FoodRequestDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifyFoodRequest() RETURNS TRIGGER AS $foodrequest$"
                  + "BEGIN "
                  + "NOTIFY foodrequest;"
                  + "RETURN NULL;"
                  + "END; $foodrequest$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER foodRequestUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "foodrequest FOR EACH STATEMENT EXECUTE FUNCTION notifyFoodRequest()")
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
      dbListener.prepareStatement("LISTEN foodrequest").execute();
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
        System.out.println("[FoodRequestDAO.verifyLocalTable]: Notification received!");
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
  public boolean insertEntry(FoodRequest entry) {

    // Check if the entry already exists. Unlikely conflicts.
    if (tableMap.containsKey(entry.getFoodRequestID())) return false;

    // Mark entry FoodRequest status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Put entry FoodRequest in local table
    tableMap.put(entry.getFoodRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(FoodRequest entry) {

    // Check if the entry already exists.
    if (!tableMap.containsKey(entry.getFoodRequestID())) return false;

    // Mark entry FoodRequest status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getFoodRequestID()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry FoodRequest in local table
    tableMap.put(entry.getFoodRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FoodRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime foodRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(foodRequestID)) {
      System.out.println(
          "[FoodRequestDAO.removeEntry]: Identifier "
              + foodRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    FoodRequest entry = tableMap.get(foodRequestID);

    // Mark entry FoodRequest status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push a REMOVE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.REMOVE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Remove entry FoodRequest from local table
    tableMap.remove(entry.getFoodRequestID(), entry);

    return true;
  }

  @Override
  public FoodRequest getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FoodRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime foodRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(foodRequestID)) {
      System.out.println(
          "[FoodRequestDAO.getEntry]: Identifier "
              + foodRequestID
              + " does not exist in local table.");
      return null;
    }

    // Return FoodRequest object from local table
    return tableMap.get(foodRequestID);
  }

  @Override
  public ArrayList<FoodRequest> getAllEntries() {

    verifyLocalTable();

    ArrayList<FoodRequest> allFoodRequests = new ArrayList<>();

    // Add all FoodRequests in local table to a list
    for (LocalDateTime foodRequestID : tableMap.keySet()) {
      allFoodRequests.add(tableMap.get(foodRequestID));
    }

    return allFoodRequests;
  }

  @Override
  public void undoChange() {
    // Pop the top item of the data edit stack
    DataEdit<FoodRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getFoodRequestID());
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
          "[FoodRequestDAO.updateDatabase]: Updating database in "
              + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

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
              + " = ?, "
              + headers.get(6)
              + " = ?, "
              + headers.get(7)
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
        DataEdit<FoodRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[FoodRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " FoodRequest "
                + dataEdit.getNewEntry().getFoodRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new FoodRequest's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFoodRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedInsert.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedInsert.setString(4, dataEdit.getNewEntry().getNotes());
            preparedInsert.setDouble(5, dataEdit.getNewEntry().getTotalPrice());
            preparedInsert.setString(6, dataEdit.getNewEntry().getDeliveryRoom());
            preparedInsert.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryTime()));
            preparedInsert.setString(8, dataEdit.getNewEntry().getRecipientName());

            // Execute the query
            preparedInsert.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getFoodRequestID(), dataEdit.getNewEntry().getFoodItems());

            break;

          case UPDATE:

            // Put the new FoodRequest's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFoodRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setDouble(5, dataEdit.getNewEntry().getTotalPrice());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getDeliveryRoom());
            preparedUpdate.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryTime()));
            preparedUpdate.setString(8, dataEdit.getNewEntry().getRecipientName());
            preparedUpdate.setTimestamp(
                9, Timestamp.valueOf(dataEdit.getNewEntry().getFoodRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getFoodRequestID(), dataEdit.getNewEntry().getFoodItems());
            break;

          case REMOVE:

            // Put the new FoodRequest's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFoodRequestID()));

            // Execute the query
            deleteAllSubtableItems(dataEdit.getNewEntry().getFoodRequestID());
            preparedRemove.executeUpdate();
            break;
        }

        // Mark FoodRequest in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getFoodRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[FoodRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[FoodRequestDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  @Override
  public boolean importCSV(String tableFilepath, String subtableFilepath, boolean backup) {

    String[] pathArr = tableFilepath.split("/");

    final HashMap<LocalDateTime, FoodRequest> backupMap = new HashMap<>(tableMap);

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
                  + "ALTER SEQUENCE foodrequest_fooditems_seq RESTART WITH 1;");

      // Get map representation of subtable
      HashMap<Integer, ArrayList<NewFoodItem>> subtable = importSubtable(subtableFilepath);

      // skip first row of csv which has the headers
      String line = br.readLine();

      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        FoodRequest foodRequest =
            new FoodRequest(
                LocalDateTime.parse(
                    parts[0].replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parts[1].replace("\"", ""),
                FormStatus.valueOf(parts[2]),
                parts[3].replace("\"", ""),
                Float.parseFloat(parts[4]),
                parts[5].replace("\"", ""),
                LocalDateTime.parse(
                    parts[6].replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                parts[7].replace("\"", ""),
                subtable.get(Integer.parseInt(parts[8])));

        tableMap.put(foodRequest.getFoodRequestID(), foodRequest);

        insertPS.setTimestamp(1, Timestamp.valueOf(foodRequest.getFoodRequestID()));
        insertPS.setString(2, foodRequest.getAssignee());
        insertPS.setString(3, foodRequest.getFormStatus().toString());
        insertPS.setString(4, foodRequest.getNotes());
        insertPS.setDouble(5, foodRequest.getTotalPrice());
        insertPS.setString(6, foodRequest.getDeliveryRoom());
        insertPS.setTimestamp(7, Timestamp.valueOf(foodRequest.getDeliveryTime()));
        insertPS.setString(8, foodRequest.getRecipientName());

        insertPS.executeUpdate();

        setSubtableItems(foodRequest.getFoodRequestID(), foodRequest.getFoodItems());
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
                    + "ALTER SEQUENCE foodrequest_fooditems_seq RESTART WITH 1;");

        String insert =
            "INSERT INTO "
                + dbConnection.getSchema()
                + "."
                + this.tableName
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement insertPS = dbConnection.prepareStatement(insert);

        // fill in database from backup
        for (Map.Entry<LocalDateTime, FoodRequest> entry : backupMap.entrySet()) {
          tableMap.put(entry.getValue().getFoodRequestID(), entry.getValue());

          insertPS.setTimestamp(1, Timestamp.valueOf(entry.getValue().getFoodRequestID()));
          insertPS.setString(2, entry.getValue().getAssignee());
          insertPS.setString(3, entry.getValue().getFormStatus().toString());
          insertPS.setString(4, entry.getValue().getNotes());
          insertPS.setDouble(5, entry.getValue().getTotalPrice());
          insertPS.setString(6, entry.getValue().getDeliveryRoom());
          insertPS.setTimestamp(7, Timestamp.valueOf(entry.getValue().getDeliveryTime()));
          insertPS.setString(8, entry.getValue().getRecipientName());

          insertPS.executeUpdate();

          setSubtableItems(entry.getValue().getFoodRequestID(), entry.getValue().getFoodItems());
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

    try {

      String tableFilename =
          tableName + "_" + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm")) + ".csv";

      PrintStream tableOut =
          new PrintStream(new FileOutputStream(directory + "\\" + tableFilename));

      tableOut.println(String.join(",", headers));

      for (Map.Entry<LocalDateTime, FoodRequest> entry : tableMap.entrySet()) {
        FoodRequest foodRequest = entry.getValue();
        tableOut.println(
            foodRequest.getFoodRequestID()
                + ","
                + foodRequest.getAssignee()
                + ","
                + foodRequest.getFormStatus()
                + ","
                + foodRequest.getNotes()
                + ","
                + foodRequest.getTotalPrice()
                + ","
                + foodRequest.getDeliveryRoom()
                + ","
                + foodRequest.getDeliveryTime()
                + ","
                + foodRequest.getRecipientName()
                + ","
                + getSubtableItemsID(foodRequest.getFoodRequestID()));
      }

      tableOut.close();

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
              + "fooditems"
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      // STEP 2: If instructed to drop the table, drop it
      if (drop) {
        query = "DROP TABLE " + dbConnection.getSchema() + "." + tableName + "fooditems" + ";";
        statement.execute(query);
      }

      // STEP 3: If it does not exist OR the table was dropped, make it exist
      if (!results.getBoolean("exists") || drop) {
        query =
            "CREATE TABLE "
                + tableName
                + "fooditems"
                + " ("
                + "itemkey INT,"
                + "itemname VARCHAR(64),"
                + "itemquantity INT"
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
          "SELECT fooditems FROM "
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
      return results.getInt("fooditems");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // Possible TODO: Check for ID = -1
      return -1;
    }
  }

  @Override
  public List<NewFoodItem> getSubtableItems(int subtableID) {

    try {

      // Create empty list so store all items
      ArrayList<NewFoodItem> allFoodItems = new ArrayList<>();

      // Query the subtable to return only items with the specified subtableID
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "fooditems "
              + "WHERE itemkey = '"
              + subtableID
              + "';";

      // Run the query
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // For each result, create a new food item and put it in the list
      while (results.next()) {
        allFoodItems.add(
            new NewFoodItem(results.getString("itemname"), results.getInt("itemquantity")));
      }

      // Return the list
      return allFoodItems;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public void setSubtableItems(LocalDateTime requestID, List<NewFoodItem> items) {

    try {

      // Remove all food items in the subtable with a matching subtableID
      deleteAllSubtableItems(requestID);

      // Query to insert one new of subtable entries
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + "fooditems"
              + " VALUES (?, ?, ?);";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);

      // Run the query for each item to insert
      for (NewFoodItem item : items) {
        preparedInsert.setInt(1, getSubtableItemsID(requestID));
        preparedInsert.setString(2, item.getName());
        preparedInsert.setInt(3, item.getQuantity());
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
      String query =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "fooditems"
              + " WHERE itemkey = ?;";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(query);

      // Setup the statement with subtableID and run it
      preparedDeleteAll.setInt(1, getSubtableItemsID(requestID));
      preparedDeleteAll.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public HashMap<Integer, ArrayList<NewFoodItem>> importSubtable(String subtableFilepath) {

    // Create empty output
    HashMap<Integer, ArrayList<NewFoodItem>> subtable = new HashMap<>();

    // Create buffered reader for CSV
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(subtableFilepath)))) {

      // Delete the old data
      dbConnection
          .createStatement()
          .executeUpdate(
              "DELETE FROM " + dbConnection.getSchema() + "." + tableName + "fooditems" + ";");

      // Iterate through each line of the CSV
      String line = br.readLine();
      while ((line = br.readLine()) != null) {

        // Split the input row into parts
        String[] parts = line.split(",");

        // Read the item ID (table link)
        int itemID = Integer.parseInt(parts[0]);

        // Make new list entry from row
        NewFoodItem foodItem = new NewFoodItem(parts[1], Integer.parseInt(parts[2]));

        // If there is no list at the item ID in the map, make one
        if (!subtable.containsKey(itemID)) {
          subtable.put(itemID, new ArrayList<>());
        }

        // Add the entry to the list at the item ID in the map
        subtable.get(itemID).add(foodItem);
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
              + "fooditems"
              + "_"
              + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm"))
              + ".csv";

      PrintStream subtableOut =
          new PrintStream(new FileOutputStream(directory + "\\" + subtableFilename));

      subtableOut.println("itemkey,itemname,itemquantity");

      for (Map.Entry<LocalDateTime, FoodRequest> entry : tableMap.entrySet()) {

        List<NewFoodItem> newFoodItems = entry.getValue().getFoodItems();
        int subtableID = getSubtableItemsID(entry.getValue().getFoodRequestID());

        for (NewFoodItem item : newFoodItems) {
          subtableOut.println(subtableID + "," + item.getName() + "," + item.getQuantity());
        }
      }

      subtableOut.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
