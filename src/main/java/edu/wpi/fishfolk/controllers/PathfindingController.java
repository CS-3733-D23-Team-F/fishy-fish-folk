package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Path;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class PathfindingController extends AbsController {
  @FXML MFXButton submitBtn;
  @FXML ChoiceBox<String> startSelector;
  @FXML ChoiceBox<String> endSelector;
  @FXML String start, end;

  @FXML AnchorPane mapAnchor;
  @FXML ImageView mapImg;

  Graph graph;
  Path path;

  public PathfindingController() {
    super();
    System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    System.out.println(dbConnection.nodeTable.getTableName());

    ArrayList[] res = dbConnection.nodeTable.getAllDestinationNodes();
    ArrayList<String> nodeIDs = res[0];
    ArrayList<String> nodeNames = res[1];

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end

    startSelector.setOnAction(
        event -> {
          start = nodeIDs.get(nodeNames.indexOf(startSelector.getValue()));
          System.out.println("start node: " + start);
        });
    endSelector.setOnAction(
        event -> {
          end = nodeIDs.get(nodeNames.indexOf(endSelector.getValue()));
          System.out.println("end node: " + end);
        });

    graph = new Graph(dbConnection.nodeTable, dbConnection.edgeTable);

    submitBtn.setOnAction(
        event -> {
          path = graph.AStar(start, end);
          drawPath(path);
        });
  }

  private void drawPath(Path path) {

    LinkedList<Line> segments = new LinkedList<>();

    ArrayList<Point2D> points = path.getPoints();

    for (int i = 1; i < points.size(); i++) {
      segments.add(line(points.get(i - 1), points.get(i)));
    }

    // segments.add(new Line(0, 7, 1120, 787)); //diagonal from top left to bottom right

    mapAnchor.getChildren().addAll(segments);
  }

  private Line line(Point2D p1, Point2D p2) {
    p1 = convert(p1);
    p2 = convert(p2);
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public Point2D convert(Point2D p) {

    // center gets mapped to center. center1 -> center2
    Point2D center1 = new Point2D(900 + 4050 / 2, 150 + 3000 / 2);
    Point2D center2 = new Point2D(1120 / 2, 780 / 2);

    Point2D rel = p.subtract(center1); // p relative to the center

    // strech x and y separately
    double x = rel.getX() * 1120 / 4050;
    double y = rel.getY() * 780 / 3000 + 7;

    return new Point2D(x, y).add(center2);
  }
}
