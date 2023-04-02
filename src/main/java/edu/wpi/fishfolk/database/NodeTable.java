package edu.wpi.fishfolk.database;

//TODO: modify this class with Trajan's plans

import edu.wpi.fishfolk.pathfinding.Location;
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
 *
 * @author Christian
 * @author Jon
 */
public class NodeTable extends Table {

  private final Connection db;
  @Getter private final String tableName;

  Table microNodeTable;
  Table moveTable;
  Table locationTable;

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

    MicroNode microNode = new MicroNode(node.id, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.id, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    if(
      microNodeTable.insert(microNode) &&
      moveTable.insert(move) &&
      locationTable.insert(location)
    ){
      return true;
    }

    return false;
  }

  public boolean update(Node node, String date){
    MicroNode microNode = new MicroNode(node.id, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.id, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    if(
      microNodeTable.update(microNode) &&
      moveTable.update(move) &&
      locationTable.insert(location)
    ){
      return true;
    }

    return false;
  }
  public void remove(Node node, String date){
    MicroNode microNode = new MicroNode(node.id, node.point.getX(), node.point.getY(), node.floor, node.building);
    Move move = new Move(node.id, node.longName, date);
    Location location = new Location(node.longName, node.shortName, node.type);

    microNodeTable.remove(microNode);
    moveTable.remove(move);
    locationTable.remove(location);

  }

  public void importCSV(String microNodePath, String locationPath, String movePath){

    microNodeTable.importCSV(microNodePath);
    locationTable.importCSV(locationPath);
    moveTable.importCSV(movePath);

  }

  @Override
  public void exportCSV(String microNodePath, String locationPath, String movePath){
    microNodeTable.exportCSV(microNodePath);
    locationTable.exportCSV(locationPath);
    moveTable.exportCSV(movePath);

  }

}
