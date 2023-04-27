package edu.wpi.fishfolk.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.mapeditor.NodeText;
import edu.wpi.fishfolk.pathfinding.*;
import edu.wpi.fishfolk.util.NodeType;
import edu.wpi.fishfolk.util.PermissionLevel;
import io.github.palexdev.materialfx.controls.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

public class PathfindingController extends AbsController {

  @FXML Text floorDisplay;

  @FXML MFXFilterComboBox<String> startSelector;
  @FXML MFXFilterComboBox<String> endSelector;
  @FXML MFXFilterComboBox<String> methodSelector;
  @FXML MFXButton clearBtn;

  @FXML Group drawGroup;
  @FXML ImageView mapImg;

  @FXML MFXButton zoomOut;
  @FXML MFXButton zoomIn;
  @FXML GesturePane pane;
  @FXML MFXButton slideUp;
  @FXML MFXButton slideDown;
  @FXML VBox textInstruct;

  @FXML VBox settingBox;
  @FXML ImageView settingButton;

  @FXML MFXButton closeSettings;

  @FXML VBox adminBox;

  @FXML MFXButton closeAdmin;

  @FXML MFXDatePicker pathDate;

  @FXML MFXTextField pathMessage;

  @FXML MFXButton submitSetting;

  @FXML HBox pathTextBox;

  @FXML ScrollPane scroll;
  @FXML GridPane grid;
  @FXML MFXTextField estimatedtime;
  @FXML MFXButton generateqr;

  @FXML MFXToggleButton noStairs;
  @FXML MFXToggleButton showLocations;

  private PathfindSingleton pathfinder;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;
  Group locationGroup = new Group();

  Map<NodeType, Group> locationGroups;
  Map<NodeType, MFXToggleButton> locationsButtons;

  ArrayList<NodeType> displayTypes;

  ArrayList<MFXToggleButton> displayButtons;

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

