package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.pathfinding.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.*;
import javafx.application.Platform;
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
  @FXML MFXFilterComboBox<String> methodSelector;
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
  @FXML MFXTextField estimatedtime;

  PathfindSingleton pathfinder;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  ArrayList<String> floors;
  int currentFloor;

  ArrayList<ParallelTransition> pathAnimations;

  private double zoom;
  List<List<TextDirection>> textDirections;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    Platform.runLater(
        new Runnable() {

          @Override
          public void run() {
            pane.centreOn(new Point2D(2500, 1200));
            zoom = 0.5;
            javafx.geometry.Bounds bounds = pane.getTargetViewport();
            pane.zoomTo(zoom, new Point2D(bounds.getCenterX(), bounds.getCenterY()));
          }
        });

    // buttons to other pages
    methodSelector.getItems().add("A*");
    methodSelector.getItems().add("BFS");
    methodSelector.getItems().add("DFS");
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
          pathAnimations.clear();
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
          grid.getChildren().clear();
          end = dbConnection.getNodeIDFromLocation(endSelector.getValue(), today);
          System.out.println("end node: " + end);
          pathfinder = PathfindSingleton.PATHFINDER;
          if (methodSelector.getValue() == null || methodSelector.getValue().equals("A*")) {
            pathfinder.setPathMethod(new AStar(graph));
          } else if (methodSelector.getValue().equals("BFS")) {
            pathfinder.setPathMethod(new BFS(graph));
          } else if (methodSelector.getValue().equals("DFS")) {
            pathfinder.setPathMethod(new DFS(graph));
          }

          paths = pathfinder.getPathMethod().pathfind(start, end, true);

          System.out.println(paths);

          textDirections = new LinkedList<>();
          populateTextDirections(paths);

          // index 0 in floors in this path - not allFloors
          currentFloor = 0;

          drawPaths(paths);
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .centreOn(paths.get(0).centerToPath(7));

          displayFloor();
          endSelector.setDisable(true);
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

          if (paths.size() > 1) {

            Point2D p2 = path.points.get(path.numNodes - 1);
            g.getChildren()
                .add(
                    generatePathButtons(
                        p2.getX(),
                        p2.getY(),
                        direction(path.getFloor(), paths.get(1).getFloor()),
                        true));
          }

        } else if (i == paths.size() - 1) { // last path segment

          if (paths.size() > 1) {

            Point2D p1 = path.points.get(0);
            g.getChildren()
                .add(
                    generatePathButtons(
                        p1.getX(),
                        p1.getY(),
                        direction(path.getFloor(), paths.get(paths.size() - 2).getFloor()),
                        false));
          }

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

    // TODO set arrow up or down based on parameter
    NodeCircle nc = new NodeCircle(-1, x, y, 8);
    nc.setFill(Color.rgb(4, 100, 180));
    nc.setOnMouseClicked(
        event -> {
          if (forwards) {
            nextFloor();
            grid.getChildren().clear();
          } else {
            prevFloor();
            grid.getChildren().clear();
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

    // stop all animations
    pathAnimations.forEach(ParallelTransition::stop);

    // show and start animation for current floor
    pathGroup
        .getChildren()
        .get(currentFloor + 1)
        .setVisible(true); // offset by 1 because first child is gesture pane
    pathAnimations.get(currentFloor).play();

    // calculated that 1537 pixels = 484 feet = 147.5 m
    // 3.175 pixels per foot, 10.42 pixels per meter
    // 1.2 m/s walking speed = 72m/min

    // add lengths of each path in list of paths
    double totalLength = paths.stream().map(Path::pathLength).reduce(0.0, Double::sum);
    System.out.println("path length pixels " + totalLength);
    int minutes = (int) Math.ceil(totalLength * (147.5 / 1537) / 72);

    estimatedtime.setText(minutes + " minutes");

    int col = 0;
    int row = 1;

    try {
      for (int i = 0; i < textDirections.get(currentFloor).size(); i++) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/TextInstruction.fxml"));

        AnchorPane anchorPane = fxmlLoader.load();

        TextInstructionController instructionController = fxmlLoader.getController();

        // show text directions for this floor
        instructionController.setData(textDirections.get(currentFloor).get(i), i + 1);

        if (col == 3) {
          col = 0;
          row++;
        }

        // col++;
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
  }

  private void populateTextDirections(ArrayList<Path> paths) {
    int lastFloorIdx = paths.size() - 1;
    System.out.println("path size: " + lastFloorIdx);
    for (int i = 0; i <= lastFloorIdx; i++) {
      List<TextDirection> floorDirections = new LinkedList<>(paths.get(i).getDirections());

      if (i == 0) {
        floorDirections.add(
            0, new TextDirection(Direction.START, "Start at " + startSelector.getValue()));
        if (lastFloorIdx > 0) {
          floorDirections.add(
              textDirectionBetweenFloors(paths.get(0).getToNextPath(), paths.get(1).getFloor()));
        }
      }

      if (i == lastFloorIdx) {
        if (lastFloorIdx > 0) {
          floorDirections.add(
              textDirectionBetweenFloors(
                  paths.get(lastFloorIdx - 1).getToNextPath(), paths.get(lastFloorIdx).getFloor()));
        }
        floorDirections.add(new TextDirection(Direction.START, "End at " + endSelector.getValue()));
      }
      System.out.println(
          "floor dir: "
              + floorDirections.get(i).getDirection()
              + " dist "
              + floorDirections.get(i).getDistance());
      textDirections.add(floorDirections);
    }
  }

  private TextDirection textDirectionBetweenFloors(Direction direction, String second) {

    String text = "";
    switch (direction) {
      case UP_ELEV:
        text = "Take the elevator up to " + second;
        break;
      case DOWN_ELEV:
        text = "Take the elevator down to " + second;
        break;
      case UP_STAI:
        text = "Take the stairs up to " + second;
        break;
      case DOWN_STAI:
        text = "Take the stairs down to " + second;
        break;
    }
    System.out.println(text);
    return new TextDirection(direction, text);
  }

  /**
   * Get the direction between two floors
   *
   * @param currFloor
   * @param nextFloor
   * @return true if the second is higher than the first, otherwise false
   */
  public static boolean direction(String currFloor, String nextFloor) {
    return allFloors.indexOf(currFloor) < allFloors.indexOf(nextFloor);
  }
}
