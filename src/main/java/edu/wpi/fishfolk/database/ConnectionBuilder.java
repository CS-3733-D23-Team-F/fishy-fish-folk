package edu.wpi.fishfolk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.NonNull;

public class ConnectionBuilder {

  private static final String username = "teamf";
  private static final String password = "teamf60";
  private static final String dbName = "teamfdb";
  private static String schema = "testing";
  private static final String URL = "jdbc:postgresql://database.cs.wpi.edu:5432/";

  /**
   * Creates a new connection to the database with preset parameters.
   *
   * @return The new connection
   */
  public static Connection buildConnection() {
    try {
      Class.forName("org.postgresql.Driver");
      Connection c = DriverManager.getConnection(URL + dbName, username, password);
      c.setSchema(schema);
      return c;
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Changes the schema of the database connection.
   *
   * @param schema New schema to use
   */
  public static void setSchema(@NonNull String schema) {
    ConnectionBuilder.schema = schema;
  }
}
