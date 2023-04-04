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
  @Getter @Setter public String date;
  @Getter @Setter public String adjacent;

  public ObservableNode() {}

  public ObservableNode(Node node, String date, String adjacentNodes) {

    this.id = node.id;
    this.x = String.format("%.0f", node.point.getX());
    this.y = String.format("%.0f", node.point.getY());
    this.floor = node.floor;
    this.building = node.building;
    this.type = node.type.toString();
    this.longName = node.longName;
    this.shortName = node.shortName;
    this.date = date;
    this.adjacent = adjacentNodes;
  }

  public void addEdge(String adjNode) {
    this.adjacent += "," + adjNode;
  }
}
