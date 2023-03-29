package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.EdgeTable;
import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.pathfinding.Edge;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.geometry.Point2D;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args);

    try {

      System.out.println("\n--- ESTABLISHING DATABASE CONNECTION ---\n");

      Fdb fdb = new Fdb();
      Connection db = fdb.connect("teamfdb", "teamf", "teamf60");
      db.setSchema("test");
      System.out.println("[Main]: Current schema: " + db.getSchema() + ".");

      System.out.println("\n--- TESTING NODE TABLE ---\n");

      NodeTable nodeTable = new NodeTable(db, "nodetable");
      if (fdb.createTable(db, nodeTable.getTableName())) {
        nodeTable.addHeaders();
      }

      nodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv");

      Node existingNode = nodeTable.getNode("CCONF001L1");
      Node newNode =
          new Node(
              "CDEPT999L1",
              new Point2D(1980, 844),
              "L1",
              "Tower",
              NodeType.DEPT,
              "Day Surgery Family Waiting Floor L1",
              "Department C002L1");
      Node newNodeUpdated =
          new Node(
              "CDEPT999L1",
              new Point2D(1980, 844),
              "L2",
              "Space",
              NodeType.DEPT,
              "Night Surgery Family Waiting Floor L1",
              "Department C002L1");

      nodeTable.insertNode(existingNode);
      nodeTable.insertNode(newNode);

      nodeTable.updateNode(newNodeUpdated);

      nodeTable.removeNode(existingNode);

      nodeTable.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/L1NodesOutput.csv");

      System.out.println("\n--- TESTING EDGE TABLE ---\n");

      EdgeTable edgeTable = new EdgeTable(db, "edgetable");
      if (fdb.createTable(db, edgeTable.getTableName())) {
        edgeTable.addHeaders();
      }

      edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Edges.csv");

      Edge existingEdge = edgeTable.getEdge("CCONF002L1_WELEV00HL1");

      Edge newEdge = new Edge("CDEPT999L1_CDEPT999L1", "CDEPT002L1", "CDEPT003L1");
      Edge newEdgeUpdated = new Edge("CDEPT999L1_CDEPT999L1", "CDEPT002L1AAA", "CDEPT003L1AAA");

      edgeTable.insertEdge(existingEdge);
      edgeTable.insertEdge(newEdge);

      edgeTable.updateEdge(newEdgeUpdated);

      edgeTable.removeEdge(existingEdge);

      edgeTable.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/L1EdgesOutput.csv");

      fdb.disconnect(db);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
