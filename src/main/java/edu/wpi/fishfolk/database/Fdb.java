package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.edit.DataEdit;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** @author Christian */
public class Fdb {

  public Table micronodeTable;
  public Table locationTable;
  public Table moveTable;
  public Table edgeTable;
  public Table userAccountTable;

  public Connection conn;

  private HashSet<String> freeIDs;

  public Fdb() {

    this.conn = connect("teamfdb", "teamf", "teamf60");

    initialize();

    int maxID = 4000;

    freeIDs = new HashSet<>((maxID - 100) / 5 * 4 / 3 + 1); // about 780 to start

    // add all ids from 100 -> 3000 counting by 5
    for (int i = 100; i <= maxID; i += 5) {
      freeIDs.add(Integer.toString(i));
    }

    // remove the ids already in the database
    micronodeTable
        .getColumn("id")
        .forEach(
            id -> {
              freeIDs.remove(id);
            });

    System.out.println("num ids left " + freeIDs.size());
  }

  public boolean processEdit(DataEdit edit) {

    // handle ids
    switch (edit.type) {
      case INSERT:
        // edit.id = getNextID();
        break;

      case REMOVE:
        freeID(edit.id);
        break;
    }

    switch (edit.table) {
      case MICRONODE:
        return micronodeTable.update(edit);

      case LOCATION:
        return locationTable.update(edit);

      case MOVE:
        return moveTable.update(edit);

      case EDGE:
        return edgeTable.update(edit);
    }

    return false;
  }

  public String getNextID() {
    Iterator<String> itr = freeIDs.iterator();
    String id = itr.next();
    itr.remove();
    return id;
  }

  public void freeID(String id) {
    freeIDs.add(id);
  }

  /**
   * Initialize the class by connecting to the database and establishing objects for both the node
   * and edge tables. TODO: Database name, user, password and table names are hardcoded -> Make them
   * configurable
   */
  public void initialize() {

    try {

      // STEP 1: Connect to PostgreSQL database

      conn = connect("teamfdb", "teamf", "teamf60");
      conn.setSchema("iter1db");
      System.out.println("[Fdb.initialize]: Current schema: " + conn.getSchema() + ".");

      // STEP 2: Establish table objects

      micronodeTable = new Table(conn, "micronode");
      micronodeTable.init(false);
      micronodeTable.setHeaders(
          new ArrayList<>(List.of("id", "x", "y", "floor", "building")),
          new ArrayList<>(List.of("int", "double", "double", "String2", "String16")));

      locationTable = new Table(conn, "location");
      locationTable.init(false);
      locationTable.setHeaders(
          new ArrayList<>(List.of("longname", "shortname", "type")),
          new ArrayList<>(List.of("String64", "String64", "String4")));

      moveTable = new Table(conn, "move");
      moveTable.init(false);
      moveTable.setHeaders(
          new ArrayList<>(List.of("id", "longname", "date")),
          new ArrayList<>(List.of("int", "String64", "String16")));

      edgeTable = new Table(conn, "edge");
      edgeTable.init(false);
      edgeTable.setHeaders(
          new ArrayList<>(List.of("node1", "node2")),
          new ArrayList<>(List.of("String8", "String8")));

      userAccountTable = new Table(conn, "useraccounts");
      userAccountTable.init(false);
      userAccountTable.setHeaders(
              new ArrayList<>(List.of("username", "passhash", "permissions")),
              new ArrayList<>(List.of("String64", "int", "String64")));

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Connect to a PostgreSQL database.
   *
   * @param dbName Database name
   * @param dbUser Account
   * @param dbPass Password
   * @return Database connection object (null if no connection is made)
   */
  private Connection connect(String dbName, String dbUser, String dbPass) {
    Connection db = null;
    String dbServer = "jdbc:postgresql://database.cs.wpi.edu:5432/";
    try {
      Class.forName("org.postgresql.Driver");
      db = DriverManager.getConnection(dbServer + dbName, dbUser, dbPass);
      if (db != null) {
        System.out.println("[Fdb.connect]: Connection established.");
      } else {
        System.out.println("[Fdb.connect]: Connection failed.");
      }
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
    return db;
  }

  /** Disconnect from PostgreSQL database. */
  public void disconnect() {
    try {
      conn.close();
      System.out.println("[Fdb.disconnect]: Connection closed.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Check if table exists within database.
   *
   * @param db Database connection
   * @param tbName Table name
   * @return True if table exists in database
   */
  private boolean tableExists(Connection db, String tbName) {
    Statement statement;
    try {
      statement = db.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + db.getSchema()
              + "' AND tablename = '"
              + tbName
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();
      return results.getBoolean("exists");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public int getMostRecentUNode(String longname) {
    ArrayList<String[]> results =
        moveTable.executeQuery("SELECT id, date", "WHERE longname = '" + longname + "';");

    results.forEach(
        res -> {
          res[1] = Move.sanitizeDate(res[1]);
          System.out.println(Arrays.toString(res));
        });

    // sort results based on date
    results.sort(Comparator.comparing(result -> LocalDate.parse(result[1], Move.format)));

    // extract id from last result
    return Integer.parseInt(results.get(results.size() - 1)[0]);
  }

  public ArrayList<String[]> getMostRecentLocations(String id) {

    ArrayList<String[]> results =
        moveTable.executeQuery("SELECT longname, date", "WHERE id = '" + id + "';");

    results.forEach(
        res -> {
          res[1] = Move.sanitizeDate(res[1]);
          System.out.println(Arrays.toString(res));
        });

    // sort results based on date
    results.sort(Comparator.comparing(result -> LocalDate.parse(result[1], Move.format)));

    ArrayList<String[]> locations = new ArrayList<>();

    for (String[] result : results) {
      locations.add(locationTable.get("longname", result[0]).toArray(new String[3]));
    }

    return locations;
  }

  /**
   * Perform a join operation on two tables along the match column. The returned ArrayList contains
   * the requested headers in the first index and each element afterwards represents one row, not
   * including
   *
   * @param selectHeaders
   * @param left
   * @param right
   * @param match
   * @return
   */
  /*
  public ArrayList<String[]> join(String[] selectHeaders, DataTableType left, DataTableType right, String match){



  }

   */

  public void loadTablesFromCSV() {
    micronodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/MicroNode.csv", false);
    locationTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Location.csv", false);
    moveTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Move.csv", false);
    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Edge.csv", false);
  }
}
