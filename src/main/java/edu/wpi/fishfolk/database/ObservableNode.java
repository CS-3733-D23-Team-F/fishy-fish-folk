package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import lombok.Getter;
import lombok.Setter;import java.util.ArrayList;import java.util.Arrays;import java.util.HashSet;

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
  @Getter public HashSet<String> adjacentNodes;

  public ObservableNode() {}

  public ObservableNode(Node node, String date, ArrayList<String> adjacentNodes) {

    this.id = node.id;
    this.x = String.format("%.0f", node.point.getX());
    this.y = String.format("%.0f", node.point.getY());
    this.floor = node.floor;
    this.building = node.building;
    this.type = node.type.toString();
    this.longName = node.longName;
    this.shortName = node.shortName;
    this.date = date;

    this.adjacentNodes = new HashSet<>(adjacentNodes);
  }

  public boolean addAdjNode(String adjNode) {
    return adjacentNodes.add(adjNode);
  }

  public String getAdjNodes(){
    String[] nodes = (String[]) adjacentNodes.toArray();
    //sort nodes. TODO sorting strings is not ideal since they are really ints
    Arrays.sort(nodes);
    return String.join(", ", nodes);
  }
}
