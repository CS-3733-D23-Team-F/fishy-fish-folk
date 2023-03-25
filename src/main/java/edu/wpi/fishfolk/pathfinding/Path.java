package edu.wpi.fishfolk.pathfinding;

import java.util.LinkedList;

public class Path {

  public LinkedList<String> nodes;

  public boolean addNode(String n) {

    return nodes.add(n);
  }

  public String removeNode() {

    return nodes.removeLast();
  }
}
