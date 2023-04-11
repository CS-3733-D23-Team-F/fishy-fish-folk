package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Graph;
import edu.wpi.fishfolk.pathfinding.Path;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

public class PathfindingController extends AbsController {

  @FXML MFXButton signageNav;
  @FXML MFXButton mealNav;
  @FXML MFXButton officeNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton sideBar;

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
  @FXML MFXButton zoomOut;
  @FXML MFXButton zoomIn;
  @FXML GesturePane pane;
  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  ArrayList<String> floors;
  int currentFloor;
  int zoom;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    // segments = new LinkedList<>();

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

    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    List<String> nodeNames =
        dbConnection.locationTable
            .executeQuery("SELECT longname", "WHERE type != 'HALL' ORDER BY longname ASC").stream()
            .map(elt -> elt[0])
            .toList();

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end

    startSelector.setOnAction(
        event -> {
          start = dbConnection.getMostRecentUNode(startSelector.getValue());
          // floor of the selected unode id
          currentFloor = 0;
          System.out.println("start node: " + start);
        });
    endSelector.setOnAction(
        event -> {
          end = dbConnection.getMostRecentUNode(endSelector.getValue());
          System.out.println("end node: " + end);
        });

    currentFloor = 0;
    floors = new ArrayList<>();
    graph = new Graph(dbConnection);

    submitBtn.setOnMouseClicked(
        event -> {
          paths = graph.AStar(start, end);

          // create segments for each path and put into groups
          drawPaths(paths);
          currentFloor = 0;
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .centreOn(paths.get(0).centerToPath(7));
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
    return new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }
}
