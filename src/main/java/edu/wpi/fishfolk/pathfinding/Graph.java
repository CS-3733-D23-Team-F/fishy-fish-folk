package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.database.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.geometry.Point2D;
import lombok.Setter;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int size;
  double[][] adjMat;
  Node[] nodes;
  HashMap<Integer, Integer> id2idx; // string id to index in nodes array and adjacency matrix
  private int lastIdx;

  HashMap<String, String> elevatorFloors;

  @Setter NodeTable nodeTable;
  @Setter Table edgeTable;

  public Graph(NodeTable nodeTable, Table edgeTable) {

    this.size = nodeTable.size();

    adjMat = new double[size][size];
    nodes = new Node[size];
    id2idx = new HashMap<>(size * 4 / 3 + 1); // default load factor is 75% = 3/4

    elevatorFloors = new HashMap<>(64); // high estimate

    lastIdx = 0;

    this.nodeTable = nodeTable;
    this.edgeTable = edgeTable;
  }

  public void populate() {

    Node[] nodes = nodeTable.getAllNodes();

    for (Node node : nodes) {
      addNode(node);
    }

    ArrayList<String>[] edges = edgeTable.getAll();

    for (ArrayList<String> edgeData : edges) {
      Edge edge = new Edge();
      edge.construct(edgeData);
      addEdge(edge);
    }
  }

  public void resize(int newSize) {

    double[][] newAdjMat = new double[newSize][newSize];
    Node[] newNodes = new Node[newSize];
    HashMap<Integer, Integer> newid2idx = new HashMap<>(newSize * 4 / 3 + 1);

    // copy adjmat
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) { // take advantage of symmetry across main diagonal
        newAdjMat[i][j] = adjMat[i][j];
        newAdjMat[j][i] = adjMat[i][j];
      }
    }

    // copy array of nodes
    for (int i = 0; i < size; i++) {
      newNodes[i] = nodes[i];
    }

    // copy id2idx map
    for (Map.Entry<Integer, Integer> entry : id2idx.entrySet()) {
      newid2idx.put(entry.getKey(), entry.getValue());
    }

    size = newSize;
  }

  public boolean addNode(Node n) {

    if (id2idx.containsKey(n.id)) { // duplicates
      return false;
    }

    String elevatorLet =
        n.longName.substring(
            8, 10); // Takes the elevator letter from name (Ex: "Elevator A" -> "A")

    // Store floors seperated by commas with associated Elevator letter
    if (elevatorFloors.containsKey(elevatorLet)) {
      elevatorFloors.put(elevatorLet, elevatorFloors.get(elevatorLet) + "," + n.floor);
    } else {
      elevatorFloors.put(elevatorLet, "," + n.floor);
    }

    id2idx.put(n.nid, lastIdx);
    nodes[lastIdx] = n;

    lastIdx++;
    return true;
  }

  public boolean removeNode(String id) {

    Integer idx = id2idx.get(id);

    if (id2idx.remove(id) == null) { // requested node is not in graph
      return false;
    }

    nodes[idx] = null; // remove from nodes array
    return false;
  }

  public boolean addEdge(Edge edge) {

    Integer idx1 = id2idx.get(edge.node1);
    Integer idx2 = id2idx.get(edge.node2);

    if (idx1 != null && idx2 != null) {

      // System.out.println(edge.id);

      Point2D point1 = nodes[idx1].point;
      Point2D point2 = nodes[idx2].point;

      adjMat[idx1][idx2] = point1.distance(point2);
      adjMat[idx2][idx1] = point1.distance(point2);
      nodes[idx1].degree++;
      nodes[idx2].degree++;

      return true;

    } else {
      return false;
    }
  }

  public Point2D nearestElevator(int node1, int node2) {

    String node1Floor = nodes[id2idx.get(node1)].floor;
    String node2Floor = nodes[id2idx.get(node2)].floor;
    Point2D nodePoint = nodes[id2idx.get(node1)].point;
    double minDist = -1;
    int ID = 0;
    String elvatorLet = "";

    for (int other = 0; other > size; other++) {
      if (nodes[other].type.equals(NodeType.ELEV)) {

        elvatorLet = nodes[other].longName.substring(8, 10);

        if (elevatorFloors
                .get(elvatorLet)
                .contains("," + node2Floor) // Elevator reaches floor of both nodes
            && (nodes[other].floor.equals(node1Floor))) {

          if ((minDist < 0)
              || minDist
                  > nodePoint.distance(nodes[other].point)) { // New minimum distance Elevator
            minDist = nodePoint.distance(nodes[other].point);
            ID = other;
          }
        }
      }
    }
    return nodes[ID].point;
  }

  public Path bfs(int start, int end) {

    // check for correctness by inputting edge list into https://graphonline.ru/en/

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    boolean[] visited = new boolean[size];

    LinkedList<Integer> queue = new LinkedList<>(); // queue of next nodes to look at in bfs

    int[] previous =
        new int[size]; // used to store the ids of the previous node in order to retrace the path

    queue.add(start);

    while (!queue.isEmpty()) {

      int cur = queue.removeFirst();

      if (cur == end) { // reached end
        Path path = new Path();

        path.addFirst(end, nodes[id2idx.get(end)].point);

        while (cur != start) { // retrace path from the end to the start
          int prev = previous[id2idx.get(cur)];
          path.addFirst(prev, nodes[id2idx.get(prev)].point);
          cur = prev;
        }

        return path;
      }

      if (!visited[id2idx.get(cur)]) { // found a new node

        for (int other = 0; other < size; other++) {

          if (adjMat[id2idx.get(cur)][other] != 0.0 // Fixed to 0.0 now that matrix is edge weights
              && !visited[other]) { // 1 means cur is connected to other
            int next = nodes[other].nid;
            previous[id2idx.get(next)] = cur;
            queue.addLast(next);
          }

          visited[id2idx.get(cur)] = true;
        }
      }
    }

    return null;
  }

  public Path AStar(int start, int end) {

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    double[] fromStart = new double[size];
    double[] heuristic = new double[size];
    boolean[] visited = new boolean[size]; // true means found the shortest path to this node
    int[] lastVisited = new int[size];
    int current_node = id2idx.get(start);

    // initialize distance from start node and heuristic arrays
    // distance from start node: 0 for start, -MIN_INT for all others
    // heuristic: approximate distances from all points to the target endpoint
    String endFloor = nodes[id2idx.get(end)].floor;
    Point2D endPoint = nodes[id2idx.get(end)].point;

    Point2D closeEleStart = nearestElevator(start, end);
    Point2D closeEleEnd = nearestElevator(start, end);

    for (int node = 0; node < size; node++) {

      fromStart[node] = Integer.MIN_VALUE;
      heuristic[node] = Integer.MIN_VALUE;

      // TODO heuristic for two nodes on different floors

    }

    fromStart[id2idx.get(start)] = 0;

    while (!(current_node == id2idx.get(end))) {

      visited[current_node] = true;

      // System.out.println(nodes[current_node].id);

      // update adjacent nodes
      for (int node = 0; node < size; node++) {
        if (!(adjMat[current_node][node] == 0.0) // Check if nodes are adjacent
            && !visited[node]) { // Make sure unvisited node

          // if going to adjacent node via current is better than the previous path to node, update
          // fromStart[node]
          if (fromStart[node] < 0
              || fromStart[current_node] + adjMat[current_node][node] < fromStart[node]) {

            fromStart[node] = fromStart[current_node] + adjMat[current_node][node];
            lastVisited[node] = current_node;
          }
        }
      }

      // choose the next node based off of distance to start and heuristic
      double min = Integer.MAX_VALUE;

      for (int other = 0; other < size; other++) {

        if (!visited[other] && fromStart[other] > -1) {

          if (heuristic[other] < -1 && nodes[other].floor.equals(endFloor)) {
            heuristic[other] = nodes[other].point.distance(endPoint);
          } else if (heuristic[other] < -1) {
            heuristic[other] =
                nodes[other].point.distance(closeEleStart)
                    + nodes[other].point.distance(closeEleEnd);
          }

          double cost = fromStart[other] + heuristic[other];

          if (cost >= 0 && cost < min) {
            min = cost;
            current_node = other;
          }
        }
      }
    }

    Path path = new Path();

    while (!(current_node == id2idx.get(start))) { // retrace path from the end to the start
      path.addFirst(nodes[current_node].nid, nodes[current_node].point);
      current_node = lastVisited[current_node];
    }
    path.addFirst(start, nodes[id2idx.get(start)].point);

    System.out.println(path.pathLength());

    System.out.println(path.getDirections());

    return path;
  }

  public double distance(String n1, String n2) {
    return nodes[id2idx.get(n1)].point.distance(nodes[id2idx.get(n2)].point);
  }

  public void print() {
    /*

    for (int row = 0; row < adjMat.length; row++) {
      System.out.println();
      for (int col = 0; col < adjMat.length; col++) {
        System.out.print(adjMat[row][col]);
      }
    }

     */

    for (int id : id2idx.keySet()) {

      System.out.println(id);
    }
  }
}
