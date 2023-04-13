package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.MicroNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BFS implements IPathfinding {

  int size;
  double[][] adjMat;
  MicroNode[] unodes;
  HashMap<Integer, Integer> id2idx;

  public ArrayList<Path> pathfind(int start, int end, Graph graph, boolean stairs) {
    this.size = graph.size;
    this.adjMat = graph.adjMat;
    this.unodes = graph.unodes;
    this.id2idx = graph.id2idx;

    // check for correctness by inputting edge list into https://graphonline.ru/en/

    if (!id2idx.containsKey(start) || !id2idx.containsKey(end)) {
      return null;
    }

    boolean[] visited = new boolean[size];

    LinkedList<Integer> queue = new LinkedList<>(); // queue of next nodes to look at in bfs

    int[] previous =
        new int[size]; // used to store the ids of the previous node in order to retrace the path

    queue.add(start);

    int cur = start;

    while (!(cur == end)) {

      cur = queue.removeFirst();

      if (!visited[id2idx.get(cur)]) { // found a new node

        for (int other = 0; other < size; other++) {

          if (adjMat[id2idx.get(cur)][other] != 0.0 // Fixed to 0.0 now that matrix is edge weights
              && !visited[other]) { // 1 means cur is connected to other
            int next = unodes[other].nid;
            previous[id2idx.get(next)] = cur;
            queue.addLast(next);
          }

          visited[id2idx.get(cur)] = true;
        }
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
