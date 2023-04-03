package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 * Represents a table of nodes in a PostgreSQL database.
 *
 * @author Christian
 * @author Jon
 * @author Charlie
 * @author Trajan
 */
public class NodeTable extends Table {
  Table microNodeTable; // id -> x, y, floor, building
  Table moveTable; // id -> longname, date
  Table locationTable; // longname -> shortname, type

  /**
   * Creates a new representation of a node table.
   *
   * @param conn Database connection object for this table
   * @param tableName Name of the table
   */
  public NodeTable(Connection conn, String tableName) {

    this.dbConnection = conn;
    this.tableName = tableName;

    microNodeTable = new Table(conn, "micronode");
    moveTable = new Table(conn, "move");
    locationTable = new Table(conn, "location");

    microNodeTable.init(false);
    microNodeTable.addHeaders(
        new ArrayList<>(List.of("id", "x", "y", "floor", "building")),
        new ArrayList<>(List.of("String", "double", "double", "String", "String")));

    moveTable.init(false);
    moveTable.addHeaders(
        new ArrayList<>(List.of("id", "longname", "date")),
        new ArrayList<>(List.of("String", "String", "String")));

    locationTable.init(false);
    locationTable.addHeaders(
        new ArrayList<>(List.of("longname", "shortname", "type")),
        new ArrayList<>(List.of("String", "String", "String")));
  }

  public Node getNode(String id) {

    ArrayList<String> microNode = microNodeTable.get("id", id);
    ArrayList<String> move = moveTable.get("id", id);
    ArrayList<String> location = locationTable.get("longname", move.get(1));

    Point2D p = new Point2D(Integer.parseInt(microNode.get(1)), Integer.parseInt(microNode.get(2)));
    return new Node(
        Integer.parseInt(microNode.get(0)),
        p,
        microNode.get(3),
        microNode.get(4),
        NodeType.valueOf(location.get(2)),
        location.get(0),
        location.get(1));
  }

  public Node[] getAllNodes() {

    Node[] nodes = new Node[microNodeTable.size() - 1];

    ArrayList<String>[] microNodes = microNodeTable.getAll();

    for (int i = 0; i < nodes.length; i++) {

      ArrayList<String> microNode = microNodes[i + 1];
      ArrayList<String> move = moveTable.get("id", microNode.get(0));

      // use longname (move.get(1)) to index into locationtable
      ArrayList<String> location = locationTable.get("longname", move.get(1));

      Point2D p =
          new Point2D(Integer.parseInt(microNode.get(1)), Integer.parseInt(microNode.get(2)));

      nodes[i] =
          new Node(
              Integer.parseInt(microNode.get(0)),
              p,
              microNode.get(3),
              microNode.get(4),
              NodeType.valueOf(location.get(2)),
              location.get(0),
              location.get(1));
    }

    return nodes;
  }

  @Override
  public int size() {
    return microNodeTable.size();
  }

  public boolean insert(Node node, String date) {

    MicroNode microNode =
        new MicroNode(node.nid, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.nid, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    return microNodeTable.insert(microNode)
        && moveTable.insert(move)
        && locationTable.insert(location);
  }

  public boolean update(Node node, String date) {
    MicroNode microNode =
        new MicroNode(node.nid, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.nid, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    return microNodeTable.update(microNode)
        && moveTable.update(move)
        && locationTable.insert(location);
  }

  public void remove(String nodeId) {

    locationTable.remove("longname", moveTable.get("id", "id").get(1));

    microNodeTable.remove("id", nodeId);
    moveTable.remove("id", nodeId);
  }

  public void importCSV(String microNodePath, String locationPath, String movePath) {

    microNodeTable.importCSV(microNodePath);
    locationTable.importCSV(locationPath);
    moveTable.importCSV(movePath);
  }

  public void exportCSV(String microNodePath, String locationPath, String movePath) {
    microNodeTable.exportCSV(microNodePath);
    locationTable.exportCSV(locationPath);
    moveTable.exportCSV(movePath);
  }
}
