package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.pathfinding.*;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args);

    try {
      System.out.println("\n--- ESTABLISHING DATABASE CONNECTION ---\n");

      Fdb fdb = new Fdb();
      Connection db = fdb.connect("teamfdb", "teamf", "teamf60");
      db.setSchema("test");
      System.out.println("[Main]: Current schema: " + db.getSchema() + ".");

      NodeTable nodeTable = new NodeTable(db, "nodetable2");

      if (fdb.createTable(db, nodeTable.getTableName())) {
        nodeTable.addHeaders();
      }

      nodeTable.importCSV();

      EdgeTable edgeTable = new EdgeTable(db, "edgetable2");

      if (fdb.createTable(db, edgeTable.getTableName())) {
        edgeTable.addHeaders();
      }

      nodeTable.setEdgeTable(edgeTable);
      edgeTable.setNodeTable(nodeTable);

      edgeTable.importCSV();

      // get data from db into graph

      Graph graph = new Graph(nodeTable, edgeTable);

      graph.populate();

      Path path = graph.AStar("fDEPT001L1", "fSERV001L1");

      System.out.println(path.toString());
      System.out.println(path.getDirections());

      fdb.disconnect(db);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // shortcut: psvm

}
