package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import java.sql.Connection;

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
   * @param db Database connection object for this table
   * @param tableName Name of the table
   */
  public NodeTable(Connection conn, String tableName) {
    // TODO init Tables
    super(conn, tableName);

    microNodeTable = new Table(conn, "microNodeTable");
    moveTable = new Table(conn, "moveTable");
    locationTable = new Table(conn, "locationTable");
  }

  public Node getNode(String id) {

    return null;
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
