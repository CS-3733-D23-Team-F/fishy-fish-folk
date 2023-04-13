package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import java.awt.geom.Point2D;
import lombok.Getter;
import lombok.Setter;

public class Node {

  @Getter @Setter private int nodeID;
  @Getter @Setter private Point2D point;
  @Getter @Setter private String floor;
  @Getter @Setter private String building;
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Node
   *
   * @param nodeID Unique ID of node
   * @param point X and Y coordinates of node
   * @param floor Floor of node
   * @param building Building of node
   */
  public Node(int nodeID, Point2D point, String floor, String building) {
    this.nodeID = nodeID;
    this.point = point;
    this.floor = floor;
    this.building = building;
    this.status = EntryStatus.UNCHANGED;
  }
}
