package edu.wpi.fishfolk.database;

public class ObservableNode {
  public String id;
  public String x;
  public String y;
  public String floor;
  public String building;
  public String type;
  public String longName;
  public String shortName;

  public ObservableNode() {}

  public ObservableNode(
      String id,
      String x,
      String y,
      String floor,
      String building,
      String type,
      String longName,
      String shortName) {

    this.id = id;
    this.x = x;
    this.y = y;
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.longName = longName;
    this.shortName = shortName;
  }
}
