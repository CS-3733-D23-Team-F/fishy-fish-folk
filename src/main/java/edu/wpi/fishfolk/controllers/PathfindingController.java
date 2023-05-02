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
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.mapeditor.NodeText;
import edu.wpi.fishfolk.pathfinding.*;
import edu.wpi.fishfolk.util.NodeType;
import edu.wpi.fishfolk.util.PermissionLevel;
import io.github.palexdev.materialfx.controls.*;
import java.io.IOException;
import java.time.LocalDate;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

public class PathfindingController extends AbsController {

  @FXML Text floorDisplay;

  @FXML MFXFilterComboBox<String> startSelector;
  @FXML MFXFilterComboBox<String> endSelector;
  @FXML MFXFilterComboBox<String> methodSelector;

  @FXML MFXFilterComboBox<String> currLocation;
  @FXML MFXButton clearBtn;

  @FXML Group drawGroup;

  @FXML Group locationGroup;
  @FXML ImageView mapImg;

  @FXML MFXButton zoomOut;
  @FXML MFXButton zoomIn;
  @FXML GesturePane pane;
  @FXML MFXButton slideUp;
  @FXML MFXButton slideDown;
  @FXML VBox textInstruct;

  @FXML VBox settingBox;
  @FXML MFXButton settingButton;

  @FXML MFXButton closeSettings;

  @FXML
  MFXCheckbox elevCheck,
      confCheck,
      deptCheck,
      restCheck,
      exitCheck,
      infoCheck,
      labsCheck,
      retlCheck,
      servCheck,
      staiCheck,
      allCheck;

  @FXML VBox adminBox;

  @FXML MFXButton closeAdmin;

  @FXML MFXDatePicker pathDate;

  @FXML MFXButton submitSetting;

  @FXML HBox pathTextBox;

  @FXML ScrollPane scroll;
  @FXML GridPane grid;
  @FXML MFXTextField estimatedtime;
  @FXML MFXButton generateqr;

  @FXML MFXToggleButton noStairs;

  @FXML MFXButton upFloor, downFloor;

  @FXML MFXToggleButton locationMoves;

  Group alertGroup;

  private PathfindSingleton pathfinder;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  Map<NodeType, Group> locationGroups;
  Map<NodeType, MFXCheckbox> locationsButtons;

  ArrayList<NodeType> displayTypes;

  ArrayList<MFXCheckbox> displayButtons;

  ArrayList<Location> mapAlerts;

  ArrayList<LocalDate> alertDates;

  Map<Location, Node> alertsNodes;

  ArrayList<String> floors;

  int currentFloor;

  int currFloorNoPath;

  String pathMethod;

  String defaultLocation;

  ParallelTransition parallelTransition;

  ArrayList<String> eachFloor;

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

    pathMethod = "A*";
    methodSelector.setValue(pathMethod);
    methodSelector.setText(pathMethod);

    slideUp.setVisible(false);
    slideDown.setVisible(false);
    slideUp.setDisable(true);
    slideDown.setDisable(true);

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
      alertPane.setStyle("-fx-background-radius: 15");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    settingBox.setTranslateX(1000);
    adminBox.setTranslateX(1000);
    settingBox.setVisible(false);
    adminBox.setVisible(false);

