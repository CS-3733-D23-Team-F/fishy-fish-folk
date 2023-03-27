package edu.wpi.fishfolk.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;
import javafx.geometry.Point2D;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int size;
  double[][] adjMat;
  Node[] nodes;
  HashMap<String, Integer> id2idx; // string id to index in nodes array and adjacency matrix

  private int lastIdx;

  public Graph(int size) {

    this.size = size;
    adjMat = new double[size][size];
    nodes = new Node[size];
    id2idx = new HashMap<>(size * 4 / 3 + 1); // default load factor is 75% = 3/4

    lastIdx = 0;
  }

  public boolean addNode(Node n) {

    if (id2idx.containsKey(n.id)) { // duplicates
      return false;
    }

    id2idx.put(n.id, lastIdx);
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

  public boolean addEdge(String n1, String n2) {

    Integer idx1 = id2idx.get(n1);
    Integer idx2 = id2idx.get(n2);

    Point2D point1 = nodes[idx1].point;
    Point2D point2 = nodes[idx2].point;

    if (idx1 != null && idx2 != null) {
      adjMat[idx1][idx2] = point1.distance(point2);
      adjMat[idx2][idx1] = point1.distance(point2);
      ;
      return true;

    } else {
      return false;
    }
  }

  public Path bfs(String start, String end) {

    // check for correctness by inputting edge list into https://graphonline.ru/en/

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    boolean[] visited = new boolean[size];

    LinkedList<String> queue = new LinkedList<>(); // queue of next nodes to look at in bfs

    String[] previous =
        new String[size]; // used to store the ids of the previous node in order to retrace the path

    queue.add(start);

    while (!queue.isEmpty()) {

      String cur = queue.removeFirst();

      if (cur.equals(end)) { // reached end
        Path path = new Path();

        path.addFirst(end);

        while (!cur.equals(start)) { // retrace path from the end to the start
          String prev = previous[id2idx.get(cur)];
          path.addFirst(prev);
          cur = prev;
        }

        return path;
      }

      if (!visited[id2idx.get(cur)]) { // found a new node

        for (int other = 0; other < size; other++) {

          if (adjMat[id2idx.get(cur)][other] != 0.0 // Fixed to 0.0 now that matrix is edge weights
              && !visited[other]) { // 1 means cur is connected to other
            String next = nodes[other].id;
            previous[id2idx.get(next)] = cur;
            queue.addLast(next);
          }

          visited[id2idx.get(cur)] = true;
        }
      }
    }

    return null;
  }

  public Path AStar(String start, String end) {

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    double[] fromStart = new double[size];
    double[] heuristic = new double[size];
    boolean[] visited = new boolean[size]; // true means found the shortest path to this node
    int[] lastVisited = new int[size];
    int current_node = id2idx.get(start);
    double pathWeight = 0;
    double min = -1;

    // initialize distance from start node and heuristic arrays
    // distance from start node: 0 for start, -MIN_INT for all others
    // heuristic: approximate distances from all points to the target endpoint
    String endFloor = nodes[id2idx.get(end)].floor;

    for (int node = 0; node < size; node++) {

      fromStart[node] = Integer.MIN_VALUE;

      if (nodes[node].floor.equals(endFloor)) {
        heuristic[node] = distance(nodes[node].id, end);
      }

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
      min = Integer.MAX_VALUE;

      for (int other = 0; other < size; other++) {

        double cost = fromStart[other] + heuristic[other];

        if (cost >= 0 && cost < min && !visited[other]) {
          min = cost;
          current_node = other;
        }
      }
    }

    Path path = new Path();

    while (!(current_node == id2idx.get(start))) { // retrace path from the end to the start
      path.addFirst(nodes[current_node].id);
      current_node = lastVisited[current_node];
    }
    path.addFirst(start);

    System.out.println(pathLength(path));

    return path;
  }

  public double distance(String n1, String n2) {
    return nodes[id2idx.get(n1)].point.distance(nodes[id2idx.get(n2)].point);
  }

  public double pathLength(Path p) {

    LinkedList<String> nodesLst = (LinkedList<String>) p.getNodes().clone();
    String prev = nodesLst.removeFirst();
    double length = 0;

    while (!nodesLst.isEmpty()) {
      String cur = nodesLst.removeFirst();
      length += distance(prev, cur);
      prev = cur;
    }

    return length;
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

    for (String id : id2idx.keySet()) {

      System.out.println(id);
    }
  }
}
