package edu.wpi.fishfolk.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import lombok.Getter;

public class Table implements ITable {

  private final Connection dbConnection;
  @Getter private final String tableName;
  private ArrayList<String> headers;
  private ArrayList<String> headerTypes;
  private int numHeaders;

  public Table(Connection conn, String name) {
    dbConnection = conn;
    tableName = name;
  }

  public boolean addHeaders(ArrayList<String> headers, ArrayList<String> headerTypes) {

    if (headers.size() != headerTypes.size()) {
      return false;
    }

    this.headers = headers;
    this.headerTypes = headerTypes;
    this.numHeaders = headers.size();

    try {
      String query = "ALTER TABLE " + tableName;

      for (int i = 0; i < numHeaders; i++) {
        query += " ADD COLUMN " + headers.get(i) + " " + headerTypes.get(i) + ", ";
      }

      query = query.substring(0, query.length() - 2); // remove last comman and space
      query += ";";

      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".addHeaders]: Column headers generated for table "
              + tableName
              + ".");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }

    return true;
  }

  public ArrayList<String> get(String id) {

    try {
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE nodeid = '"
              + id
              + "';";

      // ensure result set is scrollable (can be read forwards and backwards) and can be updated
      Statement statement =
          dbConnection.createStatement(
              ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      results.next();

      ArrayList<String> data = new ArrayList<>();
      int col = 0;
      results.getString(col); // assume at least one column in db

      while (!results.wasNull()) {
        data.add(results.getString(col));
        col++;
        results.getString(
            col); // try to read next column. if null, automatically break out of while loop
      }

      return data;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public boolean insert(TableEntry tableEntry) {

    try {
      if (!exists(tableEntry)) {
        return false;
      }

      // insert entry if not present
      String query = "INSERT INTO " + dbConnection.getSchema() + "." + tableName + " (";
      for (String header : headers) {
        query += header + ", ";
      }
      query = query.substring(0, query.length() - 2) + ") VALUES ('";

      for (String s : tableEntry.deconstruct()) {
        query += s + "','";
      }
      query = query.substring(0, query.length() - 2) + ");";

      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".insert]: TableEntry "
              + tableEntry.id
              + " successfully inserted into table "
              + tableName
              + ".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public boolean update(TableEntry tableEntry) {

    try {

      if (!exists(tableEntry)) {
        return insert(tableEntry);
      }

      // update if does exist
      String query = "UPDATE " + dbConnection.getSchema() + "." + tableName + " SET ";

      ArrayList<String> data = tableEntry.deconstruct();
      for (int i = 0; i < numHeaders; i++) {
        query += headers.get(0) + " = '" + data.get(i) + "',";
      }

      query = query.substring(0, query.length() - 1) + " WHERE id = '" + tableEntry.id + "';";

      Statement statement = dbConnection.createStatement();
      statement.execute(query);

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully updated entry  "
              + tableEntry.id
              + " in table"
              + tableName
              + ".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  private boolean exists(TableEntry tableEntry) {
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE id = '"
              + tableEntry.id
              + "');";

      Statement statement = dbConnection.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      if (!results.getBoolean("exists")) {
        System.out.println(
            "["
                + this.getClass().getSimpleName()
                + ".insert]: TableEntry "
                + tableEntry.id
                + " doesn't exist in table "
                + tableName
                + ".");
        return false;
      } else {
        return true;
      }
    } catch (SQLException e) {
      System.out.println(e.getErrorCode());
      return false;
    }
  }

  @Override
  public void remove(TableEntry tableEntry) {

    try {
      String query =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE id = '"
              + tableEntry.id
              + "'";
      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully removed  "
              + tableEntry.id
              + " from table "
              + tableName
              + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  //TODO
  @Override
  public void importCSV(String filepath) {}

  @Override
  public void exportCSV(String filepath) {}
}
