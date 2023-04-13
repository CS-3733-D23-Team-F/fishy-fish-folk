package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.MicroNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class DFS implements IPathfinding {

  int size;
  double[][] adjMat;
  MicroNode[] unodes;
  HashMap<Integer, Integer> id2idx;

  /**
   * Finds the path between to locations using the Depth First Search algorithm
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

    LinkedList<Integer> stack = new LinkedList<>(); // queue of next nodes to look at in bfs

    int[] previous = new int[size];

    int cur = start;

    stack.add(start);

    int searched = 0;
    boolean foundNew = false;

    boolean[] visited = new boolean[size];

    while (!(cur == end)) {

      cur = stack.getLast();

      searched = 0;
      foundNew = false;

      while (!foundNew && !(searched == size - 1)) {
        if (adjMat[id2idx.get(cur)][searched] != 0.0 // Fixed to 0.0 now that matrix is edge weights
            && !visited[searched]) {
          if (stairs
              || !unodes[searched].containsType(NodeType.STAI)
              || unodes[searched].nid == end) {
            int next = unodes[searched].nid;
            previous[id2idx.get(next)] = cur;
            stack.addLast(next);
            foundNew = true;
          }
        }

        searched++;
      }
      visited[id2idx.get(cur)] = true;

      if (!foundNew) {
        stack.removeLast();
      }
    }

    String currentFloor = "";
    ArrayList<Path> paths = new ArrayList<>();
    int numpaths = -1;

    int prev = cur;

    while (cur != start) {

      // start new path when hitting a new floor
      if (!unodes[id2idx.get(cur)].floor.equals(currentFloor)) {
        Path path = new Path();
        currentFloor = unodes[id2idx.get(cur)].floor;
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;
      }

      paths.get(0).addFirst(prev, unodes[id2idx.get(prev)].point);
      prev = previous[id2idx.get(cur)];
      cur = prev;
    }

    paths.get(0).addFirst(cur, unodes[id2idx.get(cur)].point);

    return paths;
  }
}
