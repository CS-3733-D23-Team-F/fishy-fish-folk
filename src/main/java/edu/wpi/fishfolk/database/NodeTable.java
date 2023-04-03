package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.Connection;
import java.util.ArrayList;
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

    microNodeTable = new Table(conn, "micronode", false);
    moveTable = new Table(conn, "move", false);
    locationTable = new Table(conn, "location", false);
  }

  public Node getNode(String id) {

    ArrayList<String> microNode = microNodeTable.get(id);
    ArrayList<String> move = moveTable.get(id);
    ArrayList<String> location = locationTable.get(move.get(1));

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

    Node[] nodes = new Node[microNodeTable.size()];

    ArrayList<String>[] microNodes = microNodeTable.getAll();

    for (int i = 0; i < nodes.length; i++) {

      ArrayList<String> microNode = microNodes[i];
      ArrayList<String> move = moveTable.get(microNode.get(0));

      // use longname (move.get(0)) to index into locationtable
      ArrayList<String> location = locationTable.get(move.get(0));

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

  public void remove(Node node, String date) {

    microNodeTable.remove(node.id);
    moveTable.remove(node.id);
    locationTable.remove(node.longName);
  }

  public void remove(String nodeId) {

    // get longname from movetable, second column
    locationTable.remove(moveTable.get(nodeId).get(1));

    microNodeTable.remove(nodeId);
    moveTable.remove(nodeId);
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
