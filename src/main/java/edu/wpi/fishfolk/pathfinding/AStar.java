package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry.Node;
import java.util.ArrayList;

public class AStar extends Pathfinder {

  public AStar(Graph g) {
    super(g);
  }

  public ArrayList<Path> pathfind(int start, int end, boolean stairs) {

    if (!graph.containsNode(start) || !graph.containsNode(end)) {
      return null;
    }

    int size = graph.getSize();
    // HashMap<Integer, Integer> id2idx = graph.getId2idx();
    // MicroNode[] unodes = graph.getUnodes();
    // double[][] adjMat = graph.getAdjMat();

    double[] fromStart = new double[size];
    double[] heuristic = new double[size];
    boolean[] visited = new boolean[size]; // true means found the shortest path to this node
    int[] lastVisited = new int[size];
    int currentNodeIdx = graph.id2idx(start);

    // initialize distance from start node and heuristic arrays
    // distance from start node: 0 for start, -MIN_INT for all others
    // heuristic: approximate distances from all points to the target endpoint
    Node endNode = graph.getNodeFromID(end);

    for (int node = 0; node < size; node++) {

      fromStart[node] = Integer.MIN_VALUE;
      heuristic[node] = Integer.MIN_VALUE;
    }

    fromStart[graph.id2idx(start)] = 0;

    // search until finding end node
    while (!(currentNodeIdx == graph.id2idx(end))) {

      visited[currentNodeIdx] = true;

      // update adjacent nodes
      for (int node = 0; node < size; node++) {
        if (graph.adjacent(currentNodeIdx, node)
            && !visited[node]) { // Make sure adjacent & unvisited node

          // if going to adjacent node via current is better than the previous path to node,
          // update fromStart[node]
          if (fromStart[node] < 0
              || fromStart[currentNodeIdx] + graph.getEdgeWeight(currentNodeIdx, node)
                  < fromStart[node]) {

            // in order to proceed:
            // check stairs condition: must NOT (stairs not allowed and currently at stairs)
            // or currently at start, where you ignore the stairs condition
            if (!(!stairs && graph.getNodeFromIdx(currentNodeIdx).containsType(NodeType.STAI))
                || currentNodeIdx == graph.id2idx(start)) {
              fromStart[node] =
                  fromStart[currentNodeIdx] + graph.getEdgeWeight(currentNodeIdx, node);
              lastVisited[node] = currentNodeIdx;
              // System.out.println(unodes[node].nid);
            }
          }
        }
      }

      // choose the next node based off of distance to start and heuristic
      double min = Integer.MAX_VALUE;

      for (int other = 0; other < size; other++) {

        if (!visited[other] && fromStart[other] > -1) {

          Node otherNode = graph.getNodeFromIdx(other);

          // TODO figure out why check heuristic < 500?
          if (heuristic[other] < -501 && otherNode.getFloor().equals(endNode.getFloor())) {
            heuristic[other] = otherNode.getPoint().distance(endNode.getPoint()) - 500;

          } else if (heuristic[other] < -501) {
            heuristic[other] = otherNode.getPoint().distance(endNode.getPoint());
          }

          double cost = fromStart[other] + heuristic[other];

          if (cost >= -501 && cost < min) {
            min = cost;
            currentNodeIdx = other;
          }
        }
      }
    }

    return backtrack(currentNodeIdx, start, lastVisited);
  }
}
