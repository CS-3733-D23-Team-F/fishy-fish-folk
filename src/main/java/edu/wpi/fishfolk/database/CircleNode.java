package edu.wpi.fishfolk.database;

import javafx.scene.shape.Circle;

public class CircleNode extends Circle {

  String id;

  public CircleNode(String nodeId, double centerX, double centerY, double radius) {
    super(centerX, centerY, radius);
    this.id = nodeId;
  }
}
