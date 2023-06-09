package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.controllers.PathfindingController;
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.util.NodeType;
import java.util.ArrayList;

public abstract class Pathfinder implements IPathfinding {

  Graph graph;

  public Pathfinder(Graph g) {
    this.graph = g;
  }

  /**
   * Backtrack through the graph using the array of previous nodes.
   *
   * @param endIdx the index of the end node in the arrays used by this object's Graph.
   * @param start the id of the start node. The returned paths will start at this node.
   * @param lastVisited the array storing which node was last visited before every node
   * @return an ArrayList of Paths where each path is on its own floor.
   */
  protected ArrayList<Path> backtrack(int endIdx, int start, int[] lastVisited) {

    String currentFloor = "";
    ArrayList<Path> paths = new ArrayList<>();
    int numpaths = 0;

    // retrace path from the end to the start
    while (!(endIdx == graph.id2idx(start))) {

      // start new path when hitting a new floor
      if (!graph.getNodeFromIdx(endIdx).getFloor().equals(currentFloor)) {

        Path path = new Path();
        Node currentNode = graph.getNodeFromIdx(endIdx);
        currentFloor = currentNode.getFloor();
        path.setFloor(currentFloor);
        paths.add(0, path);
        numpaths++;

        if (numpaths > 1) {

          // determine direction from previous path to new one

          // invert param order since we are backtracking through the path
          boolean up = PathfindingController.direction(currentFloor, paths.get(1).getFloor());
          boolean elev = currentNode.containsType(NodeType.ELEV);

          Direction direction =
              up
                  ? (elev ? Direction.UP_ELEV : Direction.UP_STAI)
                  : (elev ? Direction.DOWN_ELEV : Direction.DOWN_STAI);

          paths.get(0).setToNextPath(direction);
        }
      }

      // add current node at beginning of first path
      paths.get(0).addFirst(graph.getNodeFromIdx(endIdx));

      endIdx = lastVisited[endIdx];
    }
    // add start node to beginning of first path
    if (paths.size() > 0) {
      paths.get(numpaths - 1).setToNextPath(Direction.END);
      paths.get(0).addFirst(graph.getNodeFromID(start));
    }

    return paths;
  }
}
