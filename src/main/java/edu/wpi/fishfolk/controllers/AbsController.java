package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.database.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsController {

  static Fdb dbConnection;

  public AbsController() {

    dbConnection = new Fdb();

    try {
      dbConnection.conn.setSchema("proto2db");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    dbConnection.nodeTable = new NodeTable(dbConnection.conn, "nodetable");
    Table edgeTable = new Table(dbConnection.conn, "edge");
    edgeTable.setHeaders(
        new ArrayList<>(List.of("node1", "node2")), new ArrayList<>(List.of("String", "String")));
    edgeTable.init(false);
    dbConnection.edgeTable = edgeTable;
  }
}
