package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.ConnectionBuilder;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.TableEntry.SignagePreset;
import edu.wpi.fishfolk.ui.Sign;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.postgresql.PGConnection;
import org.postgresql.util.PSQLException;

public class SignagePresetDAO implements IDAO<SignagePreset>, ICSVWithSubtable {

  private final Connection dbConnection;
  private Connection dbListener;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<String, SignagePreset> tableMap;
  private final DataEditQueue<SignagePreset> dataEditQueue;

  /** DAO for Signage Preset table in PostgreSQL database. */
  public SignagePresetDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "signagepreset";
    this.headers = new ArrayList<>(List.of("presetname", "startdate", "kiosk", "signs"));
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
                + " (presetname VARCHAR(256),"
                + "startdate DATE,"
                + "kiosk VARCHAR(256),"
                + "signs SERIAL"
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

      // Prepare SQL query to select all signage presets from the db table
      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";
      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      // Execute the query
      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      // For each in the results, create a new SignagePreset object and put it in the local table
      while (results.next()) {

        SignagePreset signagePreset =
            new SignagePreset(
                results.getString(headers.get(0)),
                results.getDate(headers.get(1)).toLocalDate(),
                getSubtableItems(results.getInt(headers.get(2))),
                results.getString(headers.get(3)));

        tableMap.put(signagePreset.getName(), signagePreset);
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
        System.out.println("[SignagePresetDAO.prepareListener]: Listener is null.");
        return;
      }

