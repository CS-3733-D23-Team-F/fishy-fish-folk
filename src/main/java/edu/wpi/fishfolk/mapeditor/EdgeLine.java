package edu.wpi.fishfolk.mapeditor;

import edu.wpi.fishfolk.database.TableEntry.Edge;
import edu.wpi.fishfolk.database.TableEntry.Node;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import lombok.Getter;

public class EdgeLine extends Line {

  @Getter private int startNodeID, endNodeID;

  public EdgeLine(Edge edge, Point2D start, Point2D end) {

    super(start.getX(), start.getY(), end.getX(), end.getY());

    this.startNodeID = edge.getStartNode();
    this.endNodeID = edge.getEndNode();

    this.getStrokeDashArray().addAll(7.5, 5.0);
    this.setStrokeWidth(2.5);
    this.setStrokeLineCap(StrokeLineCap.ROUND);
  }

  public EdgeLine(int startNodeID, int endNodeID, double x1, double y1, double x2, double y2) {

    super(x1, y1, x2, y2);

    this.startNodeID = startNodeID;
    this.endNodeID = endNodeID;

    this.getStrokeDashArray().addAll(7.5, 5.0);
    this.setStrokeWidth(2.5);
    this.setStrokeLineCap(StrokeLineCap.ROUND);
  }

  public boolean containsNode(int nodeID) {
    return startNodeID == nodeID || endNodeID == nodeID;
  }

  public boolean containsNodes(List<Integer> nodeIDs) {

    for (int nodeID : nodeIDs) {
      if (nodeID == startNodeID || nodeID == endNodeID) return true;
    }
    return false;
  }

  public boolean updateEndpoint(Node node) {

    if (node.getNodeID() == startNodeID) {
      this.setStartX(node.getX());
      this.setStartY(node.getY());
      return true;

    } else if (node.getNodeID() == endNodeID) {
      this.setEndX(node.getX());
      this.setEndY(node.getY());
      return false;
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
