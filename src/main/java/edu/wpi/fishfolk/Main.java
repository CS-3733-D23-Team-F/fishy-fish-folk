package edu.wpi.fishfolk;

import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.geometry.Point2D;

public class Main {
  public static void main(String[] args) {

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

    Graph graph = new Graph(count);

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

        graph.addEdge(n1, n2);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // graph.print();
    return graph;
  }

  // shortcut: psvm

}
