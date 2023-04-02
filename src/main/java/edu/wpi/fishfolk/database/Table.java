package edu.wpi.fishfolk.database;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.Getter;

public class Table implements ITable {

  private final Connection dbConnection;
  @Getter private final String tableName;
  private ArrayList<String> headers;
  private ArrayList<String> headerTypes;
  private int numHeaders;

  private static HashMap<String, String> typeDict;

  public Table(Connection conn, String name) {
    dbConnection = conn;
    tableName = name;

    typeDict = new HashMap<>();
    typeDict.put("String", "VARCHAR(255)"); // 255 characters max
    typeDict.put("int", "SMALLINT"); // -2^15 to 2^15-1
    typeDict.put("double", "REAL"); // 6 decimal digits precision
  }

  // TODO & potential problems:
  // test the shit out of this
  // import & export use FileReaders which may break when packaged into a .jar
  // drop table before adding new headers

  public boolean addHeaders(ArrayList<String> _headers, ArrayList<String> _headerTypes) {

    if (_headers.size() != _headerTypes.size()) {
      return false;
    }

    // map Java types to SQL types
    this.headerTypes =
        (ArrayList<String>)
            _headerTypes.stream().map(t -> typeDict.get(t)).collect(Collectors.toList());

    this.headers = _headers;
    this.numHeaders = headers.size();

    System.out.println("[" + this.getClass().getSimpleName() + ".addHeaders]: Adding headers.");

    try {

      String query = "ALTER TABLE " + tableName;

      for (int i = 1; i < numHeaders; i++) { // start at index 1 since id is already in table
        query += " ADD COLUMN " + headers.get(i) + " " + headerTypes.get(i) + ", ";
      }

      query = query.substring(0, query.length() - 2) + ";"; // remove last comma and space

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
              + " WHERE id = '"
              + id
              + "';";

      // ensure result set is scrollable (can be read forwards and backwards) and can be updated
      Statement statement =
          dbConnection.createStatement(
              ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      if (!results.next()) {
        return null; // nothing found for this id
      }

      ArrayList<String> data = new ArrayList<>();

      for (int i = 1; i <= numHeaders; i++) {
        data.add(results.getString(i));
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
      if (exists(tableEntry.id)) {
        update(tableEntry);
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

      if (!exists(tableEntry.id)) {
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

  @Override
  public boolean update(String id, String attr, String value) {

    try {
      if (!exists(id)) {
        return false;
      }

      // update if does exist
      String query =
          "UPDATE "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " SET "
              + attr
              + " = '"
              + value
              + "' WHERE id = '"
              + id
              + "';";

      Statement statement = dbConnection.createStatement();
      statement.execute(query);

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully updated entry "
              + id
              + " attr "
              + attr
              + " to value "
              + value
              + " in table"
              + tableName
              + ".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  private boolean exists(String id) {
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE id = '"
              + id
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
                + id
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
  public void remove(String id) {

    try {
      String query =
          "DELETE FROM " + dbConnection.getSchema() + "." + tableName + " WHERE id = '" + id + "'";
      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully removed  "
              + id
              + " from table "
              + tableName
              + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void importCSV(String filepath) {

    String[] pathArr = filepath.split("/", 1);
    String filename = pathArr[pathArr.length - 1]; // last element

    System.out.println(
        "["
            + this.getClass().getSimpleName()
            + ".import]: importing "
            + filename
            + " into table "
            + tableName
            + ".");

    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

      String line = br.readLine(); // column headers on first line

      // insert row into db. each query starts the same way
      System.out.println(headers.size() + "_" + headers.get(0));
      String queryCommon = "INSERT INTO " + dbConnection.getSchema() + "." + tableName + " (";
      for (String header : headers) {
        queryCommon += header + ", ";
      }
      queryCommon = queryCommon.substring(0, queryCommon.length() - 2) + ") VALUES ('";

      while ((line = br.readLine()) != null) {

        String query = queryCommon + String.join("', '", Arrays.asList(line.split(","))) + "');";

        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(query);
      }

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".import]: Successfully imported "
              + filename
              + " into table "
              + tableName
              + ".");
      br.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void exportCSV(String filepath) {

    LocalDateTime dateTime = LocalDateTime.now();
    // see
    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofPattern-java.lang.String-
    String filename =
        tableName + "_" + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"));

    System.out.println(
        "["
            + this.getClass().getSimpleName()
            + ".export]: exporting table "
            + tableName
            + " into "
            + filename
            + ".");

    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filepath + filename)));
      String grabAll = "SELECT * FROM " + dbConnection.getSchema() + "." + tableName + ";";
      Statement statement = dbConnection.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      out.println(String.join(", ", headers));

      while (results.next()) {

        String line = "";
        for (int i = 0; i < numHeaders; i++) {
          line += results.getString(i) + ",";
        }
        out.println(line.substring(0, line.length() - 1));
      }

      out.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
