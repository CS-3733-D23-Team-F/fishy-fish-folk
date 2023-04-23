package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.util.NodeType;
import java.util.ArrayList;
import java.util.LinkedList;

public class DFS extends Pathfinder {

  public DFS(Graph g) {
    super(g);
  }

  /**
   * Finds the path between to locations using the Depth First Search algorithm
   *
   * @param start The ID of the starting location
   * @param end The ID of the ending location
   * @param stairs Boolean that indicates if stairs are allowed, false for no stairs
   * @return ArrayList of Paths, each item in the list is a path on a separate floor
   */
  public ArrayList<Path> pathfind(int start, int end, boolean stairs) {

    if (!graph.containsNode(start) || !graph.containsNode(end)) {
      return null;
    }

    LinkedList<Integer> stack = new LinkedList<>(); // queue of next nodes to look at in bfs

    int size = graph.getSize();

    int[] previous = new int[size];
    boolean[] visited = new boolean[size];

    int cur = start;
    stack.add(start);

    int searched = 0;
    boolean foundNew = false;

    while (!(cur == end)) {

      cur = stack.getLast();

      searched = 0;
      foundNew = false;

      while (!foundNew && !(searched == size - 1)) {
        // searched adjacent to cur and not yet visited
        if (graph.adjacent(graph.id2idx(cur), searched) && !visited[searched]) {

          // proceed if stairs allowed, not at stairs
          // ignore stair condition at start
          if (stairs
              || !graph.getNodeFromIdx(searched).containsType(NodeType.STAI)
              || graph.getNodeFromIdx(searched).getNodeID() == start) {

            int next = graph.getNodeFromIdx(searched).getNodeID();
            previous[graph.id2idx(next)] = graph.id2idx(cur);
            stack.addLast(next);
            foundNew = true;
          }
        }

        searched++;
      }
      visited[graph.id2idx(cur)] = true;

      if (!foundNew) {
        stack.removeLast();
      }
    }

    return backtrack(graph.id2idx(end), start, previous);
  }
}
