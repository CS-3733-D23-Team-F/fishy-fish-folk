package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.database.Table;
import java.util.*;
import javafx.geometry.Point2D;
import lombok.Setter;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int size;
  double[][] adjMat;
  Node[] nodes;

  ArrayList<Integer> elevatorIDs;
  HashMap<Integer, Integer> id2idx; // string id to index in nodes array and adjacency matrix
  private int lastIdx;

  @Setter NodeTable nodeTable;
  @Setter Table edgeTable;

  public Graph(NodeTable nodeTable, Table edgeTable) {

    this.size = nodeTable.size();

    adjMat = new double[size][size];
    nodes = new Node[size];
    id2idx = new HashMap<>(size * 4 / 3 + 1); // default load factor is 75% = 3/4
    elevatorIDs = new ArrayList<Integer>();

    lastIdx = 0;

    this.nodeTable = nodeTable;
    this.edgeTable = edgeTable;

    populate();
  }

  public void populate() {

    Node[] nodes = nodeTable.getAllNodes();
    int nodeCount = 0, edgeCount = 0;

    for (Node node : nodes) {
      if (addNode(node)) nodeCount++;
    }

    ArrayList<String>[] edges = edgeTable.getAll();

    // index 0 has the headers
    for (int i = 1; i < edges.length; i++) {
      int n1 = Integer.parseInt(edges[i].get(0));
      int n2 = Integer.parseInt(edges[i].get(1));
      if (addEdge(n1, n2)) edgeCount++;
    }

    System.out.println(nodeCount + " " + edgeCount);

    System.out.println(
        "[Graph.populate]: Populated graph with "
            + nodes.length
            + " nodes and "
            + edges.length
            + " edges.");
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

    if (n.type.equals(NodeType.ELEV)) {
      elevatorIDs.add(lastIdx);
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

  public boolean addEdge(int n1, int n2) {

    Integer idx1 = id2idx.get(n1);
    Integer idx2 = id2idx.get(n2);
    int adjust = 0;

    if (idx1 != null && idx2 != null) {

      // Adds edges between all nodes in a elevator on different floors
      if (nodes[idx1].type.equals(NodeType.ELEV) && nodes[idx2].type.equals(NodeType.ELEV)) {
        String eleLetter = nodes[idx1].longName.substring(8, 10); // Elevator letter identifier
        for (int other = 0; other < elevatorIDs.size(); other++) {

          if (nodes[elevatorIDs.get(other)]
                  .longName
                  .substring(8, 10)
                  .equals(eleLetter) // letters match
              && (adjMat[idx1][elevatorIDs.get(other)] == 0) // No edge currently
              && !nodes[elevatorIDs.get(other)].floor.equals(nodes[idx1].floor)) { // Not itself

            adjMat[idx1][elevatorIDs.get(other)] = 250;
            adjMat[elevatorIDs.get(other)][idx1] = 250;
            nodes[idx1].degree++;
            nodes[elevatorIDs.get(other)].degree++;
          }
        }
        return true;
      }

      // Add weights to stairs
      if (nodes[idx1].type.equals(NodeType.STAI) && nodes[idx2].type.equals(NodeType.STAI)) {
        adjust = 250;
      }

      // System.out.println(edge.id);

      Point2D point1 = nodes[idx1].point;
      Point2D point2 = nodes[idx2].point;

      adjMat[idx1][idx2] = point1.distance(point2) + adjust;
      adjMat[idx2][idx1] = point1.distance(point2) + adjust;
      nodes[idx1].degree++;
      nodes[idx2].degree++;

      return true;

    } else {
      return false;
    }
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

  public ArrayList<Path> AStar(int start, int end) {

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

          if (heuristic[other] < -501 && nodes[other].floor.equals(endFloor)) {
            heuristic[other] = nodes[other].point.distance(endPoint) - 500;
          } else if (heuristic[other] < -501) {
            heuristic[other] = nodes[other].point.distance(endPoint);
          }

          double cost = fromStart[other] + heuristic[other];

          if (cost >= -501 && cost < min) {
            min = cost;
            current_node = other;
          }
        }
      }
    }

    String currentFloor = "";
    ArrayList<Path> paths = new ArrayList<>();
    int numpaths = -1;

    // retrace path from the end to the start
    while (!(current_node == id2idx.get(start))) {

      // start new path when hitting a new floor
      if (!nodes[current_node].floor.equals(currentFloor)) {
        Path path = new Path();
        currentFloor = nodes[current_node].floor;
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;
      }

      // add current node at beginning of first path
      paths.get(0).addFirst(nodes[current_node].nid, nodes[current_node].point);

      current_node = lastVisited[current_node];
    }
    // add start node to beginning of first path
    paths.get(0).addFirst(start, nodes[current_node].point);

    System.out.println(paths.toString());

    // path.addFirst(start, nodes[id2idx.get(start)].point);

    // System.out.println(path.pathLength());

    // System.out.println(path.getDirections());

    return paths;
  }

  public double distance(String n1, String n2) {
    return nodes[id2idx.get(n1)].point.distance(nodes[id2idx.get(n2)].point);
  }

  public void speedTest(int n, boolean verbose) {
    /**
     * // record length of path and time taken to find the path between pairs of random points
     *
     * <p>Random rng = new Random(); double[][] dist_time = new double[n][2];
     *
     * <p>for (int i = 0; i < n; i++) {
     *
     * <p>try { int n1 = nodes[rng.nextInt(size)].nid; int n2 = nodes[rng.nextInt(size)].nid;
     *
     * <p>//TODO Make pathlength take is list of paths long start = System.nanoTime();
     * dist_time[i][0] = AStar(n1, n2).pathLength(); dist_time[i][1] = (System.nanoTime() - start) /
     * 1000.0; } catch (Exception e) { System.out.println(i + " " + e.getMessage()); } }
     *
     * <p>Arrays.sort( dist_time, new Comparator<double[]>() { @Override public int compare(double[]
     * dt1, double[] dt2) { return Double.compare(dt1[0], dt2[0]); } });
     *
     * <p>if (verbose) { for (int i = 0; i < n; i++) { System.out.println( String.format("d: %.2f" +
     * " t (us): %.1f", dist_time[i][0], dist_time[i][1])); } }
     *
     * <p>double totalDist = 0, totalTime = 0; for (int i = 0; i < n; i++) { totalDist +=
     * dist_time[i][0]; totalTime += dist_time[i][1]; }
     *
     * <p>System.out.println( String.format("avg d: %.2f" + " avg t (us): %.1f", totalDist / n,
     * totalTime / n)); }
     *
     * <p>public void print() {
     *
     * <p>for (int id : id2idx.keySet()) {
     *
     * <p>System.out.println(id); } }
     *
     * <p>public void countNull() {
     *
     * <p>for (int i = 0; i < size; i++) { try { String x = nodes[i].id; } catch (Exception e) {
     * System.out.println(i + "-" + e.getMessage()); } }
     */
  }

  public ArrayList<String> adjacentNodes(int nodeId) {

    ArrayList<String> neighbors = new ArrayList<>();
    int idx = id2idx.get(nodeId);
    for (int i = 0; i < size; i++) {
      if (adjMat[idx][i] != 0) {
        neighbors.add(nodes[i].id);
      }
    }
    return neighbors;
  }
}
