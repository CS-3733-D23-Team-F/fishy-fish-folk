package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.TableEntry.Alert;
import edu.wpi.fishfolk.util.AlertType;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.postgresql.PGConnection;
import org.postgresql.util.PSQLException;

public class AlertDAO implements IDAO<Alert> {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, Alert> alerts = new HashMap<>();
  private final DataEditQueue<Alert> dataEditQueue = new DataEditQueue<>();

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /** DAO for Alert table in PostgreSQL database. */
  public AlertDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "alert";
    this.headers =
        new ArrayList<>(List.of("timestamp", "username", "longname", "date", "text", "type"));
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
                + " (timestamp TIMESTAMP PRIMARY KEY," // compatible with LocalDataTime
                + " username VARCHAR(32)," // same as in accounts table
                + " longname VARCHAR(64)," // same as in location table
                + " date DATE," // same as in move table
                + " text VARCHAR(256),"
                + " type VARCHAR(8));";
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

      // Create a new Alert object for each result and put it in the local table
      while (results.next()) {

        Alert alert = null;
        LocalDateTime timestamp = LocalDateTime.parse(results.getString(1), formatter);

        switch (AlertType.valueOf(results.getString("type"))) {
          case MOVE:
            alert =
                new Alert(
                    timestamp,
                    results.getString("username"),
                    results.getString("longname"),
                    LocalDate.parse(results.getString("date")),
                    results.getString("text"));
            break;

          case OTHER:
            alert = new Alert(timestamp, results.getString("username"), results.getString("text"));
        }

        alerts.put(alert.getTimestamp(), alert);
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
        System.out.println("[AlertDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifyAlert() RETURNS TRIGGER AS $alert$"
                  + "BEGIN "
                  + "NOTIFY alert;"
                  + "RETURN NULL;"
                  + "END; $alert$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER alertUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "alert FOR EACH STATEMENT EXECUTE FUNCTION notifyAlert()")
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
      dbListener.prepareStatement("LISTEN alert").execute();
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
        System.out.println("[AlertDAO.verifyLocalTable]: Notification received!");
        alerts.clear();
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
  public boolean insertEntry(Alert entry) {

    // Check if the entry already exists.
    if (alerts.containsKey(entry.getTimestamp())) return false;

    // Mark entry Alert status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Store alert in list
    alerts.put(entry.getTimestamp(), entry);

    return true;
  }

  // currently no way of updating alerts
  @Override
  public boolean updateEntry(Alert entry) {
    return false;
  }

  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    // must be edge
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[AlertDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime timestamp = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!alerts.containsKey(timestamp)) {
      System.out.println(
          "[AlertDAO.removeEntry]: Identifier " + identifier + " does not exist in local table.");
      return false;
    }

    // get entry from local table
    Alert entry = alerts.get(timestamp);

    // Mark edge status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push a REMOVE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.REMOVE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Remove edge from set
    alerts.remove(entry.getTimestamp());

    return true;
  }

  @Override
  public Alert getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println("[AlertDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    // remember that moveID is the longame concatenated with the move date
    LocalDateTime timestamp = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!alerts.containsKey(timestamp)) {
      System.out.println(
          "[AlertDAO.getEntry]: Identifier " + timestamp + " does not exist in local table.");
      return null;
    }

    // Return Move object from local table
    return alerts.get(timestamp);
  }

  @Override
  public ArrayList<Alert> getAllEntries() {

    verifyLocalTable();

    ArrayList<Alert> allAlerts = new ArrayList<>();

    // Add all Moves in local table to a list
    for (LocalDateTime timestamp : alerts.keySet()) {
      allAlerts.add(alerts.get(timestamp));
    }

    Collections.sort(
        allAlerts, (alert1, alert2) -> alert1.getTimestamp().compareTo(alert2.getTimestamp()));

    return allAlerts;
  }

  @Override
  public void undoChange() {

    // Pop the top item of the data edit stack
    DataEdit<Alert> dataEdit = dataEditQueue.popRecent();

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
          "[AlertDAO.updateDatabase]: Updating database in " + Thread.currentThread().getName());

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
        DataEdit<Alert> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[AlertDAO.updateDatabase]: "
                + dataEdit.getType()
                + " Alert "
                + dataEdit.getNewEntry().toString());

        Alert newAlertEntry = dataEdit.getNewEntry();

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Edge's data into the prepared query
            preparedInsert.setTimestamp(1, Timestamp.valueOf(newAlertEntry.getTimestamp()));
            preparedInsert.setString(2, newAlertEntry.getUsername());
            preparedInsert.setString(3, newAlertEntry.getLongName());
            preparedInsert.setDate(4, Date.valueOf(newAlertEntry.getDate()));
            preparedInsert.setString(5, newAlertEntry.getText());
            preparedInsert.setString(6, newAlertEntry.getType().toString());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Node's data into the prepared query
            preparedUpdate.setTimestamp(1, Timestamp.valueOf(newAlertEntry.getTimestamp()));
            preparedUpdate.setString(2, newAlertEntry.getUsername());
            preparedUpdate.setString(3, newAlertEntry.getLongName());
            preparedUpdate.setDate(4, Date.valueOf(newAlertEntry.getDate()));
            preparedUpdate.setString(5, newAlertEntry.getText());
            preparedUpdate.setString(6, newAlertEntry.getType().toString());
            preparedUpdate.setTimestamp(7, Timestamp.valueOf(newAlertEntry.getTimestamp()));

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Edge's data into the prepared query
            preparedRemove.setTimestamp(1, Timestamp.valueOf(newAlertEntry.getTimestamp()));

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Alert in local table as OLD
        alerts.remove(newAlertEntry.getTimestamp());
        newAlertEntry.setStatus(EntryStatus.OLD);
        alerts.put(newAlertEntry.getTimestamp(), newAlertEntry);
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[AlertDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[AlertDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  public Alert getLatestAlert() {
    Iterator<LocalDateTime> itr = alerts.keySet().iterator();

    if (!itr.hasNext()) {
      return null;
    }

    LocalDateTime last = itr.next();

    while (itr.hasNext()) {
      LocalDateTime curr = itr.next();
      if (curr.isAfter(last)) {
        last = curr;
      }
    }

    return alerts.get(last);
  }
}
