package edu.wpi.fishfolk.pathfinding;

public class Edge {

  public String edgeID;
  public String nodeID1;
  public String nodeID2;

  public Edge(String nodeID1, String nodeID2) {
    this.edgeID = nodeID1 + "_" + nodeID2;
    this.nodeID1 = nodeID1;
    this.nodeID2 = nodeID2;
  }
}
