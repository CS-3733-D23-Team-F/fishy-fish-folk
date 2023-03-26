package edu.wpi.fishfolk.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnections {

  public Connection connect(String dbName, String dbUser, String dbPass) {
    Connection connection = null;
    String dbServer = "jdbc:postgresql://database.cs.wpi.edu:5432/";
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(dbServer + dbName, dbUser, dbPass);
      if (connection != null) {
        System.out.println("Connection established!");
      } else {
        System.out.println("Connection failed!");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return connection;
  }
}
