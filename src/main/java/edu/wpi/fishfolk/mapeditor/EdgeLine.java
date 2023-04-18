package edu.wpi.fishfolk.mapeditor;

import edu.wpi.fishfolk.database.TableEntry.Edge;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;

public class EdgeLine extends Line {

  @Getter private int startNode, endNode;

  public EdgeLine(int startNode, int endNode, double x1, double y1, double x2, double y2) {
    super(x1, y1, x2, y2);

    this.startNode = startNode;
    this.endNode = endNode;
  }

  public boolean containsNode(int nodeID) {
    return startNode == nodeID || endNode == nodeID;
  }

  public boolean matches(Edge edge) {
    return containsNode(edge.getStartNode()) && containsNode(edge.getEndNode());
  }

  public void highlight() {
    this.setStroke(Color.rgb(102, 102, 255));
  }

  public void reset() {
    this.setStroke(Color.rgb(0, 0, 0));
  }
}
