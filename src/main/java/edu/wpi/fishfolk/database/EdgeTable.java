package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Edge;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class EdgeTable {

  private Connection db;
  @Getter private String tableName;
  private ArrayList<String> headers = new ArrayList<>(List.of("edgeid", "startnode", "endnode"));

  public EdgeTable(Connection db, String tableName) {
    this.db = db;
    this.tableName = tableName.toLowerCase();
  }

  public void addHeaders() {
    Statement statement;
    try {
      String query =
          "ALTER TABLE "
              + tableName
              + " ADD COLUMN "
              + headers.get(0)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(1)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(2)
              + " TEXT;";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[EdgeTable.addHeaders]: Column headers generated for table " + tableName + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public Edge getEdge(String id) {
    Statement statement;
    try {
      String query =
          "SELECT * FROM " + db.getSchema() + "." + tableName + " WHERE edgeid = '" + id + "';";
      statement = db.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      String edgeid = results.getString(headers.get(0));
      String startnode = results.getString(headers.get(1));
      String endnode = results.getString(headers.get(2));

      Edge newEdge = new Edge(edgeid, startnode, endnode);
      System.out.println(
          "[EdgeTable.getEdge]: Edge " + id + " retrieved from table " + tableName + ".");

      return newEdge;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return null;
  }

  // True if inserted, false if duplicate or not added
  public boolean insertEdge(Edge edge) {
    Statement statement;
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE edgeid = '"
              + edge.edgeid
              + "');";

      statement = db.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      if (results.getBoolean("exists")) {
        System.out.println(
            "[EdgeTable.insertEdge]: Edge "
                + edge.edgeid
                + " already exists in table "
                + tableName
                + ".");
        return false;
      }

      String query =
          "INSERT INTO "
              + db.getSchema()
              + "."
              + tableName
              + " (edgeid, startnode, endnode) "
              + "VALUES ('"
              + edge.edgeid
              + "','"
              + edge.startnode
              + "','"
              + edge.endnode
              + "');";

      statement.executeUpdate(query);

      System.out.println(
          "[EdgeTable.insertEdge]: Edge "
              + edge.edgeid
              + " successfully inserted into table "
              + tableName
              + ".");
      return true;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  // True if updated, false if had to insert
  public boolean updateEdge(Edge edge) {
    Statement statement;
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE edgeid = '"
              + edge.edgeid
              + "');";

      statement = db.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      if (!results.getBoolean("exists")) {
        System.out.println(
            "[EdgeTable.updateEdge]: Edge "
                + edge.edgeid
                + " doesn't exist in table "
                + tableName
                + ".");
        insertEdge(edge);
        return false;
      }

      String query =
          "UPDATE "
              + db.getSchema()
              + "."
              + tableName
              + " SET"
              + " edgeid = '"
              + edge.edgeid
              + "',startnode = '"
              + edge.startnode
              + "',endnode = '"
              + edge.endnode
              + "' WHERE edgeid = '"
              + edge.edgeid
              + "'";

      statement.executeUpdate(query);
      System.out.println(
          "[EdgeTable.updateEdge]: Successfully updated edge "
              + edge.edgeid
              + " in table "
              + tableName
              + ".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public void removeEdge(Edge edge) {
    Statement statement;
    try {
      String query =
          "DELETE FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE edgeid = '"
              + edge.edgeid
              + "'";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[EdgeTable.removeEdge]: Edge "
              + edge.edgeid
              + " has been successfully removed from table "
              + tableName
              + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // DEBUG ONLY
  public void testQuery() {
    Statement statement;
    try {
      String query =
          "DELETE FROM "
              + db.getSchema()
              + "."
              + tableName
              + " *; "
              + "INSERT INTO "
              + db.getSchema()
              + "."
              + tableName
              + " (edgeid, startnode, endnode) "
              + "VALUES ('CCONF002L1_WELEV00HL1', 'CCONF002L1', 'WELEV00HL1'),"
              + "('CCONF003L1_CHALL002L1', 'CCONF003L1', 'CHALL002L1');";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[EdgeTable.testQuery]: Test environment set up successfully. (DEBUG ONLY)");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void importCSV() {

    System.out.println("[EdgeTable.importCSV]: Importing CSV to table " + tableName + ".");

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Edges.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String edgeID = values[0];

        String startNode = values[1];

        String endNode = values[2];

        Edge edge = new Edge(edgeID, startNode, endNode);

        insertEdge(edge);
      }
      br.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public void exportCSV() {

    System.out.println("[EdgeTable.exportCSV]: Exporting CSV from table " + tableName + ".");

    try {
      PrintWriter out =
          new PrintWriter(
              new BufferedWriter(
                  new FileWriter("src/main/resources/edu/wpi/fishfolk/csv/L1EdgesOutput.csv")));
      String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement = db.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      out.println(headers.get(0) + "," + headers.get(1) + "," + headers.get(2));

      while (results.next()) {
        // System.out.println(results.getRow());  // Removed for cleanliness, feel free to restore
        out.println(
            results.getString(headers.get(0))
                + ","
                + results.getString(headers.get(1))
                + ","
                + results.getString(headers.get(2)));
      }

      out.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
