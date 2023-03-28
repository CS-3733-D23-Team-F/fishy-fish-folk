package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import lombok.Getter;

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

  public NodeTable(Connection db, String tableName) {
    this.db = db;
    this.tableName = tableName;
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

      Node newNode = new Node(nodeid, point, floor, building, type, longname, shortname);
      System.out.println(
          "[NodeTable.getNode]: Node " + id + " retrieved from table " + tableName + ".");

      return newNode;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return null;
  }

  // true if inserted, false if duplicate or otherwise not added
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

  // true if updated (maybe inserted new node) , false
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

  public void importCSV(String fileName) {}

  public void exportCSV(String filename) {}
}
