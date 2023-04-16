package edu.wpi.fishfolk.mapeditor;

import javafx.scene.shape.Circle;
import lombok.Getter;

public class NodeCircle extends Circle {

  @Getter private int nodeID;

  public NodeCircle(int nodeID, double centerX, double centerY, double radius) {
    super(centerX, centerY, radius);
    this.nodeID = nodeID;
  }
}
