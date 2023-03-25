package edu.wpi.fishfolk;

import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.io.*;
import java.util.LinkedList;
import javafx.geometry.Point2D;

public class Main {
  public static void main(String[] args) {

    LinkedList<Node> nodes = new LinkedList<>();
    int count = 0;

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String id = values[0];
        id = "f" + id.substring(1);

        Point2D point = new Point2D(Integer.parseInt(values[1]), Integer.parseInt(values[2]));

        String floor = values[3];

        String building = values[4];

        NodeType type = NodeType.valueOf(values[5]);

        String longName = values[6];

        String shortName = values[7];

        Node node = new Node(id, point, floor, building, type, longName, shortName);

        nodes.add(node);
        count++;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Graph graph = new Graph(count);

    for (Node n : nodes) {
      graph.addNode(n);
    }

    try (BufferedReader br =
        new BufferedReader(new FileReader("src/main/resources/edu/wpi/fishfolk/csv/L1Edges.csv"))) {

      String line = br.readLine(); // ignore column headers which are on the first line
      while ((line = br.readLine()) != null) {

        String[] values = line.split(",");

        String n1 = values[1];
        String n2 = values[2];

        graph.addEdge(n1, n2);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    graph.print();

    // Fapp.launch(Fapp.class, args);
  }

  // shortcut: psvm

}
