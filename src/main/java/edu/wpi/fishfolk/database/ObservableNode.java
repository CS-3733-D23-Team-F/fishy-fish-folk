package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import lombok.Getter;
import lombok.Setter;

public class ObservableNode {
  @Getter @Setter public String id;
  @Getter @Setter public String x;
  @Getter @Setter public String y;
  @Getter @Setter public String floor;
  @Getter @Setter public String building;
  @Getter @Setter public String type;
  @Getter @Setter public String longName;
  @Getter @Setter public String shortName;

  public ObservableNode() {}

  public ObservableNode(Node node) {

    this.id = node.id;
    this.x = String.valueOf(node.point.getX());
    this.y = String.valueOf(node.point.getY());
    this.floor = node.floor;
    this.building = node.building;
    this.type = node.type.toString();
    this.longName = node.longName;
    this.shortName = node.shortName;
  }
}
