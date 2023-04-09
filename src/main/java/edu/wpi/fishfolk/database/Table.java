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
    typeDict.put("String", "VARCHAR(255)"); // default to max 255 characters for strings
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

  @Override
  public void setHeaders(ArrayList<String> _headers, ArrayList<String> _headerTypes) {

    // map Java types to SQL types
    this.headerTypes =
            (ArrayList<String>)
                    _headerTypes.stream().map(t -> {
                      if(t.startsWith("String") && t.length() > 6){
                        return "VARCHAR(" + t.substring(6) + ")";
                      }
                      return typeDict.get(t);
                    }).collect(Collectors.toList());

    this.headers = _headers;
    this.numHeaders = headers.size();
  }

  @Override
  public boolean addHeaders(ArrayList<String> _headers, ArrayList<String> _headerTypes) {

    if (_headers.size() != _headerTypes.size()) {
      return false;
    }

    setHeaders(_headers, _headerTypes);

    try {
      String query = "ALTER TABLE " + tableName + " DROP COLUMN id,";

      // if numHeaders > numCols add the remaining headers
      for (int col = 1; col <= numHeaders; col++) {
        query += " ADD COLUMN " + headers.get(col - 1) + " " + headerTypes.get(col - 1) + ",";
      }

      // if added more to base query "alter table tablename"
      if (query.split(" ").length > 3) {

        // remove last comma
        query = query.substring(0, query.length() - 1) + ";";

        System.out.println(query);

        Statement statement = dbConnection.createStatement();
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
  public ArrayList<String> get(String pkey, String id) {

    try {
      String query =
              "SELECT * FROM "
                      + dbConnection.getSchema()
                      + "."
                      + tableName
                      + " WHERE "
                      + pkey
                      + " = '"
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
  public String get(String pkey, String id, String attr) {

    try {
      String query =
              "SELECT "
                      + attr
                      + " FROM "
                      + dbConnection.getSchema()
                      + "."
                      + tableName
                      + " WHERE "
                      + pkey
                      + " = '"
                      + id
                      + "';";

      // ensure result set is scrollable (can be read forwards and backwards) and can be updated
      Statement statement = dbConnection.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      if (!results.next()) {
        return ""; // nothing found for this pkey/id
      }

      return results.getString(1);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public ArrayList<String> getColumn(String header) {
    try {
      String query =
              "SELECT " + header + " FROM " + dbConnection.getSchema() + "." + tableName + ";";

      Statement statement = dbConnection.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();

      ArrayList<String> data = new ArrayList<>();

      while (results.next()) {
        data.add(results.getString(1));
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
  public boolean insert(TableEntry tableEntry) {

    try {
      if (exists("id", tableEntry.id)) {
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

  /**
   * For internal use where one cannot create a TableEntry object to call insert(TableEntry e)
   * @param row the row to insert
   * @return true if successfully inserted, otherwise false.
   */
  private boolean insert(ArrayList<String> row) {

    try {

      // insert entry if not present
      String query = "INSERT INTO " + dbConnection.getSchema() + "." + tableName + " (";
      for (String header : headers) {
        query += header + ", ";
      }
      query = query.substring(0, query.length() - 2) + ") VALUES ('";

      for (String s : row) {
        query += s + "','";
      }
      query = query.substring(0, query.length() - 2) + ");";

      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);

      System.out.println(
              "["
                      + this.getClass().getSimpleName()
                      + ".insert]: TableEntry \""
                      + row
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

      if (!exists("id", tableEntry.id)) {
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
  public boolean update(String pkey, String id, String attr, String value) {

    try {
      if (!exists(pkey, id)) {
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
                      + "' WHERE "
                      + pkey
                      + " = '"
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
  public boolean update(DataEdit edit) {

    switch(edit.type){

      case INSERT:
        InsertEdit insertEdit = (InsertEdit) edit;
        return insert(insertEdit.data);

      case UPDATE:
        UpdateEdit updateEdit = (UpdateEdit) edit;
        return update(updateEdit.pkey,  updateEdit.id, updateEdit.attribute, updateEdit.value);

      case REMOVE:
        RemoveEdit removeEdit = (RemoveEdit) edit;
        return remove(removeEdit.pkey, removeEdit.id);
    }

    return false;
  }

  @Override
  public boolean remove(String pkey, String id) {

    try {

      String entry = "[" + String.join(", ", get(pkey, id)) + "]";

      String query =
              "DELETE FROM "
                      + dbConnection.getSchema()
                      + "."
                      + tableName
                      + " WHERE "
                      + pkey
                      + " = '"
                      + id
                      + "'";
      Statement statement = dbConnection.createStatement();
      statement.executeUpdate(query);
      System.out.println(
              "["
                      + this.getClass().getSimpleName()
                      + ".remove]: Successfully removed \""
                      + entry
                      + "\" from table \""
                      + tableName
                      + "\".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public boolean exists(String pkey, String id) {
    try {
      String exists =
              "SELECT EXISTS (SELECT FROM "
                      + dbConnection.getSchema()
                      + "."
                      + tableName
                      + " WHERE "
                      + pkey
                      + " = '"
                      + id
                      + "');";

      Statement statement = dbConnection.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      boolean ans = results.getBoolean(1);

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
  public int size() {
    try {
      String query = "SELECT COUNT(*) FROM " + dbConnection.getSchema() + "." + tableName + ";";

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
  public ArrayList<String[]> executeQuery(String selection, String condition) {

    ArrayList<String[]> data = new ArrayList<>();

    try {
      String query =
              selection.trim()
                      + " FROM "
                      + dbConnection.getSchema()
                      + "."
                      + tableName
                      + " "
                      + condition.trim()
                      + ";";
      Statement statement = dbConnection.createStatement();
      statement.execute(query);

      System.out.println(query);

      ResultSet results = statement.getResultSet();
      ResultSetMetaData meta = results.getMetaData();

      int numCols = meta.getColumnCount();

      while (results.next()) {
        String[] row = new String[numCols];

        for (int i = 0; i < numCols; i++) {
          row[i] = results.getString(i + 1);
        }
        data.add(row);
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return data;
  }

  @Override
  public void importCSV(String filepath, boolean backup) {

    String[] pathArr = filepath.split("/");
    String filename = pathArr[pathArr.length - 1]; // last element

    if (backup) {

      // filepath except for last part (actual file name)
      String folder = "";
      for (int i = 0; i < pathArr.length - 1; i++) {
        folder += pathArr[i] + "/";
      }
      exportCSV(folder);
    }

    System.out.println(
            "["
                    + this.getClass().getSimpleName()
                    + ".import]: importing \""
                    + filename
                    + "\" into table \""
                    + tableName
                    + "\".");

    try (BufferedReader br =
                 new BufferedReader(new InputStreamReader(new FileInputStream(filepath)))) {

      Statement statement = dbConnection.createStatement();
      statement.executeUpdate("DELETE FROM " + dbConnection.getSchema() + "." + tableName + ";");

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

        statement = dbConnection.createStatement();
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
      PrintStream out = new PrintStream(new FileOutputStream(filepath + "\\" + filename));
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