      // Create a function that calls NOTIFY when the table is modified
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE FUNCTION notifySignagePreset() RETURNS TRIGGER AS $signagepreset$"
                  + "BEGIN "
                  + "NOTIFY signagepreset;"
                  + "RETURN NULL;"
                  + "END; $signagepreset$ language plpgsql")
          .execute();

      // Create a trigger that calls the function on any change
      dbListener
          .prepareStatement(
              "CREATE OR REPLACE TRIGGER signagePresetUpdate AFTER UPDATE OR INSERT OR DELETE ON "
                  + "signagepreset FOR EACH STATEMENT EXECUTE FUNCTION notifySignagePreset()")
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
      dbListener.prepareStatement("LISTEN signagepreset").execute();
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
        System.out.println("[SignagePresetDAO.verifyLocalTable]: Notification received!");
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
  public boolean insertEntry(SignagePreset entry) {

    // Check if the entry already exists.
    if (tableMap.containsKey(entry.getName())) return updateEntry(entry);

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
    tableMap.put(entry.getName(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(SignagePreset entry) {

    // Check if the entry already exists.
    if (!tableMap.containsKey(entry.getName())) return false;

    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getName()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getName(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[SignagePresetDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    String signagePresetName = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(signagePresetName)) {
      System.out.println(
          "[SignagePresetDAO.removeEntry]: Identifier "
              + signagePresetName
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    SignagePreset entry = tableMap.get(signagePresetName);

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
    tableMap.remove(entry.getName(), entry);

    return true;
  }

  @Override
  public SignagePreset getEntry(Object identifier) {

    verifyLocalTable();

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[SignagePresetDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    String signagePresetName = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(signagePresetName)) {
      System.out.println(
          "[SignagePresetDAO.getEntry]: Identifier "
              + signagePresetName
              + " does not exist in local table.");
      return null;
    }

    // Return entry objects from local table
    return tableMap.get(signagePresetName);
  }

  @Override
  public ArrayList<SignagePreset> getAllEntries() {

    verifyLocalTable();

    ArrayList<SignagePreset> allSignagePresets = new ArrayList<>();

    // Add all entries in local table to a list
    for (String signagePresetName : tableMap.keySet()) {
      allSignagePresets.add(tableMap.get(signagePresetName));
    }

    return allSignagePresets;
  }

  @Override
  public void undoChange() {
    DataEdit<SignagePreset> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getName());
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
          "[SignagePresetDAO.updateDatabase]: Updating database in "
              + Thread.currentThread().getName());

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
              + " = ? WHERE "
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
        DataEdit<SignagePreset> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[SignagePresetDAO.updateDatabase]: "
                + dataEdit.getType()
                + " SignagePreset "
                + dataEdit.getNewEntry().getName());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new entry's data into the prepared query
            preparedInsert.setString(1, dataEdit.getNewEntry().getName());
            preparedInsert.setDate(2, Date.valueOf(dataEdit.getNewEntry().getDate()));
            preparedInsert.setString(3, dataEdit.getNewEntry().getKiosk());

            // Execute the query
            preparedInsert.executeUpdate();
            setSubtableItems(dataEdit.getNewEntry().getName(), dataEdit.getNewEntry().getSigns());

            break;

          case UPDATE:

            // Put the new entry's data into the prepared query
            preparedUpdate.setString(1, dataEdit.getNewEntry().getName());
            preparedUpdate.setDate(2, Date.valueOf(dataEdit.getNewEntry().getDate()));
            preparedUpdate.setString(3, dataEdit.getNewEntry().getKiosk());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getName());

            // Execute the query
            preparedUpdate.executeUpdate();
            setSubtableItems(dataEdit.getNewEntry().getName(), dataEdit.getNewEntry().getSigns());

            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setString(1, dataEdit.getNewEntry().getName());

            // Execute the query
            preparedRemove.executeUpdate();
            deleteAllSubtableItems(dataEdit.getNewEntry().getName());

            break;
        }

        // Mark entry in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getName(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[SignagePresetDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[SignagePresetDAO.updateDatabase]: Finished updating database in "
            + Thread.currentThread().getName());

    // On success
    return true;
  }

  @Override
  public boolean importCSV(String tableFilepath, String subtableFilepath, boolean backup) {
    String[] pathArr = tableFilepath.split("/");

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
                  + "ALTER SEQUENCE signagepreset_signs_seq RESTART WITH 1;");

      // Get map representation of subtable
      HashMap<Integer, Sign[]> subtable = importSubtable(subtableFilepath);

      // skip first row of csv which has the headers
      String line = br.readLine();

      String insert =
          "INSERT INTO " + dbConnection.getSchema() + "." + this.tableName + " VALUES (?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        SignagePreset signagePreset =
            new SignagePreset(
                parts[0],
                LocalDate.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE),
                subtable.get(Integer.parseInt(parts[2])),
                parts[3]);

        tableMap.put(signagePreset.getName(), signagePreset);

        insertPS.setString(1, signagePreset.getName());
        insertPS.setDate(2, Date.valueOf(signagePreset.getDate()));
        insertPS.setString(3, signagePreset.getKiosk());

        insertPS.executeUpdate();

        setSubtableItems(signagePreset.getName(), signagePreset.getSigns());
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

    try {

      String tableFilename =
          tableName + "_" + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm")) + ".csv";

      PrintStream tableOut =
          new PrintStream(new FileOutputStream(directory + "\\" + tableFilename));

      tableOut.println(String.join(",", headers));

      for (Map.Entry<String, SignagePreset> entry : tableMap.entrySet()) {
        SignagePreset signagePreset = entry.getValue();
        tableOut.println(
            signagePreset.getName()
                + ","
                + signagePreset.getDate()
                + ","
                + getSubtableItemsID(signagePreset.getName())
                + ","
                + signagePreset.getKiosk());
      }

      tableOut.close();

      exportSubtable(directory);

      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * Checks for the existence of the proper subtable, and creates one if none exist.
   *
   * @param drop Choose whether to drop the subtable and create a new one on init
   */
  public void initSubtable(boolean drop) {

    try {

      // STEP 1: Check if the subtable exists
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + dbConnection.getSchema()
              + "' AND tablename = '"
              + tableName
              + "signs"
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      // STEP 2: If instructed to drop the table, drop it
      if (drop) {
        query = "DROP TABLE " + dbConnection.getSchema() + "." + tableName + "signs" + ";";
        statement.execute(query);
      }

      // STEP 3: If it does not exist OR the table was dropped, make it exist
      if (!results.getBoolean("exists") || drop) {
        query =
            "CREATE TABLE "
                + tableName
                + "signs"
                + " ("
                + "signkey INT,"
                + "signroom VARCHAR(256),"
                + "signdirection REAL,"
                + "signsubtext VARCHAR(256),"
                + "signindex INT"
                + ");";
        statement.executeUpdate(query);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Queries the table to determine the key used in the main table column tied to the subtable.
   *
   * @param requestID The ID of the item
   * @return The ID for this food order's items in the subtable
   */
  public int getSubtableItemsID(String requestID) {

    try {

      // Setup query to read ID stored in tied column of main table
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT signs FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE presetname = '"
              + requestID
              + "';";

      // Run that shit
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // Return the stored ID
      results.next();
      return results.getInt("signs");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      // Possible TODO: Check for ID = -1
      return -1;
    }
  }

  /**
   * Gets a list of items in the subtable with matching subtableIDs.
   *
   * @param subtableID ID in the subtable to query for
   * @return A list of all the items with a matching subtableID
   */
  public Sign[] getSubtableItems(int subtableID) {

    try {

      // Create empty list so store all items
      Sign[] presetArray = new Sign[8];

      // Query the subtable to return only items with the specified subtableID
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "signs "
              + "WHERE signkey = '"
              + subtableID
              + "';";

      // Run the query
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      // For each result, create a new food item and put it in the list
      while (results.next()) {
        int index = results.getInt("signindex");
        presetArray[index] =
            new Sign(
                results.getString("signroom"),
                results.getDouble("signdirection"),
                results.getString("signsubtext"));
      }

      // Return the list
      return presetArray;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Reset the items in the subtable for a specified request to a given list of "new" items.
   *
   * @param requestID ID of the request to do the replacement for
   * @param signs Sign array to do the replacement with
   */
  public void setSubtableItems(String requestID, Sign[] signs) {

    try {

      // Remove all food items in the subtable with a matching subtableID
      deleteAllSubtableItems(requestID);

      // Query to insert one new of subtable entries
      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + "signs"
              + " VALUES (?, ?, ?, ?, ?);";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);

      // Run the query for each item to insert
      for (int i = 0; i < 8; i++) {
        if (signs[i] != null) {
          preparedInsert.setInt(1, getSubtableItemsID(requestID));
          preparedInsert.setString(2, signs[i].getLabel());
          preparedInsert.setDouble(3, signs[i].getDirection());
          preparedInsert.setString(4, signs[i].getSubtext());
          preparedInsert.setInt(5, i);
          preparedInsert.executeUpdate();
        }
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Deletes all items in the subtable for a given request.
   *
   * @param requestID ID of the request to remove subtable items from
   */
  public void deleteAllSubtableItems(String requestID) {

    try {

      // Query to delete items with matching subtableID
      String query =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + "signs"
              + " WHERE signkey = ?;";

      PreparedStatement preparedDeleteAll = dbConnection.prepareStatement(query);

      // Setup the statement with subtableID and run it
      preparedDeleteAll.setInt(1, getSubtableItemsID(requestID));
      preparedDeleteAll.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public HashMap<Integer, Sign[]> importSubtable(String subtableFilepath) {

    // Create empty output
    HashMap<Integer, Sign[]> subtable = new HashMap<>();

    // Create buffered reader for CSV
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(subtableFilepath)))) {

      // Delete the old data
      dbConnection
          .createStatement()
          .executeUpdate(
              "DELETE FROM " + dbConnection.getSchema() + "." + tableName + "signs" + ";");

      // Iterate through each line of the CSV
      String line = br.readLine();
      while ((line = br.readLine()) != null) {

        // Split the input row into parts
        String[] parts = line.split(",");

        // Read the item ID (table link)
        int itemID = Integer.parseInt(parts[0]);

        // Make new array entry from row
        Sign sign = new Sign(parts[1], Double.parseDouble(parts[2]), parts[3]);

        // If there is no array at the item ID in the map, make one
        if (!subtable.containsKey(itemID)) {
          subtable.put(itemID, new Sign[8]);
        }

        // Add the entry to the array at the item ID in the map
        subtable.get(itemID)[Integer.parseInt(parts[4])] = sign;
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
              + "signs"
              + "_"
              + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm"))
              + ".csv";

      PrintStream subtableOut =
          new PrintStream(new FileOutputStream(directory + "\\" + subtableFilename));

      subtableOut.println("signkey,signroom,signdirection,signsubject,signindex");

      for (Map.Entry<String, SignagePreset> entry : tableMap.entrySet()) {

        Sign[] signagePresets = entry.getValue().getSigns();
        int subtableID = getSubtableItemsID(entry.getValue().getName());

        for (int i = 0; i < 8; i++) {

          if (signagePresets[i] != null) {

            subtableOut.println(
                subtableID
                    + ","
                    + signagePresets[i].getLabel()
                    + ","
                    + signagePresets[i].getDirection()
                    + ","
                    + signagePresets[i].getSubtext()
                    + ","
                    + i);
          }
        }
      }

      subtableOut.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
