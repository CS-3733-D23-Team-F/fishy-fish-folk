package edu.wpi.fishfolk.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;

public class BFS extends Pathfinder {

  public BFS(Graph g) {
    super(g);
  }

  public ArrayList<Path> pathfind(int start, int end, boolean stairs) {

    if (!graph.containsNode(start) || !graph.containsNode(end)) {
      return null;
    }

    int size = graph.getSize();

    boolean[] visited = new boolean[size];
    int[] previous = new int[size];

    LinkedList<Integer> queue = new LinkedList<>(); // queue of next nodes to look at in bfs

    queue.add(start);

    int cur = start;

    while (!(cur == end)) {

      cur = queue.removeFirst();

      if (!visited[graph.id2idx(cur)]) { // found a new node

        for (int other = 0; other < size; other++) {

          if (graph.adjacent(graph.id2idx(cur), other) && !visited[other]) {

            // proceed if stairs allowed, not at stairs
            // ignore stair condition at start
            if (stairs
                || !graph.getNodeFromIdx(other).containsType(NodeType.STAI)
                || graph.getNodeFromIdx(other).getNodeID() == start) {

              int next = graph.getNodeFromIdx(other).getNodeID();
              previous[graph.id2idx(next)] = graph.id2idx(cur);
              queue.addLast(next);
            }
          }

          visited[graph.id2idx(cur)] = true;
        }
      }
    }
    System.out.println(graph.id2idx(end));
    return backtrack(graph.id2idx(end), start, previous);
  }
}
