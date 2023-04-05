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

  ArrayList<String> mapList;
  int currentDisplayed;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    ArrayList nodeNames = dbConnection.nodeTable.getDestLongNames();
    segments = new LinkedList<>();
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

    submitBtn.setOnMouseClicked(
        event -> {
          for (int i = 0; i < segments.size(); i++) {
            mapAnchor.getChildren().remove(segments.get(i));
          }
          segments.clear();
          paths = graph.AStar(start, end);
          mapList = new ArrayList<>();
          for (int i = 0; i < paths.size(); i++) {

            switch (paths.get(i).getFloor()) {
              case "L1":
                mapList.add("map/00_thelowerlevel1.png");
                break;
              case "L2":
                mapList.add("map/00_thelowerlevel2.png");
                break;
              case "G":
                mapList.add("map/00_thegroundfloor.png");
                break;
              case "1":
                mapList.add("map/01_thefirstfloor.png");
                break;
              case "2":
                mapList.add("map/02_thesecondfloor.png");
                break;
              case "3":
                mapList.add("map/03_thethirdfloor.png");
                break;
              default:
                break;
            }
          }
          System.out.println("mapList: " + mapList.size());
          mapImg.setImage(new Image(Fapp.class.getResourceAsStream(mapList.get(0))));
          currentDisplayed = 0;
          drawPath(paths, currentDisplayed);
          // directionInstructions.setText("Instructions: \n\n" + path.getDirections());
        });
    nextButton.setOnMouseClicked(
        event -> {
          if (currentDisplayed != (mapList.size() - 1)) {
            for (int k = 0; k < mapList.size(); k++) {
              if (currentDisplayed == k) {
                for (int i = 0; i < segments.size(); i++) {
                  mapAnchor.getChildren().remove(segments.get(i));
                }
                segments.clear();
                currentDisplayed = currentDisplayed + 1;
                mapImg.setImage(
                    new Image(Fapp.class.getResourceAsStream(mapList.get(currentDisplayed))));
                drawPath(paths, currentDisplayed);
                break;
              }
            }
          }
        });

    backButton.setOnMouseClicked(
        event -> {
          if (currentDisplayed != 0) {
            for (int k = mapList.size() - 1; k >= 0; k--) {
              if (currentDisplayed == k) {
                for (int i = 0; i < segments.size(); i++) {
                  mapAnchor.getChildren().remove(segments.get(i));
                }
                segments.clear();
                currentDisplayed = currentDisplayed - 1;
                mapImg.setImage(
                    new Image(Fapp.class.getResourceAsStream(mapList.get(currentDisplayed))));
                drawPath(paths, currentDisplayed);
                break;
              }
            }
          }
        });

    clearBtn.setOnMouseClicked(
        event -> {
          for (int i = 0; i < segments.size(); i++) {
            mapAnchor.getChildren().remove(segments.get(i));
          }
          segments.clear();
        });
  }

  private void drawPath(ArrayList<Path> paths, int floor) {
    ArrayList<Point2D> points = paths.get(floor).getPoints();
    System.out.println("Points: " + points.size());
    for (int j = 1; j < points.size(); j++) {
      segments.add(line(points.get(j - 1), points.get(j)));
      System.out.println(
          "Points: "
              + points.get(j)
              + " segments: "
              + segments.get(j - 1).getStartX()
              + " "
              + segments.get(j - 1).getStartY()
              + " "
              + segments.get(j - 1).getEndX()
              + " "
              + segments.get(j - 1).getEndY());
    }
    segments.remove(0);
    mapAnchor.getChildren().addAll(segments);
    // segments.add(new Line(0, 7, 1120, 787)); //diagonal from top left to bottom right

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
