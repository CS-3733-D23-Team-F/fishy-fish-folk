package edu.wpi.fishfolk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Fdb {

  public Connection connect(String dbName, String dbUser, String dbPass) {
    Connection db = null;
    String dbServer = "jdbc:postgresql://database.cs.wpi.edu:5432/";
    try {
      Class.forName("org.postgresql.Driver");
      db = DriverManager.getConnection(dbServer + dbName, dbUser, dbPass);
      if (db != null) {
        System.out.println("Connection established!");
      } else {
        System.out.println("Connection failed!");
      }
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
    return db;
  }

  public void disconnect(Connection db) {
    try {
      db.close();
      System.out.println("Connection closed!");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
