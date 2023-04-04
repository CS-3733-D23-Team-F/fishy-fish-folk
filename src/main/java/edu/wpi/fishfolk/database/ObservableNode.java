package edu.wpi.fishfolk.database;

import lombok.Getter;
import lombok.Setter;

public class ObservableNode {
  @Getter @Setter public String id;
  @Getter @Setter public String x;
  @Getter @Setter public String y;
  @Getter @Setter public String floor;
  @Getter @Setter public String building;
  @Getter @Setter public String type;
  @Getter @Setter public String longName;
  @Getter @Setter public String shortName;

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
