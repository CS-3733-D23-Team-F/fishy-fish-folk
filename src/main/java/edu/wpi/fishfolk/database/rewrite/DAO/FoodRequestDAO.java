package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.DataEditQueue;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FoodRequest;
import edu.wpi.fishfolk.ui.FormStatus;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodRequestDAO implements IDAO<FoodRequest> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<Integer, FoodRequest> tableMap;
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
                "recipientname"));
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
                + " ("
                + "id INT PRIMARY KEY,"
                + "assignee VARCHAR(64),"
                + "status VARCHAR(12),"
                + "notes VARCHAR(256),"
                + "totalprice REAL,"
                + "deliveryroom VARCHAR(64),"
                + "deliverytime TIMESTAMP,"
                + "recipientname VARCHAR(64)"
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

      // For each Location in the results, create a new FoodRequest object and put it in the local
      // table
      while (results.next()) {
        FoodRequest foodRequest =
            new FoodRequest(
                results.getInt(headers.get(0)),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                results.getFloat(headers.get(4)),
                results.getString(headers.get(5)),
                results.getTimestamp(headers.get(6)).toLocalDateTime(),
                results.getString(headers.get(7)));
        tableMap.put(foodRequest.getFoodRequestID(), foodRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(FoodRequest entry) {

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
    if (!(identifier instanceof Integer)) {
      System.out.println(
          "[FoodRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    Integer foodRequestID = (Integer) identifier;

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

    // Check if input identifier is correct type
    if (!(identifier instanceof Integer)) {
      System.out.println(
          "[FoodRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    Integer foodRequestID = (Integer) identifier;

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
    ArrayList<FoodRequest> allFoodRequests = new ArrayList<>();

    // Add all FoodRequests in local table to a list
    for (int foodRequestID : tableMap.keySet()) {
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
            preparedInsert.setInt(1, dataEdit.getNewEntry().getFoodRequestID());
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

            break;

          case UPDATE:

            // Put the new FoodRequest's data into the prepared query
            preparedUpdate.setInt(1, dataEdit.getNewEntry().getFoodRequestID());
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setDouble(5, dataEdit.getNewEntry().getTotalPrice());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getDeliveryRoom());
            preparedUpdate.setTimestamp(
                7, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryTime()));
            preparedUpdate.setString(8, dataEdit.getNewEntry().getRecipientName());
            preparedUpdate.setInt(9, dataEdit.getNewEntry().getFoodRequestID());

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new FoodRequest's data into the prepared query
            preparedRemove.setInt(1, dataEdit.getNewEntry().getFoodRequestID());

            // Execute the query
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
              + " VALUES (?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      int idTracker = 1;
      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        FoodRequest fr =
            new FoodRequest(
                idTracker,
                parts[1],
                FormStatus.valueOf(parts[2]),
                parts[3],
                Float.parseFloat(parts[4]),
                parts[5],
                LocalDateTime.parse(parts[6]),
                parts[7]);

        tableMap.put(fr.getFoodRequestID(), fr);

        insertPS.setString(1, fr.getAssignee());
        insertPS.setString(2, fr.getFormStatus().toString());
        insertPS.setString(3, fr.getNotes());
        insertPS.setDouble(4, fr.getTotalPrice());
        insertPS.setString(5, fr.getDeliveryRoom());
        insertPS.setTimestamp(6, Timestamp.valueOf(fr.getDeliveryTime()));
        insertPS.setString(7, fr.getRecipientName());

        insertPS.executeUpdate();
        idTracker += 1;
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

      for (Map.Entry<Integer, FoodRequest> entry : tableMap.entrySet()) {
        FoodRequest fr = entry.getValue();
        out.println(
            fr.getFoodRequestID()
                + ","
                + fr.getAssignee()
                + ","
                + fr.getFormStatus()
                + ","
                + fr.getNotes()
                + ","
                + fr.getTotalPrice()
                + ","
                + fr.getDeliveryRoom()
                + ","
                + fr.getDeliveryTime()
                + ","
                + fr.getRecipientName());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
