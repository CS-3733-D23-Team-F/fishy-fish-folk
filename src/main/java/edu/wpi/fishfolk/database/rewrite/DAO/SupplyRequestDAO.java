package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.DataEditQueue;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.SupplyRequest;
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

public class SupplyRequestDAO implements IDAO<SupplyRequest> {

  private final Connection dbConnection;

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
                getSupplyItems(results.getInt(headers.get(6))));
        tableMap.put(supplyRequest.getSupplyRequestID(), supplyRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(SupplyRequest entry) {

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
            setSupplyItems(
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
            setSupplyItems(
                dataEdit.getNewEntry().getSupplyRequestID(), dataEdit.getNewEntry().getSupplies());

            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getSupplyRequestID()));

            // Execute the query
            deleteAllSupplyItems(dataEdit.getNewEntry().getSupplyRequestID());
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

  private int getSupplyItemsTableID(LocalDateTime supplyRequestID) {
    try {
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT supplies FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE id = '"
              + Timestamp.valueOf(supplyRequestID)
              + "';";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();
      return results.getInt("supplies");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return -1;
    }
  }

  private List<SupplyItem> getSupplyItems(int id) {

    try {
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

      if (!results.getBoolean("exists")) {
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

      ArrayList<SupplyItem> allSupplyItems = new ArrayList<>();

      query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "supplyitems "
              + "WHERE itemkey = '"
              + id
              + "';";

      statement.execute(query);
      results = statement.getResultSet();

      while (results.next()) {
        allSupplyItems.add(new SupplyItem(results.getString("itemname"), 0));
      }

      return allSupplyItems;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  private void setSupplyItems(LocalDateTime supplyRequestID, List<SupplyItem> items) {
    try {

      Statement exists = dbConnection.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + dbConnection.getSchema()
              + "' AND tablename = '"
              + tableName
              + "supplyitems"
              + "');";
      exists.execute(query);
      ResultSet results = exists.getResultSet();
      results.next();

      if (!results.getBoolean("exists")) {
        query =
            "CREATE TABLE "
                + tableName
                + "supplyitems"
                + " ("
                + "itemkey INT,"
                + "itemname VARCHAR(64)"
                + ");";
        exists.executeUpdate(query);
      }

      String deleteAll =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "supplyitems"
              + " WHERE itemkey = ?;";

      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + "supplyitems"
              + " VALUES (?, ?);";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(deleteAll);
      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);

      preparedDeleteAll.setInt(1, getSupplyItemsTableID(supplyRequestID));
      preparedDeleteAll.executeUpdate();

      for (SupplyItem item : items) {
        preparedInsert.setInt(1, getSupplyItemsTableID(supplyRequestID));
        preparedInsert.setString(2, item.supplyName);
        preparedInsert.executeUpdate();
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  private void deleteAllSupplyItems(LocalDateTime supplyRequestID) {
    try {

      String deleteAll =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "supplyitems"
              + " WHERE itemkey = ?;";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(deleteAll);

      preparedDeleteAll.setInt(1, getSupplyItemsTableID(supplyRequestID));
      preparedDeleteAll.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
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
              + " VALUES (?, ?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        SupplyRequest sr =
            new SupplyRequest(
                LocalDateTime.parse(parts[0]),
                parts[1],
                FormStatus.valueOf(parts[2]),
                parts[3],
                parts[4],
                parts[5],
                getSupplyItems(Integer.parseInt(parts[6])));

        tableMap.put(sr.getSupplyRequestID(), sr);

        insertPS.setTimestamp(1, Timestamp.valueOf(sr.getSupplyRequestID()));
        insertPS.setString(2, sr.getAssignee());
        insertPS.setString(3, sr.getFormStatus().toString());
        insertPS.setString(4, sr.getNotes());
        insertPS.setString(5, sr.getLink());
        insertPS.setString(6, sr.getRoomNumber());

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
                + getSupplyItemsTableID(sr.getSupplyRequestID()));
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
