package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Path;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class PathfindingController extends AbsController {
  @FXML MFXButton submitBtn;
  @FXML ChoiceBox<String> startSelector;
  @FXML ChoiceBox<String> endSelector;
  @FXML MFXButton clearBtn;
  int start, end;

  @FXML AnchorPane mapAnchor;
  @FXML ImageView mapImg;
  @FXML Text directionInstructions;
  @FXML MFXButton backButton;
  @FXML MFXButton nextButton;
  Graph graph;
  ArrayList<Path> paths;
  LinkedList<Line> segments;

  ArrayList<Image> displayMaps;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    ArrayList nodeNames = dbConnection.nodeTable.getDestLongNames();
    segments = new LinkedList<>();
    displayMaps = new ArrayList<>();
    System.out.println(dbConnection.nodeTable.getTableName());

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end

    startSelector.setOnAction(
        event -> {
          start = dbConnection.nodeTable.getNode("longname", startSelector.getValue()).nid;

          System.out.println("start node: " + start);
        });
    endSelector.setOnAction(
        event -> {
          end = dbConnection.nodeTable.getNode("longname", endSelector.getValue()).nid;
          System.out.println("end node: " + end);
        });

    graph = new Graph(dbConnection.nodeTable, dbConnection.edgeTable);

    submitBtn.setOnAction(
        event -> {
          for (int i = 0; i < segments.size(); i++) {
            mapAnchor.getChildren().remove(segments.get(i));
          }
          segments.clear();
          paths = graph.AStar(start, end);
          System.out.println("paths length: " + paths.size() + " floor" + paths.get(0).getFloor());

          Image image;
          for (int i = 0; i < paths.size(); i++) {
            switch (paths.get(i).getFloor()) {
              case "L1":
                image = new Image(Fapp.class.getResourceAsStream("map/00_thelowerlevel1.png"));
                displayMaps.add(image);
                break;
              case "L2":
                image = new Image(Fapp.class.getResourceAsStream("map/00_thelowerlevel2.png"));
                displayMaps.add(image);
                break;
              case "G":
                image = new Image(Fapp.class.getResourceAsStream("map/00_thegroundfloor.png"));
                displayMaps.add(image);
                break;
              case "1":
                image = new Image(Fapp.class.getResourceAsStream("map/01_thefirstfloor.png"));
                displayMaps.add(image);
                break;
              case "2":
                image = new Image(Fapp.class.getResourceAsStream("map/02_thesecondfloor.png"));
                displayMaps.add(image);
                break;
              case "3":
                image = new Image(Fapp.class.getResourceAsStream("map/03_thethirdfloor.png"));
                displayMaps.add(image);
                break;
              default:
                image = new Image(Fapp.class.getResourceAsStream("map/00_thelowerlevel1.png"));
                displayMaps.add(image);
                break;
            }
          }
          mapImg.setImage(displayMaps.get(0));
          drawPath(paths);
          // directionInstructions.setText("Instructions: \n\n" + path.getDirections());
        });
    nextButton.setOnMouseClicked(
        event -> {
          System.out.println("Image: " + mapImg.getImage());
        });

    backButton.setOnMouseClicked(
        event -> {
          if (displayMaps.get(1) != null) {}
        });

    clearBtn.setOnAction(
        event -> {
          for (int i = 0; i < segments.size(); i++) {
            mapAnchor.getChildren().remove(segments.get(i));
          }
          segments.clear();
        });
  }

  private void drawPath(ArrayList<Path> paths) {

    Circle circle = new Circle();
    for (int i = 0; i < paths.size(); i++) {
      ArrayList<Point2D> points = paths.get(i).getPoints();

      for (int j = 1; j < points.size(); j++) {
        segments.add(line(points.get(j - 1), points.get(j)));
        if (i + 1 == points.size()) {
          circle.setRadius(9);
          circle.setFill(Color.TRANSPARENT);
          circle.setStroke(Color.RED);
          Line temp = line(points.get(i - 1), points.get(i));
          circle.setLayoutX(temp.getEndX());
          circle.setLayoutY(temp.getEndY());
        }
      }
      mapAnchor.getChildren().addAll(segments);
      mapAnchor.getChildren().add(circle);
    }

    // segments.add(new Line(0, 7, 1120, 787)); //diagonal from top left to bottom right

  }

  private Line line(Point2D p1, Point2D p2) {
    p1 = convert(p1);
    p2 = convert(p2);
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public static void wait(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
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
