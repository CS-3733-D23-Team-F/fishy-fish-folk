package edu.wpi.fishfolk;


import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.pathfinding.*;
import java.sql.SQLException;

import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.Table;
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
    Table edgeTable = new Table(fdb.db, "edge", false);

    Graph g = new Graph(nodeTable, edgeTable);
    g.populate();

    System.out.println(g.AStar(2315, 675)); // two random nodes that exist

  }
}
