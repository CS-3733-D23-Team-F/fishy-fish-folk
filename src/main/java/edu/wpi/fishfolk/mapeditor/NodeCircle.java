package edu.wpi.fishfolk.mapeditor;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

public class NodeCircle extends Circle {

  @Getter private int nodeID;
  @Getter @Setter private double prevX, prevY;

  public NodeCircle(int nodeID, double centerX, double centerY, double radius) {
    super(centerX, centerY, radius);
    this.nodeID = nodeID;
    this.prevX = centerX;
    this.prevY = centerY;
  }

  public void reset() {
    this.setStrokeWidth(2);
    this.setStroke(Color.rgb(42, 99, 156)); // #2B639C
    this.setFill(Color.rgb(42, 99, 156));
  }

  public void highlight() {
    this.setStrokeWidth(4);
    this.setStroke(Color.rgb(240, 192, 76)); // #F0BF4C
  }

  public void resetIf(int nodeID) {
    if (nodeID == this.nodeID) reset();
  }

  public void highlightIf(int nodeID) {
    if (nodeID == this.nodeID) highlight();
  }
}
