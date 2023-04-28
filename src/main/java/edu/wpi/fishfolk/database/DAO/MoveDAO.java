package edu.wpi.fishfolk.database.DAO;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.database.IDAO;
import edu.wpi.fishfolk.database.IProcessEdit;
import edu.wpi.fishfolk.database.TableEntry.Move;
import edu.wpi.fishfolk.database.TableEntry.Node;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveDAO implements IDAO<Move>, IProcessEdit {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private final HashMap<String, Move> tableMap;
  private final DataEditQueue<Move> dataEditQueue;

  /** DAO for Move table in PostgreSQL database. */
  public MoveDAO(Connection dbConnection) {

    this.dbConnection = dbConnection;
    this.tableName = "move";
    this.headers = new ArrayList<>(List.of("id", "longname", "date"));
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
                + " (id SMALLINT," // 2 bytes: -2^15 to 2^15-1
                + " longname VARCHAR(64)," // 64 characters
                + " date DATE);"; // (day, month, year) in 4 bytes
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

      // For each Move in the results, create a new Move object and put it in the local table
      while (results.next()) {
        Move move =
            new Move(
                Integer.parseInt(results.getString(1)),
                results.getString(2),
                LocalDate.parse(results.getString(3)));
        tableMap.put(move.getMoveID(), move);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void processEdit(DataEdit<Object> edit) {
    switch(edit.getType()){
      case INSERT:
        insertEntry((Move) edit.getNewEntry());
        break;
      case REMOVE:
        removeEntry((String) edit.getNewEntry());
        break;
      case UPDATE:
        updateEntry((Move) edit.getNewEntry());
        break;
    }
  }

  @Override
  public boolean insertEntry(Move entry) {

    // Mark entry Move status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(new DataEdit<>(entry, DataEditType.INSERT), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Put entry Move in local table
    // MoveID is the longname concatenated with the date
    tableMap.put(entry.getMoveID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(Move entry) {
    // Mark entry status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an UPDATE to the data edit stack, update the db if the batch limit has been reached
    if (dataEditQueue.add(
        new DataEdit<>(tableMap.get(entry.getMoveID()), entry, DataEditType.UPDATE), true)) {

      // Reset edit count
      dataEditQueue.setEditCount(0);

      // Update database in separate thread
      Thread removeThread = new Thread(() -> updateDatabase(false));
      removeThread.start();
    }

    // Update entry in local table
    tableMap.put(entry.getMoveID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println(
          "[MoveDAO.removeEntry]: Invalid identifier " + identifier.toString() + ".");
      return false;
    }

    String moveID = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(moveID)) {
      System.out.println(
          "[MoveDAO.removeEntry]: Identifier " + moveID + " does not exist in local table.");
      return false;
    }

    // Get entry from local table
    Move entry = tableMap.get(moveID);

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

    // Remove entry Move from local table
    tableMap.remove(entry.getMoveID(), entry);

    return true;
  }

  @Override
  public Move getEntry(Object identifier) {

    // Check if input identifier is correct type
    if (!(identifier instanceof String)) {
      System.out.println("[MoveDAO.getEntry]: Invalid identifier " + identifier.toString() + ".");
      return null;
    }

    // remember that moveID is the longame concatenated with the move date
    String moveID = (String) identifier;

    // Check if local table contains identifier
    if (!tableMap.containsKey(moveID)) {
      System.out.println(
          "[MoveDAO.getEntry]: Identifier " + moveID + " does not exist in local table.");
      return null;
    }

    // Return Move object from local table
    return tableMap.get(moveID);
  }

  @Override
  public ArrayList<Move> getAllEntries() {

    ArrayList<Move> allMoves = new ArrayList<>();

    // Add all Moves in local table to a list
    for (String longName : tableMap.keySet()) {
      allMoves.add(tableMap.get(longName));
    }

    return allMoves;
  }

  @Override
  public void undoChange() {
    // Pop the top item of the data edit stack
    DataEdit<Move> dataEdit = dataEditQueue.popRecent();

    // Check if there is an update to be done
    if (dataEdit == null) return;

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry().getLongName());
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
          "[MoveDAO.updateDatabase]: Updating database in " + Thread.currentThread().getName());

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
              + " = ?, "
              + headers.get(2)
              + " = ? WHERE "
              + headers.get(1) // longname matches
              + " = ? AND "
              + headers.get(2) // and date matches
              + " = ?;";

      String remove =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " WHERE "
              + headers.get(1) // longname matches
              + " = ? AND "
              + headers.get(2) // and date matches
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
        DataEdit<Move> dataEdit = dataEditQueue.next();

        // Print update info (DEBUG)
        System.out.println(
            "[MoveDAO.updateDatabase]: "
                + dataEdit.getType()
                + " Move "
                + dataEdit.getNewEntry().getLongName());

        Move newMoveEntry = dataEdit.getNewEntry();
        // Change behavior based on data edit type
        switch (dataEdit.getType()) {
          case INSERT:

            // Put the new Move's data into the prepared query
            preparedInsert.setInt(1, newMoveEntry.getNodeID());
            preparedInsert.setString(2, newMoveEntry.getLongName());
            preparedInsert.setDate(3, Date.valueOf(newMoveEntry.getDate()));

            // Execute the query
            preparedInsert.executeUpdate();
            break;

          case UPDATE:

            // Put the new Move's data into the prepared query
            preparedUpdate.setInt(1, newMoveEntry.getNodeID());
            preparedUpdate.setString(2, newMoveEntry.getLongName());
            preparedUpdate.setDate(3, Date.valueOf(newMoveEntry.getDate()));
            // match both longname and date
            preparedUpdate.setString(4, newMoveEntry.getLongName());
            preparedUpdate.setDate(5, Date.valueOf(newMoveEntry.getDate()));

            // Execute the query
            preparedUpdate.executeUpdate();
            break;

          case REMOVE:

            // Put the new Move's data into the prepared query matching both longname and date
            preparedRemove.setString(1, newMoveEntry.getLongName());
            preparedRemove.setDate(2, Date.valueOf(newMoveEntry.getDate()));

            // Execute the query
            preparedRemove.executeUpdate();
            break;
        }

        // Mark Move in local table as OLD
        dataEdit.getNewEntry().setStatus(EntryStatus.OLD);
        tableMap.put(dataEdit.getNewEntry().getMoveID(), dataEdit.getNewEntry());
      }
    } catch (SQLException e) {

      // Print active thread error (DEBUG)
      System.out.println(
          "[MoveDAO.updateDatabase]: Error updating database in "
              + Thread.currentThread().getName());

      System.out.println(e.getMessage());
      return false;
    }

    // Print active thread end (DEBUG)
    System.out.println(
        "[MoveDAO.updateDatabase]: Finished updating database in "
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
          "INSERT INTO " + dbConnection.getSchema() + "." + this.tableName + " VALUES (?, ?, ?);";

      PreparedStatement insertPS = dbConnection.prepareStatement(insert);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      while ((line = br.readLine()) != null) {

        String[] parts = line.split(",");

        // ensure that date is in dd/MM/yyyy format. add leading zeroes where necessary

        Move m =
            new Move(
                Integer.parseInt(parts[0]),
                parts[1],
                LocalDate.parse(Move.sanitizeDate(parts[2]), formatter));

        tableMap.put(m.getMoveID(), m);

        insertPS.setInt(1, m.getNodeID());
        insertPS.setString(2, m.getLongName());
        insertPS.setDate(3, m.getSQLDate());

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

      // write date to CSV in same LocalDate format as used in import
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      for (Map.Entry<String, Move> entry : tableMap.entrySet()) {
        Move m = entry.getValue();
        out.println(m.getNodeID() + "," + m.getLongName() + "," + formatter.format(m.getDate()));
      }

      out.close();
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
