package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.Path;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.animation.Interpolator;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

public class PathfindingController extends AbsController {

  /*
    @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;

  @FXML MFXButton sideBar;

   */

  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton submitBtn;
  @FXML ChoiceBox<String> startSelector;
  @FXML ChoiceBox<String> endSelector;
  @FXML MFXButton clearBtn;

  @FXML Group pathGroup;
  @FXML ImageView mapImg;
  @FXML Text directionInstructions;
  @FXML MFXButton backButton;
  @FXML MFXButton nextButton;
  @FXML MFXButton homeButton;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;

  @FXML GesturePane pane;
  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  int currentFloor;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    ArrayList<String> nodeNames =
        (ArrayList<String>)
            dbConnection.locationTable
                .executeQuery("SELECT longname", "WHERE type != 'HALL' ORDER BY longname ASC")
                .stream()
                .map(elt -> elt[0])
                .toList();
    // segments = new LinkedList<>();

    pane.setOnMouseClicked(
        e -> {
          if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
            Point2D pivotOnTarget =
                pane.targetPointAt(new Point2D(e.getX(), e.getY()))
                    .orElse(pane.targetPointAtViewportCentre());
            // increment of scale makes more sense exponentially instead of linearly
            pane.animate(Duration.millis(200))
                .interpolateWith(Interpolator.EASE_BOTH)
                .zoomBy(pane.getCurrentScale(), pivotOnTarget);
          }
        });

    /*
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    slider.setTranslateX(-400);
    sideBarClose.setVisible(false);
    menuWrap.setVisible(false);
    sideBar.setOnMouseClicked(
        event -> {
          menuWrap.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToX(400);
          slide.play();

          slider.setTranslateX(-400);
          menuWrap.setVisible(true);
          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(false);
                sideBarClose.setVisible(true);
              });
        });

    sideBarClose.setOnMouseClicked(
        event -> {
          menuWrap.setVisible(false);
          menuWrap.setDisable(true);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToX(-400);
          slide.play();

          slider.setTranslateX(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(true);
                sideBarClose.setVisible(false);
              });
        });

     */

    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end

    startSelector.setOnAction(
        event -> {
          Node startNode = new Node();
          startNode.construct(
              dbConnection.micronodeTable.get("longname", startSelector.getValue()));
          start = startNode.nid;
          currentFloor = 0;

          System.out.println("start node: " + start);
        });
    endSelector.setOnAction(
        event -> {
          Node startNode = new Node();
          startNode.construct(dbConnection.micronodeTable.get("longname", endSelector.getValue()));
          System.out.println("end node: " + end);
        });

    currentFloor = 0;

    graph = new Graph(dbConnection.micronodeTable, dbConnection.edgeTable);


    submitBtn.setOnMouseClicked(
        event -> {
          paths = graph.AStar(start, end);

          // create segments for each path and put into groups
          drawPaths(paths);
          currentFloor = 0;
          displayFloor(currentFloor);
          // directionInstructions.setText("Instructions: \n\n" + path.getDirections());
        });

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

    paths.forEach(
        path -> {
          LinkedList<Line> segments = new LinkedList<>();
          for (int i = 1; i < path.numNodes; i++) {
            segments.add(line(path.points.get(i - 1), path.points.get(i)));
          }
          Group g = new Group();
          g.getChildren().addAll(segments);
          pathGroup.getChildren().add(g);
          g.setVisible(false);

          floors.add(path.floor);
        });
  }

  private void displayFloor(int floor) {

    mapImg.setImage(images.get(floors.get(floor)));

    Iterator<javafx.scene.Node> itr = pathGroup.getChildren().iterator();
    itr.next(); // skip first child which is the imageview

    while (itr.hasNext()) {
      itr.next().setVisible(false);
    }

    pathGroup.getChildren().get(floor + 1).setVisible(true);
  }

  private Line line(Point2D p1, Point2D p2) {
    p1 = convert(p1);
    p2 = convert(p2);
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public Point2D convert(Point2D p) {

    // center gets mapped to center. center1 -> center2

    // values from viewport
    Point2D center1 = new Point2D(900 + 4050 / 2, 150 + 3000 / 2);

    // fit width/height
    Point2D center2 = new Point2D(900 / 2, 600 / 2);

    Point2D rel = p.subtract(center1); // p relative to the center

    // strech x and y separately
    // fit width/height / img width/height
    double x = rel.getX() * 900 / 4050;
    double y = rel.getY() * 600 / 3000;

    return new Point2D(x, y).add(center2);
  }
}
