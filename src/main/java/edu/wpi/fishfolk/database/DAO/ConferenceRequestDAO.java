package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.TableEntry.ConferenceRequest;
import edu.wpi.fishfolk.ui.Recurring;
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

public class ConferenceRequestDAO implements IDAO<ConferenceRequest> {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, ConferenceRequest> tableMap;
  private final DataEditQueue<ConferenceRequest> dataEditQueue;

  /** DAO for Conference Request table in PostgreSQL database. */
  public ConferenceRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "conferencerequest";
    this.headers =
        new ArrayList<>(
            List.of(
                "id",
                "notes",
                "username",
                "starttime",
                "endtime",
                "recurring",
                "numattendees",
                "roomname"));
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
                + " (id TIMESTAMP PRIMARY KEY,"
                + "notes VARCHAR(256),"
                + "username VARCHAR(256),"
                + "starttime VARCHAR(256),"
                + "endtime VARCHAR(256),"
                + "recurring VARCHAR(256),"
                + "numattendees INT,"
                + "roomname VARCHAR(256)"
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
        ConferenceRequest conferenceRequest =
            new ConferenceRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                results.getString(headers.get(2)),
                results.getString(headers.get(3)),
                results.getString(headers.get(4)),
                Recurring.valueOf(results.getString(headers.get(5))),
                results.getInt(headers.get(6)),
                results.getString(headers.get(7)));
        tableMap.put(conferenceRequest.getConferenceRequestID(), conferenceRequest);
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
        System.out.println("[ConferenceRequestDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifyConferenceRequest() RETURNS TRIGGER AS $conferencerequest$"
                  + "BEGIN "
                  + "NOTIFY conferencerequest;"
                  + "RETURN NULL;"
                  + "END; $conferencerequest$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER furnitureRequestUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "conferencerequest FOR EACH STATEMENT EXECUTE FUNCTION notifyConferenceRequest()")
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
      dbListener.prepareStatement("LISTEN conferencerequest").execute();
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
        System.out.println("[ConferenceRequestDAO.verifyLocalTable]: Notification received!");
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
  public boolean insertEntry(ConferenceRequest entry) {

    // Check if the entry already exists. Update instead?
    if (tableMap.containsKey(entry.getConferenceRequestID())) return false;

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
    tableMap.put(entry.getConferenceRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(ConferenceRequest entry) {

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getConferenceRequestID()), entry, DataEditType.UPDATE),
        true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getConferenceRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[ConferenceRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime conferenceRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(conferenceRequestID)) {
      System.out.println(
          "[ConferenceRequestDAO.removeEntry]: Identifier "
              + conferenceRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    ConferenceRequest entry = tableMap.get(conferenceRequestID);

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
    tableMap.remove(entry.getConferenceRequestID(), entry);

    return true;
  }

  @Override
  public ConferenceRequest getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[ConferenceRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime conferenceRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(conferenceRequestID)) {
      System.out.println(
          "[ConferenceRequestDAO.getEntry]: Identifier "
              + conferenceRequestID
              + " does not exist in local table.");
      return null;
    }

    // Return entry objects from local table
    return tableMap.get(conferenceRequestID);
  }

  @Override
  public ArrayList<ConferenceRequest> getAllEntries() {

    verifyLocalTable();

    ArrayList<ConferenceRequest> allConferenceRequests = new ArrayList<>();

    // Add all entries in local table to a list
    for (LocalDateTime conferenceRequestID : tableMap.keySet()) {
      allConferenceRequests.add(tableMap.get(conferenceRequestID));
    }

    return allConferenceRequests;
  }

  @Override
  public void undoChange() {
    DataEdit<ConferenceRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getConferenceRequestID());
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
          "[ConferenceRequestDAO.updateDatabase]: Updating database in "
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
        DataEdit<ConferenceRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[ConferenceRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " ConferenceRequest "
                + dataEdit.getNewEntry().getConferenceRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new entry's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getConferenceRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getNotes());
            preparedInsert.setString(3, dataEdit.getNewEntry().getUsername());
            preparedInsert.setString(4, dataEdit.getNewEntry().getStartTime());
            preparedInsert.setString(5, dataEdit.getNewEntry().getEndTime());
            preparedInsert.setString(6, dataEdit.getNewEntry().getRecurringOption().toString());
            preparedInsert.setInt(7, dataEdit.getNewEntry().getNumAttendees());
            preparedInsert.setString(8, dataEdit.getNewEntry().getRoomName());

            // Execute the query
            preparedInsert.executeUpdate();

            break;

          case UPDATE:

            // Put the new entry's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getConferenceRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getUsername());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getStartTime());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getEndTime());
            preparedUpdate.setString(6, dataEdit.getNewEntry().getRecurringOption().toString());
            preparedUpdate.setInt(7, dataEdit.getNewEntry().getNumAttendees());
            preparedUpdate.setString(8, dataEdit.getNewEntry().getRoomName());
            preparedUpdate.setTimestamp(
                9, Timestamp.valueOf(dataEdit.getNewEntry().getConferenceRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getConferenceRequestID()));

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark entry in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getConferenceRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[ConferenceRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[ConferenceRequestDAO.updateDatabase]: Finished updating database in "
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

        ConferenceRequest cr =
            new ConferenceRequest(
                LocalDateTime.parse(parts[0]),
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                Recurring.valueOf(parts[5]),
                Integer.parseInt(parts[6]),
                parts[7]);

        tableMap.put(cr.getConferenceRequestID(), cr);

        insertPS.setTimestamp(1, Timestamp.valueOf(cr.getConferenceRequestID()));
        insertPS.setString(2, cr.getNotes());
        insertPS.setString(3, cr.getUsername());
        insertPS.setString(4, cr.getStartTime());
        insertPS.setString(5, cr.getEndTime());
        insertPS.setString(6, cr.getRecurringOption().toString());
        insertPS.setInt(7, cr.getNumAttendees());
        insertPS.setString(8, cr.getRoomName());

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

      for (Map.Entry<LocalDateTime, ConferenceRequest> entry : tableMap.entrySet()) {
        ConferenceRequest cr = entry.getValue();
        out.println(
            cr.getConferenceRequestID()
                + ","
                + cr.getNotes()
                + ","
                + cr.getUsername()
                + ","
                + cr.getStartTime()
                + ","
                + cr.getEndTime()
                + ","
                + cr.getRecurringOption().toString()
                + ","
                + cr.getNumAttendees()
                + ","
                + cr.getRoomName());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
