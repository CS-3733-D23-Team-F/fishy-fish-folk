package edu.wpi.fishfolk.mapeditor;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

public class NodeCircle extends Circle {

  @Getter private int nodeID;

  public NodeCircle(int nodeID, double centerX, double centerY, double radius) {
    super(centerX, centerY, radius);
    this.nodeID = nodeID;
  }

  public void reset() {
    this.setStroke(Color.rgb(60, 190, 246)); // #208036
    this.setFill(Color.rgb(60, 190, 246));
  }

  public void highlight() {
    this.setStroke(Color.rgb(102, 102, 255));
  }
}
