package edu.wpi.fishfolk.pathfinding;

import java.util.ArrayList;

public abstract class Pathfinder {

  Graph graph;

  public Pathfinder(Graph g) {
    this.graph = g;
  }

  /**
   * Finds the path between to locations using the A* algorithm
   *
   * @param start The ID of the starting location
   * @param end The ID of the ending location
   * @param stairs Boolean that indicates if stairs are allowed, false for no stairs
   * @return ArrayList of Paths, each item in the list is a path on a separate floor
   */
  public abstract ArrayList<Path> pathfind(int start, int end, boolean stairs);

  /**
   * Backtrack through the graph using the array of previous nodes.
   * @param endIdx the index of the end node in the arrays used by this object's Graph.
   * @param start the id of the start node. The returned paths will start at this node.
   * @param lastVisited the array storing which node was last visited before every node
   * @return an ArrayList of Paths where each path is on its own floor.
   */
  protected ArrayList<Path> backtrack(int endIdx, int start, int[] lastVisited) {

    String currentFloor = "";
    ArrayList<Path> paths = new ArrayList<>();
    int numpaths = -1;

    // retrace path from the end to the start
    while (!(endIdx == graph.id2idx(start))) {

      // start new path when hitting a new floor
      if (!graph.getNodeFromIdx(endIdx).floor.equals(currentFloor)) {
        Path path = new Path();
        currentFloor = graph.getNodeFromIdx(endIdx).floor;
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;
      }

      // add current node at beginning of first path
      paths.get(0).addFirst(graph.getNodeFromIdx(endIdx));

      endIdx = lastVisited[endIdx];
    }
    // add start node to beginning of first path
    paths.get(0).addFirst(graph.getNodeFromID(start));

    return paths;

  }
}
