package edu.wpi.fishfolk.pathfinding;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class Node {

  @Getter @Setter public String id;
  @Getter @Setter public String oldID;
  @Getter @Setter public Point2D point;
  @Getter @Setter public String floor;
  @Getter @Setter public String building;
  @Getter @Setter public NodeType type;
  @Getter @Setter public String longName;
  @Getter @Setter public String shortName;

  @Getter @Setter public int degree;

  public Node(
      String id,
      Point2D point,
      String floor,
      String building,
      NodeType type,
      String longName,
      String shortName) {

    this.id = id;
    this.oldID = "-";
    this.point = point;
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.longName = longName;
    this.shortName = shortName;

    this.degree = 0;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    Node other = (Node) o;
    return id.equals(other.id);
  }
}
