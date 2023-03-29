package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.EdgeTable;
import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.pathfinding.Edge;
import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
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

      NodeTable nodeTable = new NodeTable(db, "nodetable2");

      if (fdb.createTable(db, nodeTable.getTableName())) {
        nodeTable.addHeaders();
      }

      nodeTable.importCSV();

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

      nodeTable.exportCSV();

      System.out.println("\n--- TESTING EDGE TABLE ---\n");

      EdgeTable edgeTable = new EdgeTable(db, "edgetable2");

      if (fdb.createTable(db, edgeTable.getTableName())) {
        edgeTable.addHeaders();
      }

      nodeTable.setEdgeTable(edgeTable);
      edgeTable.setNodeTable(nodeTable);

      edgeTable.importCSV();
      // edgeTable.testQuery();

      Edge existingEdge = edgeTable.getEdge("CCONF002L1_WELEV00HL1");

      Edge newEdge = new Edge("CDEPT002L1", "CDEPT003L1");
      Edge newEdgeUpdated = new Edge("CDEPT002L1AAA", "CDEPT003L1AAA");

      edgeTable.insertEdge(existingEdge);
      edgeTable.insertEdge(newEdge);

      edgeTable.updateEdge(newEdgeUpdated);

      edgeTable.removeEdge(existingEdge);

      edgeTable.exportCSV();

      fdb.disconnect(db);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public static Graph loadMapFromCSV() {

    LinkedList<Node> nodeLst = new LinkedList<>();

    int count = 0;

    HashMap<String, String> idTranslation = new HashMap<>();

    HashMap<String, Integer> nodeTypeCounts =
        new HashMap<>(); // key: nodeType + floor, value is count

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        Point2D point = new Point2D(Integer.parseInt(values[1]), Integer.parseInt(values[2]));

        String floor = values[3];

        String building = values[4];

        NodeType type = NodeType.valueOf(values[5]);
        Integer prevCount = nodeTypeCounts.get(type.toString() + floor);
        if (prevCount != null) {
          nodeTypeCounts.put(type.toString() + floor, prevCount + 1);
        } else {
          nodeTypeCounts.put(type.toString() + floor, 1);
        }

        String longName = values[6];

        String shortName = values[7];

        // create id from scratch and map given id (which is also used in edges csv) to correct id

        String nodeNum;

        if (type == NodeType.ELEV) {
          nodeNum = "00" + shortName.substring(9, 10);

        } else {
          nodeNum = "00" + nodeTypeCounts.get(type.toString() + floor);
          nodeNum = nodeNum.substring(nodeNum.length() - 3);
        }
        String id = "f" + type.toString() + nodeNum + floor;

        idTranslation.put(values[0], id); // map given id to correct id

        Node node = new Node(id, point, floor, building, type, longName, shortName);

        nodeLst.add(node);

        count++;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Graph graph = new Graph();

    for (Node n : nodeLst) {
      graph.addNode(n);
    }

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Edges.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String n1 = idTranslation.get(values[1]);
        String n2 = idTranslation.get(values[2]);

        // System.out.println(n1 + "-" + n2); // edge list for https://graphonline.ru/en/

        graph.addEdge(new Edge(n1, n2));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // graph.print();
    return graph;
  }

  // shortcut: psvm

}
