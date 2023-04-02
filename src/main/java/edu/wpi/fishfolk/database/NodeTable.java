package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import java.sql.Connection;
import java.util.ArrayList;
import lombok.Getter;

/**
 * Represents a table of nodes in a PostgreSQL database.
 *
 * @author Christian
 * @author Jon
 * @author Charlie
 * @author Trajan
 */
public class NodeTable extends Table {

  private final Connection db;
  @Getter private final String tableName;

  Table microNodeTable; // id -> x, y, floor, building
  Table moveTable;      //id -> longname, date
  Table locationTable;  // longname -> shortname, type

  /**
   * Creates a new representation of a node table.
   *
   * @param db Database connection object for this table
   * @param tableName Name of the table
   */
  public NodeTable(Connection db, String tableName) {
    //TODO init Tables
    super(db, tableName.toLowerCase());
    this.db = db;
    this.tableName = tableName.toLowerCase();
  }

  @Override
  public ArrayList<String> get(String id){

    return null;
  }

  public boolean insert(Node node, String date){

    MicroNode microNode = new MicroNode(node.nid, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.nid, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    return microNodeTable.insert(microNode) &&
            moveTable.insert(move) &&
            locationTable.insert(location);
  }

  public boolean update(Node node, String date){
    MicroNode microNode = new MicroNode(node.nid, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.nid, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    return microNodeTable.update(microNode) &&
            moveTable.update(move) &&
            locationTable.insert(location);
  }
  public void remove(Node node, String date){

    microNodeTable.remove(node.id);
    moveTable.remove(node.id);
    locationTable.remove(node.longName);
  }

  public void remove(String nodeId){

    //get longname from movetable, second column
    locationTable.remove( moveTable.get(nodeId).get(1));

    microNodeTable.remove(nodeId);
    moveTable.remove(nodeId);
  }

  public void importCSV(String microNodePath, String locationPath, String movePath){

    microNodeTable.importCSV(microNodePath);
    locationTable.importCSV(locationPath);
    moveTable.importCSV(movePath);
  }

  public void exportCSV(String microNodePath, String locationPath, String movePath){
    microNodeTable.exportCSV(microNodePath);
    locationTable.exportCSV(locationPath);
    moveTable.exportCSV(movePath);
  }

}
