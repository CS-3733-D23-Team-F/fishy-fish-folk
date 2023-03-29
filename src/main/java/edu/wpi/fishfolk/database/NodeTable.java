package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a table of nodes in a PostgreSQL database.
 *
 * @author Christian
 * @author Jon
 */
public class NodeTable {

  private Connection db;

  @Setter private EdgeTable edgeTable;
  @Getter private String tableName;
  private ArrayList<String> headers =
      new ArrayList<>(
          Arrays.asList(
              "nodeid",
              "oldid",
              "xcoord",
              "ycoord",
              "floor",
              "building",
              "nodetype",
              "longname",
              "shortname"));

  HashMap<String, String> idTranslation = new HashMap<>();

  HashMap<String, Integer> nodeTypeCounts =
      new HashMap<>(); // key: nodeType + floor, value is count

  /**
   * Creates a new representation of a node table.
   *
   * @param db Database connection object for this table
   * @param tableName Name of the table
   */
  public NodeTable(Connection db, String tableName) {
    this.db = db;
    this.tableName = tableName.toLowerCase();
  }

  public NodeTable(String tableName) {

    this.db = new Fdb().connect("teamfdb", "teamf", "teamf60");
    this.tableName = tableName;
  }

  /**
   * For empty tables only, generates new column headers for the node table. TODO: Check if table is
   * empty before applying new headers
   */
  public void addHeaders() {
    Statement statement;
    try {
      String query =
          "ALTER TABLE "
              + tableName
              + " ADD COLUMN "
              + "nodeid"
              + " TEXT,"
              + " ADD COLUMN "
              + "oldid"
              + " TEXT,"
              + " ADD COLUMN "
              + "xcoord"
              + " INT,"
              + " ADD COLUMN "
              + "ycoord"
              + " INT,"
              + " ADD COLUMN "
              + "floor"
              + " TEXT,"
              + " ADD COLUMN "
              + "building"
              + " TEXT,"
              + " ADD COLUMN "
              + "nodetype"
              + " TEXT,"
              + " ADD COLUMN "
              + "longname"
              + " TEXT,"
              + " ADD COLUMN "
              + "shortname"
              + " TEXT;";
      statement = db.createStatement();
      statement.executeUpdate(query);
      System.out.println(
          "[NodeTable.addHeaders]: Column headers generated for table " + tableName + ".");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public int getSize() {
    int size = -1;
    try {
      String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement =
          db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      if (results != null) {
        results.last(); // moves cursor to the last row
        size = results.getRow(); // get row id
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return size;
  }

  public LinkedList<Node> getAllNodes() {

    LinkedList<Node> nodes = new LinkedList<>();

    try {
      String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement = db.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      while (results.next()) {
        // System.out.println(results.getRow());  // Removed for cleanliness, feel free to restore

        String nodeId = results.getString("nodeid");
        double xcoord = results.getDouble("xcoord");
        double ycoord = results.getDouble("ycoord");
        String floor = results.getString("floor");
        String building = results.getString("building");
        NodeType nodeType = NodeType.valueOf(results.getString("nodetype"));
        String longName = results.getString("longname");
        String shortName = results.getString("shortname");

        nodes.add(
            new Node(
                nodeId,
                new Point2D(xcoord, ycoord),
                floor,
                building,
                nodeType,
                longName,
                shortName));
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return nodes;
  }

  /**
   * Returns a new Node from a specified entry in the table.
   *
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

      String nodeId = results.getString("nodeid");
      double xcoord = results.getDouble("xcoord");
      double ycoord = results.getDouble("ycoord");
      String floor = results.getString("floor");
      String building = results.getString("building");
      NodeType nodeType = NodeType.valueOf(results.getString("nodetype"));
      String longName = results.getString("longname");
      String shortName = results.getString("shortname");

      // i dont love how this returns a new object but i dont see a way around it
      // and besides the Node .equals would return true for all nodes returned by getNode() with the
      // same parameter
      Node newNode =
          new Node(
              nodeId, new Point2D(xcoord, ycoord), floor, building, nodeType, longName, shortName);
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
   *
   * @param node Node to insert
   * @return True if inserted, false if the node already exists and/or is not added
   */
  public boolean insertNode(Node node) {

    if (node == null) {
      return false;
    }

    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE nodeid = '"
              + node.id
              + "');";

      Statement statement = db.createStatement();
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
              + " (nodeid, oldid, xcoord, ycoord, floor, building, nodetype, longname, shortname) "
              + "VALUES ('"
              + node.id
              + "','"
              + node.oldID
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
      // update count for this type of node on this floor
      getNodeTypeCount(node.type.toString() + node.floor);

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

    if (node == null) {
      return false;
    }

    try {
      String exists =
          "SELECT EXISTS (SELECT FROM "
              + db.getSchema()
              + "."
              + tableName
              + " WHERE nodeid = '"
              + node.id
              + "');";

      Statement statement = db.createStatement();
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

    if (node == null) {
      return;
    }

    try {
      String query =
          "DELETE FROM " + db.getSchema() + "." + tableName + " WHERE nodeid = '" + node.id + "'";
      Statement statement = db.createStatement();
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

    System.out.println("[NodeTable.importCSV]: Importing CSV to table " + tableName + ".");

    try {
      String delete = "DELETE FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement = db.createStatement();
      statement.execute(delete);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String oldID = values[0];
        Point2D point = new Point2D(Integer.parseInt(values[1]), Integer.parseInt(values[2]));

        String floor = values[3];

        String building = values[4];

        NodeType type = NodeType.valueOf(values[5]);

        String longName = values[6];

        String shortName = values[7];

        String nodeNum;

        if (type == NodeType.ELEV) {
          nodeNum = "00" + shortName.substring(9, 10);

        } else {

          nodeNum = "00" + getNodeTypeCount(type.toString() + floor);
          nodeNum = nodeNum.substring(nodeNum.length() - 3);
        }
        String nodeID = "f" + type.toString() + nodeNum + floor;
        idTranslation.put(oldID, nodeID); // map given id to correct id

        Node node = new Node(nodeID, point, floor, building, type, longName, shortName);
        node.oldID = oldID;

        insertNode(node);
      }
      br.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public void exportCSV() {

    System.out.println("[NodeTable.exportCSV]: exporting CSV from table " + tableName + ".");

    try {
      PrintWriter out =
          new PrintWriter(
              new BufferedWriter(
                  new FileWriter("src/main/resources/edu/wpi/fishfolk/csv/L1NodesOutput.csv")));

      String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
      Statement statement = db.createStatement();
      statement.execute(grabAll);
      ResultSet results = statement.getResultSet();

      out.println(String.join(", ", headers));

      while (results.next()) {
        // System.out.println(results.getRow());  // Removed for cleanliness, feel free to restore
        out.println(
            results.getString("nodeid")
                + ","
                + results.getString("oldid")
                + ", "
                + results.getDouble("xcoord")
                + ","
                + results.getDouble("ycoord")
                + ","
                + results.getString("floor")
                + ","
                + results.getString("building")
                + ","
                + results.getString("nodetype")
                + ","
                + results.getString("longname")
                + ","
                + results.getString("shortname"));
      }

      out.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  int getNodeTypeCount(String key) {
    Integer prevCount = nodeTypeCounts.get(key);
    int count;
    if (prevCount != null) {
      count = prevCount + 1;
    } else {
      count = 1;
    }
    nodeTypeCounts.put(key, count);
    return count;
  }
}
