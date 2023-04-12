package edu.wpi.fishfolk.database.rewrite.TableEntry;

import lombok.Getter;
import lombok.Setter;

public class Edge {

  @Getter @Setter int startNode;
  @Getter @Setter int endNode;

  /**
   * Table entry type: Edge
   *
   * @param startNode Unique ID of start node
   * @param endNode Unique ID of end node
   */
  public Edge(int startNode, int endNode) {
    this.startNode = startNode;
    this.endNode = endNode;
  }
}
