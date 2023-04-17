package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.pathfinding.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
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

  @FXML MFXButton zoomOut;
  @FXML MFXButton zoomIn;
  @FXML GesturePane pane;
  @FXML MFXButton slideUp;
  @FXML MFXButton slideDown;
  @FXML VBox textInstruct;

  @FXML ScrollPane scroll;
  @FXML GridPane grid;

  Pathfinder pathfinder;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  ArrayList<String> floors;
  int currentFloor;

  ArrayList<ParallelTransition> pathAnimations;

  int zoom;
  ArrayList<TextDirection> textDirections;

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
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(textInstruct);
          slideUp.setDisable(true);
          slideUp.setVisible(false);
          slideDown.setVisible(true);
          slideDown.setDisable(false);
          slide.setToY(0);
          slide.play();

          textInstruct.setTranslateY(0);
          slide.setOnFinished((ActionEvent e) -> {});
        });

    slideDown.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(textInstruct);
          slideUp.setDisable(false);
          slideUp.setVisible(true);
          slideDown.setVisible(false);
          slideDown.setDisable(true);
          slide.setToY(252);
          slide.play();

          textInstruct.setTranslateY(252);
          slide.setOnFinished((ActionEvent e) -> {});
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
          textDirections = new ArrayList<>();
          textDirections.addAll(parseDirections(paths.get(0).getDirections()));
          int col = 0;
          int row = 1;

          try {
            for (int i = 0; i < textDirections.size(); i++) {
              FXMLLoader fxmlLoader = new FXMLLoader();
              fxmlLoader.setLocation(Fapp.class.getResource("views/TextInstruction.fxml"));

              AnchorPane anchorPane = fxmlLoader.load();

              TextInstructionController instructionController = fxmlLoader.getController();
              instructionController.setData(textDirections.get(i), i + 1);

              if (col == 3) {
                col = 0;
                row++;
              }

              grid.add(anchorPane, col++, row);

              grid.setMinWidth(Region.USE_COMPUTED_SIZE);
              grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
              grid.setMaxWidth(Region.USE_COMPUTED_SIZE);

              grid.setMinHeight(Region.USE_COMPUTED_SIZE);
              grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
              grid.setMaxHeight(Region.USE_COMPUTED_SIZE);

              GridPane.setMargin(anchorPane, new Insets(10));
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    clearBtn.setOnMouseClicked(
        event -> {
          // clear paths
          pathGroup.getChildren().clear();
          pathGroup.getChildren().add(mapImg);

          // clear list of floors
          floors.clear();
        });

    currentFloor = 0;
    floors = new ArrayList<>();
    pathAnimations = new ArrayList<>();

    graph = new Graph(dbConnection);
  }

  private List<TextDirection> parseDirections(ArrayList<String> directions) {
    List<TextDirection> textDirections = new ArrayList<>();
    TextDirection textDirection;
    for (int i = 0; i < directions.size() - 2; i += 2) {

      textDirection = new TextDirection();
      if (i == 0) {
        textDirection.setDirection("straight");
        textDirection.setDistance(directions.get(i));
        i = -1;
      } else {
        System.out.println(
            "direction: " + directions.get(i) + " distance: " + directions.get(i + 1));
        textDirection.setDirection(directions.get(i));
        textDirection.setDistance(directions.get(i + 1));
      }
      textDirections.add(textDirection);
    }

    return textDirections;
  }

  private void drawPaths(ArrayList<Path> paths) {

    for (int i = 0; i < paths.size(); i++) {
      Path path = paths.get(i);
      // skip paths of length 1 (going through elevator node)
      // except for first and last path
      if (path.numNodes > 1 || i == 0 || i == paths.size() - 1) {

        // group to store this floor's path
        Group g = new Group();

        double pathLength = path.pathLength();

        // create points to move along path
        int numPoints = (int) (pathLength * 0.04);

        ParallelTransition parallelTransition = new ParallelTransition();
        Duration duration = new Duration(1000);
        Point2D[] points = path.interpolate(numPoints);

        for (int j = 0; j < numPoints; j++) {
          Circle c = new Circle(5);
          c.setFill(Color.rgb(4, 100, 180));
          g.getChildren().add(c);

          // one animation object per point moving along its segment
          Polyline segment =
              new Polyline(
                  points[j].getX(), points[j].getY(), points[j + 1].getX(), points[j + 1].getY());

          PathTransition animation = new PathTransition(duration, segment, c);
          animation.setInterpolator(Interpolator.LINEAR);
          animation.setCycleCount(Timeline.INDEFINITE);
          animation.setAutoReverse(false);
          parallelTransition.getChildren().add(animation);
        }

        pathAnimations.add(parallelTransition);

        // add buttons to start and end of paths on each floor
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

        floors.add(path.getFloor());
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

    pathAnimations.forEach(ParallelTransition::stop);

    pathGroup.getChildren().get(currentFloor + 1).setVisible(true);
    System.out.println(
        "parallel animations: " + pathAnimations.get(currentFloor).getChildren().size());
    pathAnimations.get(currentFloor).play();
  }

  /**
   * Get the direction between two floors
   *
   * @param currFloor
   * @param nextFloor
   * @return true if the second is higher than the first, otherwise false
   */
  private boolean direction(String currFloor, String nextFloor) {
    return allFloors.indexOf(currFloor) < allFloors.indexOf(nextFloor);
  }
}
