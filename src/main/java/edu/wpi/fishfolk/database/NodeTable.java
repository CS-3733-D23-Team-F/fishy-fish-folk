package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import lombok.Getter;

/**
 * Represents a table of nodes in a PostgreSQL database.
 * @author Christian
 * @author Jon
 */
public class NodeTable {

  private Connection db;
  @Getter private String tableName;
  private ArrayList<String> headers =
      new ArrayList<>(
          List.of(
              "nodeid",
              "xcoord",
              "ycoord",
              "floor",
              "building",
              "nodetype",
              "longname",
              "shortname"));

  /**
   * Creates a new representation of a node table.
   * @param db Database connection object for this table
   * @param tableName Name of the table
   */
  public NodeTable(Connection db, String tableName) {
    this.db = db;
    this.tableName = tableName.toLowerCase();
  }

  /**
   * For empty tables only, generates new column headers for the node table.
   * TODO: Check if table is empty before applying new headers
   */
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
              + " INT,"
              + " ADD COLUMN "
              + headers.get(2)
              + " INT,"
              + " ADD COLUMN "
              + headers.get(3)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(4)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(5)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(6)
              + " TEXT,"
              + " ADD COLUMN "
              + headers.get(7)
              + " TEXT;";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[NodeTable.addHeaders]: Column headers generated for table " + tableName + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Returns a new Node from a specified entry in the table.
   * @param id Node id
   * @return New node object, returns null if specified Node does not exist in table
   */
  public Node getNode(String id) {
    Statement statement;
    try {
      String query =
          "SELECT * FROM " + db.getSchema() + "." + tableName + " WHERE nodeid = '" + id + "';";
      statement = db.createStatement();
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();

      String nodeid = results.getString(headers.get(0));
      double xcoord = results.getDouble(headers.get(1));
      double ycoord = results.getDouble(headers.get(2));
      String floor = results.getString(headers.get(3));
      String building = results.getString(headers.get(4));
      String nodetype = results.getString(headers.get(5));
      String longname = results.getString(headers.get(6));
      String shortname = results.getString(headers.get(7));

      Point2D point = new Point2D(xcoord, ycoord);
      NodeType type = NodeType.valueOf(nodetype);

      // i dont love how this returns a new object but i dont see a way around it
      // and besides the Node .equals would return true for all nodes returned by getNode() with the
      // same parameter
      Node newNode = new Node(nodeid, point, floor, building, type, longname, shortname);
      System.out.println(
          "[NodeTable.getNode]: Node " + id + " retrieved from table " + tableName + ".");

      return newNode;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return null;
  }

  /**
   * Inserts a node into the table if it does not exist.
   * @param node Node to insert
   * @return True if inserted, false if the node already exists and/or is not added
   */
  public boolean insertNode(Node node) {
    Statement statement;
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE nodeid = '"
              + node.id
              + "');";

      statement = db.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      if (results.getBoolean("exists")) {
        System.out.println(
            "[NodeTable.insertNode]: Node "
                + node.id
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
              + " (nodeid, xcoord, ycoord, floor, building, nodetype, longname, shortname) "
              + "VALUES ('"
              + node.id
              + "','"
              + (int) node.point.getX()
              + "','"
              + (int) node.point.getY()
              + "','"
              + node.floor
              + "','"
              + node.building
              + "','"
              + node.type.toString()
              + "','"
              + node.longName
              + "','"
              + node.shortName
              + "');";

      statement.executeUpdate(query);

      System.out.println(
          "[NodeTable.insertNode]: Node "
              + node.id
              + " successfully inserted into table "
              + tableName
              + ".");
      return true;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  // true if updated, false if had to insert
  public boolean updateNode(Node node) {
    Statement statement;
    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE nodeid = '"
              + node.id
              + "');";

      statement = db.createStatement();
      statement.execute(exists);
      ResultSet results = statement.getResultSet();
      results.next();

      if (!results.getBoolean("exists")) {
        System.out.println(
            "[NodeTable.updateNode]: Node "
                + node.id
                + " doesn't exist in table "
                + tableName
                + ".");
        insertNode(node);
        return false;
      }

      String query =
          "UPDATE "
              + db.getSchema()
              + "."
              + tableName
              + " SET"
              + " nodeid = '"
              + node.id
              + "',xcoord = '"
              + (int) node.point.getX()
              + "',ycoord = '"
              + (int) node.point.getY()
              + "',floor = '"
              + node.floor
              + "',building = '"
              + node.building
              + "',longname = '"
              + node.longName
              + "',shortname = '"
              + node.shortName
              + "' WHERE nodeid = '"
              + node.id
              + "'";

      statement.executeUpdate(query);
      System.out.println(
          "[NodeTable.updateNode]: Successfully updated node "
              + node.id
              + " in table "
              + tableName
              + ".");
      return true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public void removeNode(Node node) {
    Statement statement;
    try {
      String query =
          "DELETE FROM " + db.getSchema() + "." + tableName + " WHERE nodeid = '" + node.id + "'";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[NodeTable.removeNode]: Node "
              + node.id
              + " has been successfully removed from table "
              + tableName
              + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void importCSV() {

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String nodeID = values[0];

        Point2D point = new Point2D(Integer.parseInt(values[1]), Integer.parseInt(values[2]));

        String floor = values[3];

        String building = values[4];

        NodeType type = NodeType.valueOf(values[5]);

        String longName = values[6];

        String shortName = values[7];

        Node node = new Node(nodeID, point, floor, building, type, longName, shortName);

        insertNode(node);
      }
      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void exportCSV() {
    try {
      PrintWriter out =
          new PrintWriter(
              new BufferedWriter(
                  new FileWriter("src/main/resources/edu/wpi/fishfolk/csv/L1NodesOutput.csv")));
      String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement = db.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      out.println(
          headers.get(0)
              + ","
              + headers.get(1)
              + ","
              + headers.get(2)
              + ","
              + headers.get(3)
              + ","
              + headers.get(4)
              + ","
              + headers.get(5)
              + ","
              + headers.get(6)
              + ","
              + headers.get(7));

      while (results.next()) {
        // System.out.println(results.getRow());  // Removed for cleanliness, feel free to restore
        out.println(
            results.getString(headers.get(0))
                + ","
                + results.getDouble(headers.get(1))
                + ","
                + results.getDouble(headers.get(2))
                + ","
                + results.getString(headers.get(3))
                + ","
                + results.getString(headers.get(4))
                + ","
                + results.getString(headers.get(5))
                + ","
                + results.getString(headers.get(6))
                + ","
                + results.getString(headers.get(7)));
      }

      out.close();

    } catch (IOException e) {

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
