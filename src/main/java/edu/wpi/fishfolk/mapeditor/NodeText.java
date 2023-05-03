package edu.wpi.fishfolk.mapeditor;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;

public class NodeText extends Text {

  @Getter private int nodeID;

  public NodeText(int nodeID, double x, double y, String text) {
    super(x, y, text);
    this.setFont(Font.font("Open Sans", 20));
    this.setRotate(-25);
    this.nodeID = nodeID;
  }
}
