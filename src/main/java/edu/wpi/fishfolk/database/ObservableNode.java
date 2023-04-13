// package edu.wpi.fishfolk.database;
//
// import edu.wpi.fishfolk.pathfinding.Node;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.stream.Collectors;
// import lombok.Getter;
// import lombok.Setter;
//
// public class ObservableNode {
//  @Getter @Setter public String id;
//  @Getter @Setter public String x;
//  @Getter @Setter public String y;
//  @Getter @Setter public String floor;
//  @Getter @Setter public String building;
//  @Getter @Setter public String type;
//
//  @Getter @Setter public String longName;
//  @Getter @Setter public String shortName;
//  @Getter @Setter public String date;
//  @Getter public String adjacentNodes;
//
//  @Getter @Setter public Integer sortable;
//  private HashSet<String> adjNodesSet;
//
//  public ObservableNode() {}
//
//  public ObservableNode(Node node, String date, ArrayList<String> adjacentNodes) {
//
//    this.id = node.id;
//    this.x = String.format("%.0f", node.point.getX());
//    this.y = String.format("%.0f", node.point.getY());
//    this.floor = node.floor;
//    this.building = node.building;
//    this.type = node.type.toString();
//    this.longName = node.longName;
//    this.shortName = node.shortName;
//    this.date = date;
//
//    this.sortable = Integer.valueOf(node.id);
//
//    this.adjacentNodes = String.join(", ", adjacentNodes);
//    adjNodesSet = new HashSet<>(adjacentNodes);
//  }
//
//  public boolean addAdjNode(String adjNode) {
//    boolean res = adjNodesSet.add(adjNode);
//    if (res) setAdjacentNodes();
//    return res;
//  }
//
//  public boolean removeAdjNode(String adjNode) {
//    boolean res = adjNodesSet.remove(adjNode);
//    if (res) setAdjacentNodes();
//    return res;
//  }
//
//  public void setAdjacentNodes() {
//
//    // sort nodes. TODO sorting strings is not ideal since they are really ints
//    adjacentNodes = String.join(", ", adjNodesSet.stream().sorted().collect(Collectors.toList()));
//  }
// }
