package edu.wpi.fishfolk.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** @author Christian */
public class Fdb {

  // public NodeTable nodeTable;

  public Table micronodeTable;

  public Table locationTable;

  public Table moveTable;
  public Table edgeTable;

  public Connection conn;

  public Fdb() {

    this.conn = connect("teamfdb", "teamf", "teamf60");
    initialize();
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

  /** Disconnect from PostgreSQL database. */
  public void disconnect() {
    try {
      conn.close();
      System.out.println("[Fdb.disconnect]: Connection closed.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void loadTablesFromCSV() {
    micronodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/MicroNode.csv", false);
    locationTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Location.csv", false);
    moveTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Move.csv", false);
    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Edge.csv", false);
  }
}
