package edu.wpi.fishfolk.database;

public class EdgeEdit {

  public EdgeEditType type;
  public String node1;
  public String node2;

  public EdgeEdit(EdgeEditType type, String node1, String node2) {
    this.type = type;
    this.node1 = node1;
    this.node2 = node2;
  }
}

