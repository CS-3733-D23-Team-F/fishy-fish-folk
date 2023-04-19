package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.IHasSubtable;
import edu.wpi.fishfolk.database.TableEntry.FlowerRequest;
import edu.wpi.fishfolk.ui.FlowerItem;
import edu.wpi.fishfolk.ui.FormStatus;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowerRequestDAO implements IDAO<FlowerRequest>, IHasSubtable<FlowerItem> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, FlowerRequest> tableMap;
  private final DataEditQueue<FlowerRequest> dataEditQueue;

  /** DAO for Flower Request table in PostgreSQL database. */
  public FlowerRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "flowerrequest";
    this.headers =
        new ArrayList<>(
            List.of(
                "id",
                "assignee",
                "status",
                "notes",
                "recipientname",
                "deliveryLocation",
                "deliverytime",
                "totalPrice",
                "items"));
    this.tableMap = new HashMap<>();
    this.dataEditQueue = new DataEditQueue<>();

    init(false);
    initSubtable(false);
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
                + "recipientname VARCHAR(64),"
                + "deliverylocation VARCHAR(64),"
                + "deliverytime TIMESTAMP,"
                + "totalprice REAL,"
                + "items SERIAL" // TODO: SUBTABLE in the future
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

      // For each entry in the results, create a new entry object and put it in the local table
      while (results.next()) {

        FlowerRequest flowerRequest =
            new FlowerRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                results.getString(headers.get(4)),
                results.getString(headers.get(5)),
                results.getTimestamp(headers.get(6)).toLocalDateTime(),
                results.getDouble(headers.get(7)),
                getSubtableItems(results.getInt(headers.get(8))));
        tableMap.put(flowerRequest.getFlowerRequestID(), flowerRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(FlowerRequest entry) {
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
    tableMap.put(entry.getFlowerRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(FlowerRequest entry) {
    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getFlowerRequestID()), entry, DataEditType.UPDATE),
        true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getFlowerRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FlowerRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime flowerRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(flowerRequestID)) {
      System.out.println(
          "[FlowerRequestDAO.removeEntry]: Identifier "
              + flowerRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    FlowerRequest entry = tableMap.get(flowerRequestID);

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
    tableMap.remove(entry.getFlowerRequestID(), entry);

    return true;
  }

  @Override
  public FlowerRequest getEntry(Object identifier) {
    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FlowerRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime flowerRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(flowerRequestID)) {
      System.out.println(
          "[FlowerRequestDAO.getEntry]: Identifier "
              + flowerRequestID
              + " does not exist in local table.");
      return null;
    }

    // Return entry objects from local table
    return tableMap.get(flowerRequestID);
  }

  @Override
  public ArrayList<FlowerRequest> getAllEntries() {
    ArrayList<FlowerRequest> allFlowerRequests = new ArrayList<>();

    // Add all entries in local table to a list
    for (LocalDateTime flowerRequestID : tableMap.keySet()) {
      allFlowerRequests.add(tableMap.get(flowerRequestID));
    }

    return allFlowerRequests;
  }

  @Override
  public void undoChange() {
    DataEdit<FlowerRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getFlowerRequestID());
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
          "[FlowerRequestDAO.updateDatabase]: Updating database in "
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
        DataEdit<FlowerRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[FlowerRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " FlowerRequest "
                + dataEdit.getNewEntry().getFlowerRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new entry's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFlowerRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedInsert.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedInsert.setString(4, dataEdit.getNewEntry().getNotes());
            preparedInsert.setString(5, dataEdit.getNewEntry().getRecipientName());
            preparedInsert.setString(6, dataEdit.getNewEntry().getDeliveryLocation());
            preparedInsert.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryTime()));
            preparedInsert.setDouble(8, dataEdit.getNewEntry().getTotalPrice());

            // Execute the query
            preparedInsert.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getFlowerRequestID(), dataEdit.getNewEntry().getItems());

            break;

          case UPDATE:

            // Put the new entry's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFlowerRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getRecipientName());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getDeliveryLocation());
            preparedUpdate.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryTime()));
            preparedUpdate.setDouble(8, dataEdit.getNewEntry().getTotalPrice());
            preparedUpdate.setTimestamp(
                9, Timestamp.valueOf(dataEdit.getNewEntry().getFlowerRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            setSubtableItems(
                dataEdit.getNewEntry().getFlowerRequestID(), dataEdit.getNewEntry().getItems());

            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFlowerRequestID()));

            // Execute the query
            deleteAllSubtableItems(dataEdit.getNewEntry().getFlowerRequestID());
            preparedRemove.executeUpdate();
            break;
        }

        // Mark entry in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getFlowerRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[FlowerRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[FlowerRequestDAO.updateDatabase]: Finished updating database in "
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
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        FlowerRequest fr =
            new FlowerRequest(
                LocalDateTime.parse(parts[0]),
                parts[1],
                FormStatus.valueOf(parts[2]),
                parts[3],
                parts[4],
                parts[5],
                LocalDateTime.parse(parts[6]),
                Double.parseDouble(parts[7]),
                getSubtableItems(Integer.parseInt(parts[8])));

        tableMap.put(fr.getFlowerRequestID(), fr);

        insertPS.setTimestamp(1, Timestamp.valueOf(fr.getFlowerRequestID()));
        insertPS.setString(2, fr.getAssignee());
        insertPS.setString(3, fr.getFormStatus().toString());
        insertPS.setString(4, fr.getNotes());
        insertPS.setString(5, fr.getRecipientName());
        insertPS.setString(6, fr.getDeliveryLocation());
        insertPS.setTimestamp(7, Timestamp.valueOf(fr.getDeliveryTime()));
        insertPS.setDouble(8, fr.getTotalPrice());

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

      for (Map.Entry<LocalDateTime, FlowerRequest> entry : tableMap.entrySet()) {
        FlowerRequest fr = entry.getValue();
        out.println(
            fr.getFlowerRequestID()
                + ","
                + fr.getAssignee()
                + ","
                + fr.getFormStatus()
                + ","
                + fr.getNotes()
                + ","
                + fr.getRecipientName()
                + ","
                + fr.getDeliveryLocation()
                + ","
                + fr.getTotalPrice()
                + ","
                + getSubtableItemsID(fr.getFlowerRequestID()));
      }

      out.close();
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
              + "floweritems"
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      // STEP 2: If instructed to drop the table, drop it
      if (drop) {
        query = "DROP TABLE " + dbConnection.getSchema() + "." + tableName + "floweritems" + ";";
        statement.execute(query);
      }

      // STEP 3: If it does not exist OR the table was dropped, make it exist
      if (!results.getBoolean("exists") || drop) {
        query =
            "CREATE TABLE "
                + tableName
                + "floweritems"
                + " ("
                + "itemkey INT,"
                + "itemname VARCHAR(64),"
                + "fullcost REAL,"
                + "itemamount INT"
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
          "SELECT items FROM "
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
      return results.getInt("items");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // Possible TODO: Check for ID = -1
      return -1;
    }
  }

  @Override
  public List<FlowerItem> getSubtableItems(int subtableID) {

    try {

      // Create empty list so store all items
      ArrayList<FlowerItem> allFlowerItems = new ArrayList<>();

      // Query the subtable to return only items with the specified subtableID
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "floweritems "
              + "WHERE itemkey = '"
              + subtableID
              + "';";

      // Run the query
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // For each result, create a new food item and put it in the list
      while (results.next()) {
        allFlowerItems.add(
            new FlowerItem(
                results.getString("itemname"),
                results.getInt("fullcost"),
                results.getInt("itemamount")));
      }

      // Return the list
      return allFlowerItems;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public void setSubtableItems(LocalDateTime requestID, List<FlowerItem> items) {

    try {

      // Remove all food items in the subtable with a matching subtableID
      deleteAllSubtableItems(requestID);

      // Query to insert one new of subtable entries
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + "floweritems"
              + " VALUES (?, ?, ?, ?);";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);

      // Run the query for each item to insert
      for (FlowerItem item : items) {
        preparedInsert.setInt(1, getSubtableItemsID(requestID));
        preparedInsert.setString(2, item.itemName);
        preparedInsert.setDouble(3, item.fullCost);
        preparedInsert.setInt(4, item.amount);
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
              + "floweritems"
              + " WHERE itemkey = ?;";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(deleteAll);

      // Setup the statement with subtableID and run it
      preparedDeleteAll.setInt(1, getSubtableItemsID(requestID));
      preparedDeleteAll.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}