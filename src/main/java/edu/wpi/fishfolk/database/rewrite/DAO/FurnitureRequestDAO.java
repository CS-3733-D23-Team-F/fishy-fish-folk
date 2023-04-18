package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.DataEditQueue;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.ServiceType;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FurnitureRequest;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.FurnitureItem;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureRequestDAO implements IDAO<FurnitureRequest> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<LocalDateTime, FurnitureRequest> tableMap;
  private final DataEditQueue<FurnitureRequest> dataEditQueue;

  /** DAO for Furniture Request table in PostgreSQL database. */
  public FurnitureRequestDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "furniturerequest";
    this.headers =
        new ArrayList<>(
            List.of(
                "id",
                "assignee",
                "status",
                "notes",
                "item",
                "servicetype",
                "roomnumber",
                "deliverydate"));
    tableMap = new HashMap<>();
    dataEditQueue = new DataEditQueue<>();

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
                + "item VARCHAR(64),"
                + "servicetype VARCHAR(64),"
                + "roomNumber VARCHAR(64),"
                + "deliverydate TIMESTAMP"
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

        FurnitureRequest furnitureRequest =
            new FurnitureRequest(
                results.getTimestamp(headers.get(0)).toLocalDateTime(),
                results.getString(headers.get(1)),
                FormStatus.valueOf(results.getString(headers.get(2))),
                results.getString(headers.get(3)),
                new FurnitureItem(results.getString(headers.get(4))),
                    ServiceType.valueOf(results.getString(headers.get(5))),
                results.getString(headers.get(6)),
                results.getTimestamp(headers.get(7)).toLocalDateTime());
        tableMap.put(furnitureRequest.getFurnitureRequestID(), furnitureRequest);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean insertEntry(FurnitureRequest entry) {
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
    tableMap.put(entry.getFurnitureRequestID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(FurnitureRequest entry) {
    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getFurnitureRequestID()), entry, DataEditType.UPDATE),
        true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getFurnitureRequestID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FurnitureRequestDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    LocalDateTime furnitureRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(furnitureRequestID)) {
      System.out.println(
          "[FurnitureRequestDAO.removeEntry]: Identifier "
              + furnitureRequestID
              + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    FurnitureRequest entry = tableMap.get(furnitureRequestID);

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
    tableMap.remove(entry.getFurnitureRequestID(), entry);

    return true;
  }

  @Override
  public FurnitureRequest getEntry(Object identifier) {
    // Check if input identifier is correct type
    if (!(identifier instanceof LocalDateTime)) {
      System.out.println(
          "[FurnitureRequestDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    LocalDateTime furnitureRequestID = (LocalDateTime) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(furnitureRequestID)) {
      System.out.println(
          "[FurnitureRequestDAO.getEntry]: Identifier "
              + furnitureRequestID
              + " does not exist in local table.");
      return null;
    }

    // Return entry objects from local table
    return tableMap.get(furnitureRequestID);
  }

  @Override
  public ArrayList<FurnitureRequest> getAllEntries() {
    ArrayList<FurnitureRequest> allFurnitureRequests = new ArrayList<>();

    // Add all entries in local table to a list
    for (LocalDateTime furnitureRequestID : tableMap.keySet()) {
      allFurnitureRequests.add(tableMap.get(furnitureRequestID));
    }

    return allFurnitureRequests;
  }

  @Override
  public void undoChange() { // Pop the top item of the data edit stack
    DataEdit<FurnitureRequest> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getFurnitureRequestID());
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
          "[FurnitureRequestDAO.updateDatabase]: Updating database in "
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
        DataEdit<FurnitureRequest> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[FurnitureRequestDAO.updateDatabase]: "
                + dataEdit.getType()
                + " FurnitureRequest "
                + dataEdit.getNewEntry().getFurnitureRequestID());

        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new entry's data into the prepared query
            preparedInsert.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFurnitureRequestID()));
            preparedInsert.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedInsert.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedInsert.setString(4, dataEdit.getNewEntry().getNotes());
            preparedInsert.setString(5, dataEdit.getNewEntry().getItem().furnitureName);
            preparedInsert.setString(6, dataEdit.getNewEntry().getServiceType().toString());
            preparedInsert.setString(7, dataEdit.getNewEntry().getRoomNumber());
            preparedInsert.setTimestamp(
                8, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryDate()));

            // Execute the query
            preparedInsert.executeUpdate();

            break;

          case UPDATE:

            // Put the new entry's data into the prepared query
            preparedUpdate.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFurnitureRequestID()));
            preparedUpdate.setString(2, dataEdit.getNewEntry().getAssignee());
            preparedUpdate.setString(3, dataEdit.getNewEntry().getFormStatus().toString());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getNotes());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getItem().furnitureName);
            preparedUpdate.setString(6, dataEdit.getNewEntry().getServiceType().toString());
            preparedUpdate.setString(7, dataEdit.getNewEntry().getRoomNumber());
            preparedUpdate.setTimestamp(
                8, Timestamp.valueOf(dataEdit.getNewEntry().getDeliveryDate()));
            preparedUpdate.setTimestamp(
                9, Timestamp.valueOf(dataEdit.getNewEntry().getFurnitureRequestID()));

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new entry's data into the prepared query
            preparedRemove.setTimestamp(
                1, Timestamp.valueOf(dataEdit.getNewEntry().getFurnitureRequestID()));

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark entry in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getFurnitureRequestID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[FurnitureRequestDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[FurnitureRequestDAO.updateDatabase]: Finished updating database in "
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

        FurnitureRequest fr =
            new FurnitureRequest(
                LocalDateTime.parse(parts[0]),
                parts[1],
                FormStatus.valueOf(parts[2]),
                parts[3],
                new FurnitureItem(parts[4]),
                ServiceType.valueOf(parts[5]),
                parts[6],
                LocalDateTime.parse(parts[7]));

        tableMap.put(fr.getFurnitureRequestID(), fr);

        insertPS.setTimestamp(1, Timestamp.valueOf(fr.getFurnitureRequestID()));
        insertPS.setString(2, fr.getAssignee());
        insertPS.setString(3, fr.getFormStatus().toString());
        insertPS.setString(4, fr.getNotes());
        insertPS.setString(5, fr.getItem().furnitureName);
        insertPS.setString(6, fr.getServiceType().toString());
        insertPS.setString(7, fr.getRoomNumber());
        insertPS.setTimestamp(8, Timestamp.valueOf(fr.getDeliveryDate()));

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

      for (Map.Entry<LocalDateTime, FurnitureRequest> entry : tableMap.entrySet()) {
        FurnitureRequest fr = entry.getValue();
        out.println(
            fr.getFurnitureRequestID()
                + ","
                + fr.getAssignee()
                + ","
                + fr.getFormStatus()
                + ","
                + fr.getNotes()
                + ","
                + fr.getItem().furnitureName
                + ","
                + fr.getServiceType().toString()
                + ","
                + fr.getRoomNumber()
                + ","
                + fr.getDeliveryDate());
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