    settingButton.setOnMouseClicked(
        event -> {
          if (settingBox.isVisible() || adminBox.isVisible()) {
            settingBox.setVisible(false);
            settingBox.setDisable(true);
            adminBox.setVisible(false);
            adminBox.setDisable(true);
            adminBox.setTranslateX(1000);
            settingBox.setTranslateX(1000);
            currLocation.setValue(defaultLocation);
            currLocation.setText(defaultLocation);
            methodSelector.setValue(pathMethod);
            methodSelector.setText(pathMethod);

          } else {
            textInstruct.setTranslateY(252);
            if (!slideUp.isDisabled() || !slideDown.isDisabled()) {
              slideUp.setDisable(false);
              slideUp.setVisible(true);
              slideDown.setVisible(false);
              slideDown.setDisable(true);
            }

            adminBox.setTranslateX(0);
            settingBox.setTranslateX(0);
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
          settingBox.setTranslateX(1000);
          settingBox.setVisible(false);
          settingBox.setDisable(true);
        });

    closeAdmin.setOnMouseClicked(
        event -> {
          adminBox.setTranslateX(1000);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });

    submitSetting.setOnMouseClicked(
        event -> {
          submitSettings();
        });

    locationMoves.setOnMouseClicked(
        event -> {
          alertGroup.setVisible(locationMoves.isSelected());
        });

    slideUp.setOnMouseClicked(
        event -> {
          settingBox.setTranslateX(1000);
          settingBox.setVisible(false);
          settingBox.setDisable(true);
          adminBox.setTranslateX(1000);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
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

    startSelector.getItems().add("Current Location");
    startSelector.getItems().addAll(nodeNames);
    defaultLocation = nodeNames.get(0);
    currLocation.getItems().addAll(nodeNames);
    currLocation.setValue(defaultLocation);
    currLocation.setText(defaultLocation);
    endSelector.getItems().addAll(nodeNames); // same options for start and end
    endSelector.setDisable(true);

    eachFloor = new ArrayList<>(Arrays.asList("L2", "L1", "1", "2", "3"));
    currFloorNoPath = 1;
    floorDisplay.setText("Floor " + "L1");

    upFloor.setOnMouseClicked(
        event -> {
          downFloor.setDisable(false);
          currFloorNoPath++;
          if (currFloorNoPath == 4) {
            upFloor.setDisable(true);
          }
          mapImg.setImage(images.get(eachFloor.get(currFloorNoPath)));
          floorDisplay.setText("Floor " + eachFloor.get(currFloorNoPath));

          locationGroup.getChildren().clear();
          generateAlertBoxs();

          locationGroup.setVisible(true);

          for (int typeNum = 0; typeNum < displayButtons.size() - 1; typeNum++) {

            NodeType type = displayTypes.get(typeNum);

            drawLocations(
                eachFloor.get(currFloorNoPath), locationsButtons.get(type).isSelected(), type);
          }
        });

    downFloor.setOnMouseClicked(
        event -> {
          upFloor.setDisable(false);
          currFloorNoPath--;
          if (currFloorNoPath == 0) {
            downFloor.setDisable(true);
          }
          mapImg.setImage(images.get(eachFloor.get(currFloorNoPath)));
          floorDisplay.setText("Floor " + eachFloor.get(currFloorNoPath));

          locationGroup.getChildren().clear();
          generateAlertBoxs();

          locationGroup.setVisible(true);

          for (int typeNum = 0; typeNum < displayButtons.size() - 1; typeNum++) {

            NodeType type = displayTypes.get(typeNum);

            drawLocations(
                eachFloor.get(currFloorNoPath), locationsButtons.get(type).isSelected(), type);
          }
        });

    startSelector.setOnAction(
        event -> {
          if (startSelector.getValue() != null) {
            pathAnimations.clear();

            drawGroup.getChildren().clear();

            drawGroup.getChildren().add(mapImg);

            drawGroup.getChildren().add(locationGroup);

            // clear list of floors
            floors.clear();
            if (startSelector.getValue().equals("Current Location")) {
              start = dbConnection.getNodeIDFromLocation(defaultLocation, today);
            } else {
              start = dbConnection.getNodeIDFromLocation(startSelector.getValue(), today);
            }

            // floor of the selected unode id
            currentFloor = 0;
            System.out.println("start node: " + start);
            endSelector.setVisible(true);
            endSelector.setDisable(false);
          }
        });

    endSelector.setOnAction(
        event -> {
          if (endSelector.getValue() != null) {
            grid.getChildren().clear();
            end = dbConnection.getNodeIDFromLocation(endSelector.getValue(), today);
            System.out.println("end node: " + end);
            pathfinder = PathfindSingleton.PATHFINDER;
            if (pathMethod.equals("A*")) {
              pathfinder.setPathMethod(new AStar(graph));
            }
            if (pathMethod.equals("BFS")) {
              pathfinder.setPathMethod(new BFS(graph));
            }
            if (pathMethod.equals("DFS")) {
              pathfinder.setPathMethod(new DFS(graph));
            }
            if (pathMethod.equals("Dijkstra's")) {
              pathfinder.setPathMethod(new Dijkstras(graph));
            }

            paths = pathfinder.getPathMethod().pathfind(start, end, !noStairs.isSelected());

            System.out.println(paths);

            slideUp.setVisible(true);
            slideUp.setDisable(false);
            downFloor.setDisable(true);
            upFloor.setDisable(true);

            textDirections = new LinkedList<>();
            populateTextDirections(paths);

            // index 0 in floors in this path - not allFloors
            currentFloor = 0;

            for (int i = 0; i < paths.size(); i++) {
              Path path = paths.get(i);
              floors.add(path.getFloor());
            }

            drawPaths(paths);
            if (paths.size() > 0) {
              pane.animate(Duration.millis(200))
                  .interpolateWith(Interpolator.EASE_BOTH)
                  .centreOn(paths.get(0).centerToPath(7));

              displayFloor();
              endSelector.setDisable(true);
            }
          }
        });

    clearBtn.setOnMouseClicked(
        event -> {
          // clear paths

          slideUp.setVisible(false);
          slideDown.setVisible(false);
          slideUp.setDisable(true);
          slideDown.setDisable(true);
          if (!(currFloorNoPath == 4)) {
            upFloor.setDisable(false);
          }
          if (!(currFloorNoPath == 0)) {
            downFloor.setDisable(false);
          }

          textInstruct.setTranslateY(252);

          drawGroup.getChildren().clear();
          drawGroup.getChildren().add(mapImg);

          drawGroup.getChildren().add(locationGroup);

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
          settingBox.setTranslateX(1000);
          settingBox.setVisible(false);
          settingBox.setDisable(true);
          adminBox.setTranslateX(1000);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });
    pane.setOnDragDetected(
        event -> {
          settingBox.setTranslateX(1000);
          settingBox.setVisible(false);
          settingBox.setDisable(true);
          adminBox.setTranslateX(1000);
          adminBox.setVisible(false);
          adminBox.setDisable(true);
        });

    currentFloor = 0;
    floors = new ArrayList<>();
    pathAnimations = new ArrayList<>();

    // Bath, Conf, Dept, Elev, Exit, Info, Labs, Rest, Retl, serv, stai

    locationsButtons = new HashMap<NodeType, MFXCheckbox>();
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
    displayButtons =
        new ArrayList<>(
            Arrays.asList(
                elevCheck, confCheck, deptCheck, restCheck, exitCheck, infoCheck, labsCheck,
                restCheck, retlCheck, servCheck, staiCheck));
    displayButtons.add(elevCheck);

    for (int typeNum = 0; typeNum < displayButtons.size() - 1; typeNum++) {

      NodeType type = displayTypes.get(typeNum);

      locationsButtons.put(type, displayButtons.get(typeNum));

      drawLocations("L1", locationsButtons.get(type).isSelected(), type);
    }

    allCheck.setOnAction(
        event -> {
          for (int typeNum = 0; typeNum < displayButtons.size() - 1; typeNum++) {

            NodeType type = displayTypes.get(typeNum);
            if (allCheck.isSelected()) {
              locationsButtons.get(type).setSelected(true);
              drawLocations(
                  eachFloor.get(currFloorNoPath), locationsButtons.get(type).isSelected(), type);
            } else {
              locationsButtons.get(type).setSelected(false);
              locationGroups.get(type).setVisible(false);
            }
          }
        });

    setAlerts();
    generateAlertBoxs();

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

        parallelTransition = new ParallelTransition();
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
                        p2.getX() - 50,
                        p2.getY() - 25,
                        direction(path.getFloor(), paths.get(1).getFloor()),
                        true,
                        i));
          }

        } else if (i == paths.size() - 1) { // last path segment

          if (paths.size() > 1) {

            Point2D p1 = path.points.get(0);
            g.getChildren()
                .add(
                    generatePathButtons(
                        p1.getX() - 50,
                        p1.getY() - 25,
                        direction(path.getFloor(), paths.get(paths.size() - 2).getFloor()),
                        false,
                        i));
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
                      p1.getX() - 50,
                      p1.getY() - 25,
                      direction(
                          path.getFloor(), // this button goes in reverse
                          paths.get(i - 1).getFloor()),
                      false,
                      i));

          Point2D p2 = path.points.get(path.numNodes - 1);
          g.getChildren()
              .add(
                  generatePathButtons(
                      p2.getX() - 50,
                      p2.getY() - 25,
                      direction(path.getFloor(), paths.get(i + 1).getFloor()),
                      true,
                      i));
        }

        pathAnimations.add(parallelTransition);

        if (paths.size() == 1) {
          Point2D p2 = path.points.get(path.numNodes - 1);
          NodeCircle end = new NodeCircle(-1, p2.getX(), p2.getY(), 12);
          end.setFill(Color.rgb(1, 45, 90));
          g.getChildren().add(end);
        }
        drawGroup.getChildren().add(g);
        g.setVisible(false);
      }
    }
  }

  private void drawLocations(String floor, boolean visibility, NodeType type) {

    locationGroups.put(type, new Group());

    if (visibility) {
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

      locationsButtons
          .get(type)
          .setOnAction(
              event -> {
                locationGroups.get(type).setVisible(locationsButtons.get(type).isSelected());
              });

      locationGroups.get(type).setVisible(visibility);
      locationGroup.getChildren().add(locationGroups.get(type));

    } else {
      locationsButtons
          .get(type)
          .setOnAction(
              event -> {
                drawLocations(floor, true, type);
              });
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
  private javafx.scene.Node generatePathButtons(
      double x, double y, boolean up, boolean forwards, int floorNum) {

    HBox floorBox = new HBox();
    floorBox.setMinHeight(50);
    floorBox.setMinWidth(100);
    floorBox.setStyle("-fx-background-radius:15; -fx-background-color: #f1f1f1");
    floorBox.setEffect(new DropShadow(0, 0, 1, Color.web("#000000")));
    floorBox.setLayoutX(x);
    floorBox.setLayoutY(y);
    floorBox.setVisible(true);

    Text floorText = new Text();

    Polygon triangle = new Polygon();

    Polyline segment = new Polyline();

    if (up) {
      triangle.getPoints().addAll(x - 15, y + 15, x + 15, y + 15, x, y - 15);
      triangle.setFill(Color.rgb(0, 100, 0));
      segment =
          new Polyline(
              floorBox.getLayoutX() + 3,
              floorBox.getLayoutY() - 3,
              floorBox.getLayoutX() + 3,
              floorBox.getLayoutY() + 3);

    } else {
      triangle.getPoints().addAll(x - 15, y - 15, x + 15, y - 15, x, y + 15);
      triangle.setFill(Color.rgb(100, 0, 0));
      segment =
          new Polyline(
              floorBox.getLayoutX() + 3,
              floorBox.getLayoutY() + 3,
              floorBox.getLayoutX() + 3,
              floorBox.getLayoutY() - 3);
    }

    if (forwards) {
      floorText.setText("Floor: " + floors.get(floorNum + 1));
    } else {
      floorText.setText("Floor: " + floors.get(floorNum - 1));
    }

    floorText.setFont(new Font("OpenSans", 15));
    floorBox.setAlignment(Pos.CENTER);

    floorBox.setOnMouseClicked(
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

    Duration duration = new Duration(750);

    PathTransition animation = new PathTransition(duration, segment, triangle);
    animation.setInterpolator(Interpolator.LINEAR);
    animation.setCycleCount(Timeline.INDEFINITE);
    animation.setAutoReverse(true);
    parallelTransition.getChildren().add(animation);

    floorBox.getChildren().add(floorText);

    floorBox.getChildren().add(triangle);

    return floorBox;
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

    currFloorNoPath = eachFloor.indexOf(floors.get(currentFloor));

    mapImg.setImage(images.get(floors.get(currentFloor)));
    floorDisplay.setText("Floor " + floors.get(currentFloor));

    Iterator<javafx.scene.Node> itr = drawGroup.getChildren().iterator();
    itr.next(); // skip first child which is the imageview
    while (itr.hasNext()) {
      itr.next().setVisible(false);
    }

    locationGroup.getChildren().clear();
    generateAlertBoxs();

    locationGroup.setVisible(true);

    for (int typeNum = 0; typeNum < displayButtons.size() - 1; typeNum++) {

      NodeType type = displayTypes.get(typeNum);

      drawLocations(floors.get(currentFloor), locationsButtons.get(type).isSelected(), type);
    }

    // stop all animations
    pathAnimations.forEach(ParallelTransition::stop);

    // show and start animation for current floor
    drawGroup
        .getChildren()
        .get(currentFloor + 2)
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
          ((LinkedList<TextDirection>) floorDirections)
              .addFirst(
                  textDirectionBetweenFloors(
                      paths.get(lastFloorIdx - 1).getToNextPath(),
                      paths.get(lastFloorIdx).getFloor()));
        }
        floorDirections.add(new TextDirection(Direction.START, "End at " + endSelector.getValue()));
      }

      if (!floorDirections.isEmpty()) {
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
    if (!(methodSelector.getValue() == null)) {
      pathMethod = methodSelector.getValue();
    }
    if (!(currLocation.getValue() == null)) {
      defaultLocation = currLocation.getValue();
    }

    adminBox.setVisible(false);
    adminBox.setDisable(true);
    settingBox.setVisible(false);
    settingBox.setDisable(true);
  }

  private void setAlerts() {
    mapAlerts = new ArrayList<Location>();
    alertDates = new ArrayList<LocalDate>();
    alertsNodes = new HashMap<Location, Node>();
    dbConnection
        .getAllEntries(TableEntryType.ALERT)
        .forEach(
            obj -> {
              edu.wpi.fishfolk.database.TableEntry.Alert a =
                  (edu.wpi.fishfolk.database.TableEntry.Alert) obj;

              String name = a.getLongName();
              if (!name.equals("no location")) {
                int alertID = dbConnection.getNodeIDFromLocation(name, today);
                List<Location> mapLocations =
                    dbConnection.getLocations(alertID, today).stream().toList();
                for (int i = 0; i < mapLocations.size(); i++) {
                  if (!mapAlerts.contains(mapLocations.get(i))
                      && (mapLocations.get(i)).isDestination()) {
                    mapAlerts.add(mapLocations.get(i));
                    alertDates.add(a.getDate());
                  }
                }
              }
            });

    dbConnection
        .getAllEntries(TableEntryType.NODE)
        .forEach(
            node -> {
              edu.wpi.fishfolk.database.TableEntry.Node n =
                  (edu.wpi.fishfolk.database.TableEntry.Node) node;
              for (int i = 0; i < mapAlerts.size(); i++) {
                if (n.getNodeID()
                    == dbConnection.getNodeIDFromLocation(mapAlerts.get(i).getLongName(), today)) {
                  alertsNodes.put(mapAlerts.get(i), n);
                }
              }
            });
  }

  private void generateAlertBoxs() {
    alertGroup = new Group();
    for (int locat = 0; locat < mapAlerts.size(); locat++) {
      Location currLocat = mapAlerts.get(locat);
      if (alertsNodes.get(currLocat).getFloor().equals(eachFloor.get(currFloorNoPath))) {
        VBox mapAlert = new VBox();
        mapAlert.setMinHeight(50);
        mapAlert.setMinWidth(100);
        mapAlert.setStyle("-fx-background-radius:15; -fx-background-color: #f1f1f1");
        mapAlert.setEffect(new DropShadow(0, 0, 1, Color.web("#000000")));
        mapAlert.setLayoutX(alertsNodes.get(currLocat).getX());
        mapAlert.setLayoutY(alertsNodes.get(currLocat).getY());
        mapAlert.setVisible(true);

        Text alertText = new Text();
        alertText.setFont(new Font("OpenSans", 15));
        alertText.setText(currLocat.getShortName());

        Text moveDate = new Text();
        moveDate.setFont(new Font("OpenSans", 15));
        moveDate.setText("Moves On " + alertDates.get(locat));

        mapAlert.setAlignment(Pos.CENTER);

        mapAlert.getChildren().add(alertText);
        mapAlert.getChildren().add(moveDate);
        mapAlert.setPadding(new Insets(0, 5, 0, 5));

        alertGroup.getChildren().add(mapAlert);
      }
    }
    alertGroup.setVisible(locationMoves.isSelected());
    locationGroup.getChildren().add(alertGroup);
  }
}
