package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import lombok.Getter;
import lombok.Setter;

public class Edge {

  @Getter @Setter int startNode;
  @Getter @Setter int endNode;
  @Getter @Setter private EntryStatus status;

  @Getter String edgeID;

  /**
   * Table entry type: Edge
   *
   * @param startNode Unique ID of start node
   * @param endNode Unique ID of end node
   */
  public Edge(int startNode, int endNode) {
    this.startNode = startNode;
    this.endNode = endNode;
    edgeID = startNode + "<->" + endNode;
  }

  @Override
  public boolean equals(Object other) {

    if (other instanceof Edge) {

      Edge otherEdge = (Edge) other;
      return startNode == otherEdge.startNode && endNode == otherEdge.endNode;

    } else return false;
  }

  @Override
  public int hashCode() {
    return edgeID.hashCode();
  }

  @Override
  public String toString() {
    return edgeID;
  }
}
