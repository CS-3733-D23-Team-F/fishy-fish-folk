package edu.wpi.fishfolk.pathfinding;

import java.util.HashMap;

public class Graph {

  // see
  // https://stackoverflow.com/questions/3287003/three-ways-to-store-a-graph-in-memory-advantages-and-disadvantages
  // in the future maybe switch to adjacency list

  int[][] adjMat;

  HashMap<String, Node> nodes; // string id to node objects
  HashMap<String, Integer> id2idx; // string id to index in adjacency matrix
  private int lastIdx;

  public Graph(int size) {

    adjMat = new int[size][size];
    nodes = new HashMap<>(64); // 46 nodes -> load factor is just under the default 0.75
    id2idx = new HashMap<>(64);

    lastIdx = 0;
  }

  public boolean addNode(Node n) {

    if (nodes.put(n.id, n) != null) { // non-null if the key already mapped to a value
      return false;
    }

    id2idx.put(n.id, lastIdx);
    return true;
  }

  public boolean removeNode(String id) {

    if (nodes.remove(id) != null) { // non-null if id existed
      id2idx.remove(id);
      return true;
    }

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

  public void print() {

    for (String id : nodes.keySet()) {

      System.out.println(id);
    }
  }
}
