package edu.wpi.fishfolk.pathfinding;

import javafx.geometry.Point2D;

public class Node {

  public String id;
  public Point2D point;
  public String floor;
  public String building;
  public NodeType type;
  public String longName;
  public String shortName;

  public Node(
      String id,
      Point2D point,
      String floor,
      String building,
      NodeType type,
      String longName,
      String shortName) {
    this.id = id;
    this.point = point;
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.longName = longName;
    this.shortName = shortName;
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
