package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.pathfinding.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args); // run ui

    Fdb fdb = new Fdb();
    try {
      fdb.db.setSchema("proto2db");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    NodeTable nodeTable = new NodeTable(fdb.db, "nodeTable");
    Table edgeTable = new Table(fdb.db, "edge");
    edgeTable.setHeaders(
        new ArrayList<>(List.of("node1", "node2")), new ArrayList<>(List.of("String", "String")));
    edgeTable.init(false);

    Graph g = new Graph(nodeTable, edgeTable);
    g.populate();

    System.out.println(g.AStar(1875, 2210)); // connected via 2315
    System.out.println(g.AStar(800, 2975));
  }
}
