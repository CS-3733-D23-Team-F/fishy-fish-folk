package edu.wpi.fishfolk.pathfinding;

import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

public class Path {

  public LinkedList<String> nodes;

  @Getter @Setter private int length;

  public Path() {
    nodes = new LinkedList<>();
    length = 0;
  }

  public void addFirst(String n) {
    nodes.addFirst(n);
    length++;
  }

  public String removeLast() {
    length--;
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
