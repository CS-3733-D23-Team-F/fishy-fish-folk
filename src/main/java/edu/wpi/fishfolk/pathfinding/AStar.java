package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.MicroNode;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Point2D;

public class AStar implements IPathfinding {

  int size;
  double[][] adjMat;
  MicroNode[] unodes;
  HashMap<Integer, Integer> id2idx;

  /**
   * Finds the path between to locations using the A* algorithm
   *
   * @param start The ID of the starting location
   * @param end The ID of the ending location
   * @param graph Graph which contains an adjacency matrix of all locations
   * @param stairs Boolean that indicates if stairs are allowed, false for no stairs
   * @return ArrayList of Paths, each item in the list is a path on a separate floor
   */
  public ArrayList<Path> pathfind(int start, int end, Graph graph, boolean stairs) {
    this.size = graph.size;
    this.adjMat = graph.adjMat;
    this.unodes = graph.unodes;
    this.id2idx = graph.id2idx;

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
    String endFloor = unodes[id2idx.get(end)].floor;
    Point2D endPoint = unodes[id2idx.get(end)].point;

    for (int node = 0; node < size; node++) {

      fromStart[node] = Integer.MIN_VALUE;
      heuristic[node] = Integer.MIN_VALUE;
    }

    fromStart[id2idx.get(start)] = 0;

    while (!(current_node == id2idx.get(end))) {

      visited[current_node] = true;

      // update adjacent nodes
      for (int node = 0; node < size; node++) {
        if (!(adjMat[current_node][node] == 0.0) // Check if nodes are adjacent
            && !visited[node]) { // Make sure unvisited node

          // if going to adjacent node via current is better than the previous path to node, update
          // fromStart[node]
          if (fromStart[node] < 0
              || fromStart[current_node] + adjMat[current_node][node] < fromStart[node]) {

            if ((stairs || !unodes[current_node].containsType(NodeType.STAI))
                || current_node == id2idx.get(start)) {
              fromStart[node] = fromStart[current_node] + adjMat[current_node][node];
              lastVisited[node] = current_node;
              System.out.println(unodes[node].nid);
            }
          }
        }
      }

      // choose the next node based off of distance to start and heuristic
      double min = Integer.MAX_VALUE;

      for (int other = 0; other < size; other++) {

        if (!visited[other] && fromStart[other] > -1) {

          if (heuristic[other] < -501 && unodes[other].floor.equals(endFloor)) {
            heuristic[other] = unodes[other].point.distance(endPoint) - 500;
          } else if (heuristic[other] < -501) {
            heuristic[other] = unodes[other].point.distance(endPoint);
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
      if (!unodes[current_node].floor.equals(currentFloor)) {
        Path path = new Path();
        currentFloor = unodes[current_node].floor;
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;
      }

      // add current node at beginning of first path
      paths.get(0).addFirst(unodes[current_node].nid, unodes[current_node].point);

      current_node = lastVisited[current_node];
    }
    // add start node to beginning of first path
    paths.get(0).addFirst(start, unodes[current_node].point);

    return paths;
  }
}