    methodSelector.getItems().addAll("A*", "BFS", "DFS", "Dijkstra's");

    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Fapp.class.getResource("views/Alerts.fxml"));

      HBox alertPane = fxmlLoader.load();

      AlertsController alertsController = fxmlLoader.getController();
      alertsController.setData(dbConnection.getLatestAlert());

      alertsController.closeAlert.setDisable(true);
      alertsController.closeAlert.setVisible(false);

      pathTextBox.getChildren().clear();
      pathTextBox.getChildren().add(alertPane);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    settingButton.setOnMouseClicked(
        event -> {
          if (settingBox.isVisible() || adminBox.isVisible()) {
            settingBox.setVisible(false);
            settingBox.setDisable(true);
            adminBox.setVisible(false);
            adminBox.setDisable(true);
          } else {
            settingBox.setVisible(true);
            settingBox.setDisable(false);
            if (SharedResources.getCurrentUser().getLevel() == PermissionLevel.ADMIN
                || SharedResources.getCurrentUser().getLevel() == PermissionLevel.ROOT) {
              adminBox.setVisible(true);
              adminBox.setDisable(false);
            }
          }
        });

    closeSettings.setOnMouseClicked(
        event -> {
          settingBox.setVisible(false);
          settingBox.setDisable(true);
        });

    closeAdmin.setOnMouseClicked(
        event -> {
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });

    submitSetting.setOnMouseClicked(
        event -> {
          submitSettings();
        });

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
              .zoomBy(+0.2, new Point2D(bounds.getCenterX(), bounds.getCenterY()));
        });

    zoomOut.setOnMouseClicked(
        event -> {
          javafx.geometry.Bounds bounds = pane.getTargetViewport();
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .zoomBy(-0.2, new Point2D(bounds.getCenterX(), bounds.getCenterY()));
        });

    List<String> nodeNames = dbConnection.getDestLongnames();

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end
    endSelector.setDisable(true);

    startSelector.setOnAction(
        event -> {
          pathAnimations.clear();
          drawGroup.getChildren().clear();
          drawGroup.getChildren().add(mapImg);
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
          } else if (methodSelector.getValue().equals("Dijkstra's")) {
            pathfinder.setPathMethod(new Dijkstras(graph));
          }

          paths = pathfinder.getPathMethod().pathfind(start, end, !noStairs.isSelected());

          System.out.println(paths);

          textDirections = new LinkedList<>();
          populateTextDirections(paths);

          // index 0 in floors in this path - not allFloors
          currentFloor = 0;

          drawPaths(paths);
          if (paths.size() > 0) {
            pane.animate(Duration.millis(200))
                .interpolateWith(Interpolator.EASE_BOTH)
                .centreOn(paths.get(0).centerToPath(7));

            displayFloor();
            endSelector.setDisable(true);
          }
        });

    clearBtn.setOnMouseClicked(
        event -> {
          // clear paths
          drawGroup.getChildren().clear();
          drawGroup.getChildren().add(mapImg);

          startSelector.clearSelection();
          endSelector.clearSelection();

          // clear list of floors
          floors.clear();

          startSelector.clear();
          endSelector.clear();
        });

    generateqr.setDisable(true);

    generateqr.setOnMouseClicked(
        event -> {
          Stage popup = new Stage();

          popup.setX(Fapp.getPrimaryStage().getWidth() * 0.4);
          popup.setY(Fapp.getPrimaryStage().getHeight() * 0.4);

          QRCodeWriter qrCodeWriter = new QRCodeWriter();

          String text =
              "TEXT: "
                  + textDirections.stream()
                      .reduce(
                          new LinkedList<>(),
                          (lstAccum, lst) -> {
                            lstAccum.addAll(lst);
                            return lstAccum;
                          })
                      .stream()
                      .map(TextDirection::toString)
                      .collect(Collectors.joining());

          Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
          hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
          BitMatrix bitMatrix;
          int size = 500;
          try {
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hintMap);
          } catch (WriterException e) {
            throw new RuntimeException(e);
          }

          WritableImage img = new WritableImage(size * 2, size * 2);
          PixelWriter writer = img.getPixelWriter();
          Color dark = Color.rgb(1, 45, 90);
          Color white = Color.WHITE;

          for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
              if (bitMatrix.get(i, j)) {
                writer.setColor(2 * i, 2 * j, dark);
                writer.setColor(2 * i + 1, 2 * j, dark);
                writer.setColor(2 * i, 2 * j + 1, dark);
                writer.setColor(2 * i + 1, 2 * j + 1, dark);
              } else {
                writer.setColor(2 * i, 2 * j, white);
                writer.setColor(2 * i + 1, 2 * j, white);
                writer.setColor(2 * i, 2 * j + 1, white);
                writer.setColor(2 * i + 1, 2 * j + 1, white);
              }
            }
          }

          ImageView imgView = new ImageView(img);

          StackPane root = new StackPane();
          root.getChildren().add(imgView);

          popup.setScene(new Scene(root, size * 2 + 50, size * 2 + 50));
          popup.show();
        });

    pane.setOnMouseClicked(
        event -> {
          settingBox.setVisible(false);
          settingBox.setDisable(true);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });
    pane.setOnDragDetected(
        event -> {
          settingBox.setVisible(false);
          settingBox.setDisable(true);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });

    currentFloor = 0;
    floors = new ArrayList<>();
    pathAnimations = new ArrayList<>();

    // Bath, Conf, Dept, Elev, Exit, Info, Labs, Rest, Retl, serv, stai

    locationsButtons = new HashMap<NodeType, MFXToggleButton>();
    locationGroups = new HashMap<NodeType, Group>();

    displayTypes =
        new ArrayList<>(
            Arrays.asList(
                NodeType.ELEV,
                NodeType.CONF,
                NodeType.DEPT,
                NodeType.BATH,
                NodeType.EXIT,
                NodeType.INFO,
                NodeType.LABS,
                NodeType.REST,
                NodeType.RETL,
                NodeType.SERV,
                NodeType.STAI));
    displayButtons = new ArrayList<>();
    displayButtons.add(showLocations);

    for (int typeNum = 0; typeNum < displayButtons.size(); typeNum++) {

      NodeType type = displayTypes.get(typeNum);

      drawLocations("L1", showLocations.isSelected(), type);

      displayButtons
          .get(typeNum)
          .setOnAction(
              event -> {
                locationGroups.get(type).setVisible(showLocations.isSelected());
              });

      locationsButtons.put(type, showLocations);

      drawGroup.getChildren().add(locationGroups.get(type));
    }

    graph = new Graph(dbConnection, AbsController.today);
  }

  private void drawPaths(ArrayList<Path> paths) {
    for (int i = 0; i < paths.size(); i++) {
      Path path = paths.get(i);
      // skip paths of length 1 (going through elevator node)
      // except for first and last path
      if (path.numNodes > 1 || i == 0 || i == paths.size() - 1) {
        // group to sore this floor's path
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
        drawGroup.getChildren().add(g);
        g.setVisible(false);

        floors.add(path.getFloor());
      }
    }
  }

  private void drawLocations(String floor, boolean visibility, NodeType type) {

    locationGroups.put(type, new Group());

    // copied directly from mapeditor function
    dbConnection
        .getNodesOnFloor(floor)
        .forEach(
            node -> {
              List<Location> locations =
                  dbConnection.getLocations(node.getNodeID(), today).stream().toList();

              List<String> shortnames = new ArrayList<>();
              for (int locatNum = 0; locatNum < locations.size(); locatNum++) {
                if (locations.get(locatNum).getNodeType() == type) {
                  shortnames.add(locations.get(locatNum).getShortName());
                }
              }

              /*
              List<String> shortnames =
                  dbConnection.getLocations(node.getNodeID(), today).stream()
                      .filter(Location::isDestination)
                      .map(Location::getShortName)
                      .toList();

                 */

              if (!shortnames.isEmpty()) {
                String label = String.join(", ", shortnames);
                locationGroups
                    .get(type)
                    .getChildren()
                    .add(
                        new NodeText(
                            node.getNodeID(),
                            node.getX() - label.length() * 5,
                            node.getY() - 10,
                            label));
              }
            });

    locationGroups.get(type).setVisible(visibility);
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

    Polygon triangle = new Polygon();

    if (up) {
      triangle.getPoints().addAll(x - 15, y + 15, x + 15, y + 15, x, y - 15);
      triangle.setFill(Color.rgb(0, 100, 0));
    } else {
      triangle.getPoints().addAll(x - 15, y - 15, x + 15, y - 15, x, y + 15);
      triangle.setFill(Color.rgb(100, 0, 0));
    }

    triangle.setOnMouseClicked(
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
    return triangle;
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

    Iterator<javafx.scene.Node> itr = drawGroup.getChildren().iterator();
    itr.next(); // skip first child which is the imageview
    while (itr.hasNext()) {
      itr.next().setVisible(false);
    }

    for (int typeNum = 0; typeNum < displayButtons.size(); typeNum++) {

      NodeType type = displayTypes.get(typeNum);

      drawLocations(floors.get(currentFloor), showLocations.isSelected(), type);

      drawGroup.getChildren().add(locationGroups.get(type));
    }

    // stop all animations
    pathAnimations.forEach(ParallelTransition::stop);

    // show and start animation for current floor
    drawGroup
        .getChildren()
        .get(currentFloor + 1)
        .setVisible(true); // offset by 1 because first child is gesture pane
    pathAnimations.get(currentFloor).play();

    // calculated that 1537 pixels = 484 feet = 147.5 m
    // 3.175 pixels per foot, 10.42 pixels per meter
    // 1 m/s walking speed = 60m/min

    // add lengths of each path in list of paths
    double totalLength = paths.stream().map(Path::pathLength).reduce(0.0, Double::sum);
    System.out.println("path length pixels " + totalLength);
    int minutes = (int) Math.ceil(totalLength * (147.5 / 1537) / 60);

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

      if (!floorDirections.isEmpty()) {

        System.out.println(
            "floor dir: "
                + floorDirections.get(i).getDirection()
                + " dist "
                + floorDirections.get(i).getDistance());
        textDirections.add(floorDirections);
      }
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

  /** Submits the Admin settings */
  private void submitSettings() {
    if (!(pathDate.getValue() == null)) {
      graph = new Graph(dbConnection, pathDate.getValue());
    }
    if (!(pathMessage.getText().equals(""))) {
      pathTextBox.setVisible(true);
    } else {
      pathTextBox.setVisible(false);
    }
    adminBox.setVisible(false);
    adminBox.setDisable(true);
    settingBox.setVisible(false);
    settingBox.setDisable(true);
  }
}
