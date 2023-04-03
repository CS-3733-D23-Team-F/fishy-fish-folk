package edu.wpi.fishfolk.database;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.Getter;

public class Table implements ITable {

  protected Connection dbConnection;
  @Getter protected String tableName;
  private ArrayList<String> headers;
  private ArrayList<String> headerTypes;
  private int numHeaders;

  private static HashMap<String, String> typeDict;

  public Table() {}

  /**
   * @param dbConnection
   * @param tableName
   */
  public Table(Connection dbConnection, String tableName) {
    this.dbConnection = dbConnection;
    this.tableName = tableName;

    // set up map from Java types to SQL types
    typeDict = new HashMap<>();
    typeDict.put("String", "VARCHAR(255)"); // 255 characters max
    typeDict.put("int", "SMALLINT"); // -2^15 to 2^15-1
    typeDict.put("double", "REAL"); // 6 decimal digits precision
  }

  /**
   * Create the Table in the database
   *
   * @param drop true if this Table should replace any table with the same name, false if this Table
   *     should not take replace another with the same name
   */
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

          query = "CREATE TABLE " + tableName + " (id VARCHAR(10) PRIMARY KEY);";
          statement.executeUpdate(query);
          System.out.println("[Table.constructor]: Created table \"" + tableName + "\".");
        }
        // exists but dont drop - do nothing

      } else {
        // doesnt exist, create as usual
        query = "CREATE TABLE " + tableName + " (id VARCHAR(10) PRIMARY KEY);";
        statement.executeUpdate(query);
        System.out.println("[Table.constructor]: Created table \"" + tableName + "\".");
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // TODO & potential problems:
  // test the shit out of this
  // import & export use FileReaders which may break when packaged into a .jar
  // drop table before adding new headers

  @Override
  public void setHeaders(ArrayList<String> _headers, ArrayList<String> _headerTypes) {

    // map Java types to SQL types
    this.headerTypes =
        (ArrayList<String>)
            _headerTypes.stream().map(t -> typeDict.get(t)).collect(Collectors.toList());

    this.headers = _headers;
    this.numHeaders = headers.size();
  }

  public boolean addHeaders(ArrayList<String> _headers, ArrayList<String> _headerTypes) {

    if (_headers.size() != _headerTypes.size()) {
      return false;
    }

    setHeaders(_headers, _headerTypes);

    try {

      String query = "SELECT COUNT(*) FROM " + dbConnection.getSchema() + "." + tableName + ";";
      Statement statement = dbConnection.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();
      int numRows = results.getInt(1);
      int numCols = 1;

      query = "ALTER TABLE " + tableName;

      if (numRows != 0) {
        // get only 1 row which is enough to get the metadata
        statement.execute(
            "SELECT * FROM " + dbConnection.getSchema() + "." + tableName + " LIMIT 1;");
        ResultSetMetaData meta = statement.getResultSet().getMetaData();
        numCols = meta.getColumnCount();

        // rename current column names
        // ex: currently 2 columns and requested 5 headers
        // leave first id column as is. rename 2 to header idx 1.
        // add headers idx 2, 3, 4

        // SQL columns start at 1
        for (int col = 1; col <= numCols && col <= numHeaders; col++) {

          String oldCol = meta.getColumnName(col), newCol = headers.get(col - 1);
          if (!oldCol.equals(newCol)) {
            query += " RENAME COLUMN '" + oldCol + "' to '" + newCol + "' ";
          }
        }

        // if numCols > numHeaders delete remaining columns
        for (int col = numHeaders + 1; col <= numCols; col++) {
          query += " DROP COLUMN " + meta.getColumnName(col) + ",";
        }
      }

      // if numHeaders > numCols add the remaining headers
      for (int col = numCols + 1; col <= numHeaders; col++) {
        query += " ADD COLUMN " + headers.get(col - 1) + " " + headerTypes.get(col - 1) + ",";
      }

      // added more to base query "alter table tablename"
      if (query.split(" ").length > 3) {

        // remove last comma
        query = query.substring(0, query.length() - 1) + ";";

        System.out.println(query);

        statement = dbConnection.createStatement();
        statement.executeUpdate(query);
      }

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".addHeaders]: Set column headers of table \""
              + tableName
              + "\".");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }

    return true;
  }

  @Override
  public ArrayList<String> get(String attr, String value) {

    try {
      String query =
          "SELECT * FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE "
              + attr
              + " = '"
              + value
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
  public ArrayList<String>[] getAll() {

    ArrayList<String>[] data = new ArrayList[size() + 1];
    data[0] = headers;

    try {

      String query = "SELECT * FROM " + dbConnection.getSchema() + "." + tableName + ";";

      Statement statement = dbConnection.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      int idx = 1;
      while (results.next()) {
        ArrayList<String> row = new ArrayList<>(numHeaders);
        for (int i = 1; i <= numHeaders; i++) {
          row.add(results.getString(i));
        }
        data[idx] = row;
        idx++;
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return data;
  }

  @Override
  public int size() {
    try {
      String query = "SELECT COUNT(*) FROM " + dbConnection.getSchema() + "." + tableName + ";";

      // ensure result set is scrollable (can be read forwards and backwards) and can be updated
      Statement statement = dbConnection.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();
      return results.getInt(1);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return -1;
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
              + ".insert]: TableEntry \""
              + tableEntry.id
              + "\" successfully inserted into table \""
              + tableName
              + "\".");
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

      // id does exist so update
      String query = "UPDATE " + dbConnection.getSchema() + "." + tableName + " SET ";

      ArrayList<String> data = tableEntry.deconstruct();
      for (int i = 0; i < numHeaders; i++) {
        query += headers.get(i) + " = '" + data.get(i) + "',";
      }

      query = query.substring(0, query.length() - 1) + " WHERE id = '" + tableEntry.id + "';";

      Statement statement = dbConnection.createStatement();
      statement.execute(query);

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully updated entry \""
              + tableEntry.id
              + "\" in table \""
              + tableName
              + "\".");
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

      // id does exist so update
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
              + "'s attribute \""
              + attr
              + "\" to value \""
              + value
              + "\" in table \""
              + tableName
              + "\".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public void remove(String attr, String value) {

    try {

      String entry = "[" + String.join(", ", get(attr, value)) + "]";

      String query =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + tableName
              + " WHERE "
              + attr
              + " = '"
              + value
              + "'";
      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".update]: Successfully removed \""
              + entry
              + "\" from table \""
              + tableName
              + "\".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public boolean exists(String id) {
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

      boolean ans = results.getBoolean("exists");

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".exists]: TableEntry "
              + id
              + (ans ? " does" : " does not")
              + " exist in table \""
              + tableName
              + "\".");
      return ans;

    } catch (SQLException e) {
      System.out.println(e.getErrorCode());
      return false;
    }
  }

  @Override
  public void importCSV(String filepath) {

    String[] pathArr = filepath.split("/", 1);
    String filename = pathArr[pathArr.length - 1]; // last element

    System.out.println(
        "["
            + this.getClass().getSimpleName()
            + ".import]: importing \""
            + filename
            + "\" into table \""
            + tableName
            + "\".");

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
              + ".import]: Successfully imported \""
              + filename
              + "\" into table \""
              + tableName
              + "\".");
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
        tableName + "_" + dateTime.format(DateTimeFormatter.ofPattern("yy-MM-dd HH-mm")) + ".csv";

    System.out.println(
        "["
            + this.getClass().getSimpleName()
            + ".export]: exporting table \""
            + tableName
            + "\" into \""
            + filename
            + "\".");

    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filepath + filename)));
      String grabAll = "SELECT * FROM " + dbConnection.getSchema() + "." + tableName + ";";
      Statement statement = dbConnection.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      out.println(String.join(", ", headers));

      while (results.next()) {

        String line = "";
        for (int i = 1; i <= numHeaders; i++) {
          line += results.getString(i) + ",";
        }
        out.println(line.substring(0, line.length() - 1));
      }

      out.close();

      System.out.println(
          "["
              + this.getClass().getSimpleName()
              + ".export]: successfully exported table \""
              + tableName
              + "\" into \""
              + filename
              + "\".");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
