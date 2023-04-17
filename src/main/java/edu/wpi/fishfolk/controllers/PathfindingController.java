package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.pathfinding.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

public class PathfindingController extends AbsController {

  @FXML Text floorDisplay;

  @FXML MFXFilterComboBox<String> startSelector;
  @FXML MFXFilterComboBox<String> endSelector;
  @FXML MFXButton clearBtn;

  @FXML Group pathGroup;
  @FXML ImageView mapImg;
  // @FXML Text directionInstructions;

  @FXML MFXButton zoomOut;
  @FXML MFXButton zoomIn;
  @FXML GesturePane pane;
  @FXML MFXButton slideUp;
  @FXML VBox textInstruct;

  Pathfinder pathfinder;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  ArrayList<String> floors;
  int currentFloor;
  int zoom;

  private LocalDate today = LocalDate.of(2023, Month.JUNE, 1);

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    // buttons to other pages
    slideUp.setOnMouseClicked(
        event -> {
          // serviceBar.setVisible(true);
          // serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(textInstruct);

          slide.setToY(0);
          slide.play();

          textInstruct.setTranslateY(0);
          slide.setOnFinished(
              (ActionEvent e) -> {
                /*
                serviceNav.setVisible(false);
                closeServiceNav.setVisible(true);
                serviceNav.setDisable(true);
                closeServiceNav.setDisable(false);

                 */
              });
        });

    zoomIn.setOnMouseClicked(
        event -> {
          javafx.geometry.Bounds bounds = pane.getTargetViewport();
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .zoomBy(zoom + 0.2, new Point2D(bounds.getCenterX(), bounds.getCenterY()));
        });

    zoomOut.setOnMouseClicked(
        event -> {
          javafx.geometry.Bounds bounds = pane.getTargetViewport();
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .zoomBy(zoom - 0.2, new Point2D(bounds.getCenterX(), bounds.getCenterY()));
        });

    List<String> nodeNames = dbConnection.getDestLongnames();
    System.out.println(nodeNames.size());

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end
    endSelector.setDisable(true);
    startSelector.setOnAction(
        event -> {
          pathGroup.getChildren().clear();
          pathGroup.getChildren().add(mapImg);

          // clear list of floors
          floors.clear();
          start = dbConnection.getNodeIDFromLocation(startSelector.getValue(), today);
          // floor of the selected unode id
          currentFloor = 0;
          System.out.println("start node: " + start);
          endSelector.setVisible(true);
          endSelector.setDisable(false);
        });
    endSelector.setOnAction(
        event -> {
          end = dbConnection.getNodeIDFromLocation(endSelector.getValue(), today);
          System.out.println("end node: " + end);
          pathfinder = new AStar(graph);
          paths = pathfinder.pathfind(start, end, true);

          // index 0 in floors in this path - not allFloors
          currentFloor = 0;

          drawPaths(paths);
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .centreOn(paths.get(0).centerToPath(7));

          displayFloor();
          endSelector.setDisable(true);
        });

    currentFloor = 0;
    floors = new ArrayList<>();
    graph = new Graph(dbConnection);

