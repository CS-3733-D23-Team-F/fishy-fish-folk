package edu.wpi.fishfolk.database;

import java.sql.*;

/** @author Christian */
public class Fdb {

  /**
   * Connect to a PostgreSQL database.
   *
   * @param dbName Database name
   * @param dbUser Account
   * @param dbPass Password
   * @return Database connection object (null if no connection is made)
   */
  public Connection connect(String dbName, String dbUser, String dbPass) {
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

  /**
   * Disconnect from PostgreSQL database.
   *
   * @param db Database connection
   */
  public void disconnect(Connection db) {
    try {
      db.close();
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
  public boolean tableExists(Connection db, String tbName) {
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

  /**
   * Creates a new empty table within database if it doesn't exist.
   *
   * @param db Database connection
   * @param tbName Table name
   * @return True if a new table is created
   */
  public boolean createTable(Connection db, String tbName) {
    Statement statement;
    try {
      statement = db.createStatement();
      if (tableExists(db, tbName)) {
        System.out.println("[Fdb.createTable]: Table " + tbName + " already exists.");
        return false;
      } else {
        String query = "CREATE TABLE " + tbName + " ();";
        statement.executeUpdate(query);
        System.out.println("[Fdb.createTable]: Table " + tbName + " created.");
        return true;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }
}
