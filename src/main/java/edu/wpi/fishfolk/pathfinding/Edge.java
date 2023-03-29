package edu.wpi.fishfolk.pathfinding;

public class Edge {

  public String edgeid;
  public String startnode;
  public String endnode;

  public Edge(String edgeid, String startnode, String endnode) {
    this.edgeid = edgeid;
    this.startnode = startnode;
    this.endnode = endnode;
  }
}