    /*
            nextButton.setOnMouseClicked(
                event -> {
                  if (currentFloor < floors.size() - 1) {
                    currentFloor++;
                  }
                  displayFloor(currentFloor);
                });

            backButton.setOnMouseClicked(
                event -> {
                  if (currentFloor >= 1) {
                    currentFloor--;
                  }
                  displayFloor(currentFloor);
                });
    */
    clearBtn.setOnMouseClicked(
        event -> {
          // clear paths
          pathGroup.getChildren().clear();
          pathGroup.getChildren().add(mapImg);

          // clear list of floors
          floors.clear();
        });
  }

  private void drawPaths(ArrayList<Path> paths) {

    for (int i = 0; i < paths.size(); i++) {
      Path path = paths.get(i);
      // skip paths of length 1 (going through elevator node)
      // except for first and last path
      if (path.numNodes > 1 || i == 0 || i == paths.size() - 1) {

        LinkedList<Line> segments = new LinkedList<>();
        for (int j = 1; j < path.numNodes; j++) {
          segments.add(line(path.points.get(j - 1), path.points.get(j)));
        }
        // group to store this floor's path
        Group g = new Group();
        g.getChildren().addAll(segments);

        // add icons to start and end of paths on each floor
        if (i == 0) {
          Point2D p1 = path.points.get(0);
          NodeCircle start = new NodeCircle(-1, p1.getX(), p1.getY(), 12);
          start.setFill(Color.rgb(1, 45, 90));
          g.getChildren().add(start);

          Point2D p2 = path.points.get(path.numNodes - 1);
          g.getChildren()
              .add(
                  generatePathButtons(
                      p2.getX(),
                      p2.getY(),
                      direction(path.getFloor(), paths.get(1).getFloor()),
                      true));

        } else if (i == paths.size() - 1) { // last path segment
          Point2D p1 = path.points.get(0);
          g.getChildren()
              .add(
                  generatePathButtons(
                      p1.getX(),
                      p1.getY(),
                      direction(path.getFloor(), paths.get(paths.size() - 2).getFloor()),
                      false));

          Point2D p2 = path.points.get(path.numNodes - 1);
          NodeCircle end = new NodeCircle(-1, p2.getX(), p2.getY(), 12);
          end.setFill(Color.rgb(1, 45, 90));
          g.getChildren().add(end);

        } else { // middle path segment. draw a regular path button at each endpoint
          Point2D p1 = path.points.get(0);
          g.getChildren()
              .add(
                  generatePathButtons(
                      p1.getX(),
                      p1.getY(),
                      direction(
                          path.getFloor(), // this button goes in reverse
                          paths.get(i - 1).getFloor()),
                      false));

          Point2D p2 = path.points.get(path.numNodes - 1);
          g.getChildren()
              .add(
                  generatePathButtons(
                      p2.getX(),
                      p2.getY(),
                      direction(path.getFloor(), paths.get(i + 1).getFloor()),
                      true));
        }

        pathGroup.getChildren().add(g);
        g.setVisible(false);

        floors.add(path.floor);
      }
    }
  }

  /**
   * Generate the buttons that appear on paths at elevator / stair nodes to go up or down a floor.
   *
   * @param x
   * @param y
   * @param up true if this button goes up , false if down
   * @param forwards true if this button goes forwards in the path, false if backwards
   * @return a JavaFX Node object to draw
   */
  private javafx.scene.Node generatePathButtons(double x, double y, boolean up, boolean forwards) {

    NodeCircle nc = new NodeCircle(-1, x, y, 8);
    nc.setFill(Color.rgb(4, 100, 180));
    nc.setOnMouseClicked(
        event -> {
          if (forwards) {
            nextFloor();
          } else {
            prevFloor();
          }
          displayFloor();
        });
    return nc;
  }

  private void nextFloor() {
    if (currentFloor < floors.size() - 1) {
      currentFloor++;
    }
  }

  private void prevFloor() {
    if (currentFloor > 0) {
      currentFloor--;
    }
  }

  /** Display the floor indexed by the class variable currentFloor */
  private void displayFloor() {

    mapImg.setImage(images.get(floors.get(currentFloor)));
    floorDisplay.setText("Floor " + floors.get(currentFloor));
    Iterator<javafx.scene.Node> itr = pathGroup.getChildren().iterator();
    itr.next(); // skip first child which is the imageview

    while (itr.hasNext()) {
      itr.next().setVisible(false);
    }

    pathGroup.getChildren().get(currentFloor + 1).setVisible(true);
  }

  private Line line(Point2D p1, Point2D p2) {
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  /**
   * Get the direction between two floors
   *
   * @param currFloor
   * @param nextFloor
   * @return true if the second is higher than the first, otherwise false
   */
  private boolean direction(String currFloor, String nextFloor) {
    System.out.println(allFloors.indexOf(currFloor) + "->" + allFloors.indexOf(nextFloor));
    return allFloors.indexOf(currFloor) < allFloors.indexOf(nextFloor);
  }
}
