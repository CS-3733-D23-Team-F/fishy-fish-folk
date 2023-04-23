package edu.wpi.fishfolk.mapeditor;

import edu.wpi.fishfolk.database.TableEntry.Edge;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import lombok.Getter;

public class EdgeLine extends Line {

  @Getter private int startNode, endNode;

  public EdgeLine(int startNode, int endNode, double x1, double y1, double x2, double y2) {
    super(x1, y1, x2, y2);

    this.startNode = startNode;
    this.endNode = endNode;

    this.getStrokeDashArray().addAll(7.5, 5.0);
    this.setStrokeWidth(2.5);
    this.setStrokeLineCap(StrokeLineCap.ROUND);
  }

  public boolean containsNode(int nodeID) {
    return startNode == nodeID || endNode == nodeID;
  }

  public boolean containsNodes(List<Integer> nodeIDs) {

    for (int nodeID : nodeIDs) {
      if (nodeID == startNode || nodeID == endNode) return true;
    }
    return false;
  }

  public boolean matches(Edge edge) {
    return containsNode(edge.getStartNode()) && containsNode(edge.getEndNode());
  }

  public void reset() {
    this.setStroke(Color.rgb(61, 56, 56)); // #3D3838
  }

  public void highlight() {
    this.setStroke(Color.rgb(98, 160, 223)); // #62A0DF
  }

  public void highlightIf(Edge edge) {
    if (matches(edge)) highlight();
  }

  public void resetIf(Edge edge) {
    if (matches(edge)) reset();
  }
}
