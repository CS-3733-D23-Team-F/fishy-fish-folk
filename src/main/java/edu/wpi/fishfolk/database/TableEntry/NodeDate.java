package edu.wpi.fishfolk.database.TableEntry;

import java.time.LocalDate;
import lombok.Getter;

public class NodeDate {
  @Getter private Node node;
  @Getter private LocalDate date;

  public NodeDate(Node node, LocalDate date) {
    this.node = node;
    this.date = date;
  }

  public String toString() {
    return "moving to " + node.getNodeID() + " on " + date;
  }
}
