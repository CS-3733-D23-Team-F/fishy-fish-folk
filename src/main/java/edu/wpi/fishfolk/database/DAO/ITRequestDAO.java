package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.TableEntry.ITRequest;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.ITComponent;
import edu.wpi.fishfolk.ui.ITPriority;
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

public class ITRequestDAO implements IDAO<ITRequest>, ICSVNoSubtable {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, ITRequest> tableMap;
  private final DataEditQueue<ITRequest> dataEditQueue;

  /** DAO for IT Request table in PostgreSQL database. */
  public ITRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "itrequest";
    this.headers =
        new ArrayList<>(
            List.of(
                "id",
                "assignee",
                "status",
                "notes",
                "component",
                "priority",
                "roomnumber",
                "contactinfo"));
    this.tableMap = new HashMap<>();
    this.dataEditQueue = new DataEditQueue<>();
    this.dataEditQueue.setBatchLimit(1);

    init(false);
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
                + "component VARCHAR(64),"
                + "priority VARCHAR(64),"
                + "roomnumber VARCHAR(64),"
                + "contactinfo VARCHAR(64)"
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

        ITRequest itRequest =
            new ITRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                ITComponent.valueOf(results.getString(headers.get(4))),
                ITPriority.valueOf(results.getString(headers.get(5))),
                results.getString(headers.get(6)),
                results.getString(headers.get(7)));
        tableMap.put(itRequest.getItRequestID(), itRequest);
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
        System.out.println("[ITRequestDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifyITRequest() RETURNS TRIGGER AS $itrequest$"
                  + "BEGIN "
                  + "NOTIFY itrequest;"
                  + "RETURN NULL;"
                  + "END; $itrequest$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER itRequestUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "itrequest FOR EACH STATEMENT EXECUTE FUNCTION notifyITRequest()")
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
      dbListener.prepareStatement("LISTEN itrequest").execute();
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
        System.out.println("[ITRequestDAO.verifyLocalTable]: Notification received!");
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
  public boolean insertEntry(ITRequest entry) {

    // Check if the entry already exists. Unlikely conflicts.
    if (tableMap.containsKey(entry.getItRequestID())) return false;

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
    tableMap.put(entry.getItRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(ITRequest entry) {

    // Check if the entry already exists.
    if (!tableMap.containsKey(entry.getItRequestID())) return false;

    // Mark entry FoodRequest status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getItRequestID()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry FoodRequest in local table
    tableMap.put(entry.getItRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[ITRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime itRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(itRequestID)) {
      System.out.println(
          "[ITRequestDAO.removeEntry]: Identifier "
              + itRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    ITRequest entry = tableMap.get(itRequestID);

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
    tableMap.remove(entry.getItRequestID(), entry);

    return true;
  }

  @Override
  public ITRequest getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[ITRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime itRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(itRequestID)) {
      System.out.println(
          "[ITRequestDAO.getEntry]: Identifier " + itRequestID + " does not exist in local table.");
      return null;
    }

    // Return FoodRequest object from local table
    return tableMap.get(itRequestID);
  }

  @Override
  public ArrayList<ITRequest> getAllEntries() {

    verifyLocalTable();

    ArrayList<ITRequest> allITRequests = new ArrayList<>();

    // Add all FoodRequests in local table to a list
    for (LocalDateTime itRequestID : tableMap.keySet()) {
      allITRequests.add(tableMap.get(itRequestID));
    }

    return allITRequests;
  }

  @Override
  public void undoChange() {
    // Pop the top item of the data edit stack
    DataEdit<ITRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getItRequestID());
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
          "[ITRequestDAO.updateDatabase]: Updating database in "
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
        DataEdit<ITRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[ITRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " FoodRequest "
                + dataEdit.getNewEntry().getItRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new FoodRequest's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getItRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedInsert.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedInsert.setString(4, dataEdit.getNewEntry().getNotes());
            preparedInsert.setString(5, dataEdit.getNewEntry().getComponent().toString());
            preparedInsert.setString(6, dataEdit.getNewEntry().getPriority().toString());
            preparedInsert.setString(7, dataEdit.getNewEntry().getRoomNumber());
            preparedInsert.setString(8, dataEdit.getNewEntry().getContactInfo());

            // Execute the query
            preparedInsert.executeUpdate();

            break;

          case UPDATE:

            // Put the new FoodRequest's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getItRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getComponent().toString());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getPriority().toString());
            preparedUpdate.setString(7, dataEdit.getNewEntry().getRoomNumber());
            preparedUpdate.setString(8, dataEdit.getNewEntry().getContactInfo());
            preparedUpdate.setTimestamp(
                9, Timestamp.valueOf(dataEdit.getNewEntry().getItRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();

            break;

          case REMOVE:

            // Put the new FoodRequest's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getItRequestID()));

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark FoodRequest in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getItRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[ITRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[ITRequestDAO.updateDatabase]: Finished updating database in "
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

        ITRequest itRequest =
            new ITRequest(
                LocalDateTime.parse(parts[0]),
                parts[1],
                FormStatus.valueOf(parts[2]),
                parts[3],
                ITComponent.valueOf(parts[4]),
                ITPriority.valueOf(parts[5]),
                parts[6],
                parts[7]);

        tableMap.put(itRequest.getItRequestID(), itRequest);

        insertPS.setTimestamp(1, Timestamp.valueOf(itRequest.getItRequestID()));
        insertPS.setString(2, itRequest.getAssignee());
        insertPS.setString(3, itRequest.getFormStatus().toString());
        insertPS.setString(4, itRequest.getNotes());
        insertPS.setString(5, itRequest.getComponent().toString());
        insertPS.setString(6, itRequest.getPriority().toString());
        insertPS.setString(7, itRequest.getRoomNumber());
        insertPS.setString(8, itRequest.getContactInfo());

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

      for (Map.Entry<LocalDateTime, ITRequest> entry : tableMap.entrySet()) {
        ITRequest itRequest = entry.getValue();
        out.println(
            itRequest.getItRequestID()
                + ","
                + itRequest.getAssignee()
                + ","
                + itRequest.getFormStatus()
                + ","
                + itRequest.getNotes()
                + ","
                + itRequest.getComponent()
                + ","
                + itRequest.getPriority()
                + ","
                + itRequest.getRoomNumber()
                + ","
                + itRequest.getContactInfo());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
