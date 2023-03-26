package edu.wpi.fishfolk.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int size;
  int[][] adjMat;
  Node[] nodes;
  HashMap<String, Integer> id2idx; // string id to index in nodes array and adjacency matrix

  private int lastIdx;

  public Graph(int size) {

    this.size = size;
    adjMat = new int[size][size];
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

    if (idx1 != null && idx2 != null) {
      adjMat[idx1][idx2] = 1;
      adjMat[idx2][idx1] = 1;
      return true;

    } else {
      return false;
    }
  }

  public Path bfs(String start, String end) {

    //check for correctness by inputting edge list into https://graphonline.ru/en/

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

          if (adjMat[id2idx.get(cur)][other] == 1
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

  public void print() {

    /*
    for (String id : id2idx.keySet()) {

      System.out.println(id);
    }

     */
  }
}
