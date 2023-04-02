package edu.wpi.fishfolk.database;

//TODO: modify this class with Trajan's plans

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

  @Override
  public boolean insert(TableEntry tableEntry){

    return false;
  }

  @Override
  public boolean update(TableEntry tableEntry){

    return false;
  }

  @Override
  public void remove(TableEntry tableEntry){

  }

  @Override
  public void importCSV(String filepath){

  }

  @Override
  public void exportCSV(String filepath){

  }

}
