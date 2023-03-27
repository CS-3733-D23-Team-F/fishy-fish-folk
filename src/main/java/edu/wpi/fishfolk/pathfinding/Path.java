package edu.wpi.fishfolk.pathfinding;

import java.util.LinkedList;
import lombok.Getter;

public class Path {

  @Getter private LinkedList<String> nodes;

  int numNodes;

  public Path() {
    nodes = new LinkedList<>();
    numNodes = 0;
  }

  public void addFirst(String n) {
    nodes.addFirst(n);
    numNodes++;
  }

  public String removeLast() {
    numNodes--;
    return nodes.removeLast();
  }

  public String toString() {
    return nodes.toString();
  }

  @Override
  public boolean equals(Object o) {
    Path other = (Path) o;

    return nodes.equals(other.nodes);
  }
}
