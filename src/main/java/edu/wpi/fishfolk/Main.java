package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.DbConnections;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) {
    // Fapp.launch(Fapp.class, args);
    DbConnections dbConnections = new DbConnections();
    Connection connection = dbConnections.connect("teamfdb", "teamf", "teamf60");
    try {
      connection.setSchema("test");
      System.out.println("Current schema: " + connection.getSchema());
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // shortcut: psvm

}
