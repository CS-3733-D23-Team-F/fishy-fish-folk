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

    double[] shortestDist = new double[size];
    boolean[] RoomVisited = new boolean[size];
    int[] lastVisited = new int[size];
    int current_node = id2idx.get(start);
    Point2D endPoint = nodes[current_node].point;
    double pathWeight = 0;
    double min = -1;

    while (!(current_node == id2idx.get(end))) {

      // Sets the current node to check to the one with the least weight to reach
      min = -1; // -1 as stand in for infinity since can't have negative distance
      for (int dist = 0; dist < size; dist++) {
        if (((shortestDist[dist] < min) || (min <= 0))
            && !(shortestDist[dist] == 0)
            && !(RoomVisited[dist])) {
          current_node = dist;
          min = shortestDist[dist];
        }
      }

      for (int room = 0; room < size; room++) {
        if (!(adjMat[current_node][room] == 0.0) // Check if nodes are adjacent
            && !RoomVisited[room]) { // Make sure unvisited node
          pathWeight = shortestDist[current_node] + adjMat[current_node][room]; // distance of total path to node
          pathWeight = pathWeight + endPoint.distance(nodes[room].point); // A* adjusts weight by distance to end point
          if (shortestDist[room] == 0 || shortestDist[room] > pathWeight) {
            shortestDist[room] = pathWeight;
            lastVisited[room] = current_node;
          }
        }
      }

      RoomVisited[current_node] = true;
    }

    Path path = new Path();

    while (!(current_node == id2idx.get(start))) { // retrace path from the end to the start
      path.addFirst(nodes[current_node].id);
      current_node = lastVisited[current_node];
    }
    path.addFirst(start);

    return path;
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

    /*
    for (String id : id2idx.keySet()) {

      System.out.println(id);
    }

     */
  }
}
