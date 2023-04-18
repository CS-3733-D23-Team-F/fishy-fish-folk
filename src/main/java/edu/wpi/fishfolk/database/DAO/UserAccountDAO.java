package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.TableEntry.UserAccount;
import edu.wpi.fishfolk.util.PermissionLevel;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccountDAO implements IDAO<UserAccount> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<String, UserAccount> tableMap;
  private final DataEditQueue<UserAccount> dataEditQueue;

  /** DAO for User Account Request table in PostgreSQL database. */
  public UserAccountDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "account";
    this.headers = new ArrayList<>(List.of("username", "password", "email", "level"));
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
                + " (username VARCHAR(32) PRIMARY KEY," // 32 characters
                + " password VARCHAR(32),"
                + " email VARCHAR(64),"
                + " level VARCHAR(8));"; // enum values are 4-5 chars
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

      // For each entry in the results, create a new object and put it in the local table
      while (results.next()) {
        UserAccount account =
            new UserAccount(
                results.getString(1),
                results.getString(2),
                results.getString(3),
                PermissionLevel.valueOf(results.getString(4)));

        tableMap.put(account.getUsername(), account);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(UserAccount entry) {

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

    // Put entry  in local table
    tableMap.put(entry.getUsername(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(UserAccount entry) {

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getUsername()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getUsername(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[UserAccountDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    String username = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(username)) {
      System.out.println(
          "[UserAccountDAO.removeEntry]: Identifier "
              + username
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    UserAccount entry = tableMap.get(username);

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
    tableMap.remove(entry.getUsername(), entry);

    return true;
  }

  @Override
  public UserAccount getEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[UserAccountDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    String username = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(username)) {
      System.out.println(
          "[UserAccountDAO.getEntry]: Identifier " + username + " does not exist in local table.");
      return null;
    }

    // Return UserAccount object from local table
    return tableMap.get(username);
  }

  @Override
  public ArrayList<UserAccount> getAllEntries() {

    ArrayList<UserAccount> all = new ArrayList<>();

    // Add all UserAccounts in local table to a list
    for (Map.Entry<String, UserAccount> entry : tableMap.entrySet()) {
      all.add(entry.getValue());
    }

    return all;
  }

  @Override
  public void undoChange() {

    // Pop the top item of the data edit stack
    DataEdit<UserAccount> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getUsername());
        break;

      case UPDATE:

        // UPDATE the entry if it was an UPDATE
        updateEntry(dataEdit.getOldEntry());
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
          "[UserAccountDAO.updateDatabase]: Updating database in "
              + Thread.currentThread().getName());

      // Prepare SQL queries for INSERT, UPDATE, and REMOVE actions
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?);";

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
        DataEdit<UserAccount> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[UserAccountDAO.updateDatabase]: "
                + dataEdit.getType()
                + " UserAccount "
                + dataEdit.getNewEntry().getUsername());

        UserAccount newAccountEntry = dataEdit.getNewEntry();

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new UserAccount's data into the prepared query
            preparedInsert.setString(1, newAccountEntry.getUsername());
            preparedInsert.setString(2, newAccountEntry.getPassword());
            preparedInsert.setString(3, newAccountEntry.getEmail());
            preparedInsert.setString(4, newAccountEntry.getLevel().toString());

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Node's data into the prepared query
            // Put the new Node's data into the prepared query
            preparedUpdate.setString(1, newAccountEntry.getUsername());
            preparedUpdate.setString(2, newAccountEntry.getPassword());
            preparedUpdate.setString(3, newAccountEntry.getEmail());
            preparedUpdate.setString(4, newAccountEntry.getLevel().toString());
            preparedUpdate.setString(5, newAccountEntry.getUsername());

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Node's data into the prepared query
            preparedRemove.setString(1, newAccountEntry.getUsername());

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Node in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getUsername(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[UserAccountDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[UserAccountDAO.updateDatabase]: Finished updating database in "
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
              + " VALUES (?, ?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        UserAccount account =
            new UserAccount(parts[0], parts[1], parts[2], PermissionLevel.valueOf(parts[3]));

        tableMap.put(account.getUsername(), account);

        insertPS.setString(1, account.getUsername());
        insertPS.setString(2, account.getPassword());
        insertPS.setString(3, account.getEmail());
        insertPS.setString(4, account.getLevel().toString());

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

      for (Map.Entry<String, UserAccount> entry : tableMap.entrySet()) {
        UserAccount acc = entry.getValue();
        out.println(
            acc.getUsername()
                + ","
                + acc.getPassword()
                + ","
                + acc.getEmail()
                + ","
                + acc.getLevel());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
