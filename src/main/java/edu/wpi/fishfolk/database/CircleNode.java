package edu.wpi.fishfolk.database;

import javafx.scene.shape.Circle;

public class CircleNode extends Circle {

  public String getCircleNodeID() {
    return circleNodeID;
  }

  String circleNodeID;

  public CircleNode(String nodeId, double centerX, double centerY, double radius) {
    super(centerX, centerY, radius);
    this.circleNodeID = nodeId;
  }
}
