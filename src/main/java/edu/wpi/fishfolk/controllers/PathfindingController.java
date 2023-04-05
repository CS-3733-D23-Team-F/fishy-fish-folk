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
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

  @FXML AnchorPane mapAnchor;
  @FXML ImageView mapImg;
  @FXML Text directionInstructions;
  @FXML MFXButton backButton;
  @FXML MFXButton nextButton;
  @FXML MFXButton homeButton;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;

  int start, end;
  Graph graph;
  ArrayList<Path> paths;

  HashMap<String, Image> images;
  ArrayList<String> floors;
  int currentFloor;
  HashMap<String, String> mapImgURLs;

  public PathfindingController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    ArrayList nodeNames = dbConnection.nodeTable.getDestLongNames();
    // segments = new LinkedList<>();
    System.out.println(dbConnection.nodeTable.getTableName());

    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
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
    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end

    startSelector.setOnAction(
        event -> {
          Node startNode = dbConnection.nodeTable.getNode("longname", startSelector.getValue());
          start = startNode.nid;
          currentFloor = 0;

          System.out.println("start node: " + start);
        });
    endSelector.setOnAction(
        event -> {
          end = dbConnection.nodeTable.getNode("longname", endSelector.getValue()).nid;
          System.out.println("end node: " + end);
        });

    floors = new ArrayList<>(3);
    currentFloor = 0;

    graph = new Graph(dbConnection.nodeTable, dbConnection.edgeTable);

    images = new HashMap<>();

    mapImgURLs = new HashMap<>();

    mapImgURLs.put("L1", "map/00_thelowerlevel1.png");
    mapImgURLs.put("L2", "map/00_thelowerlevel2.png");
    mapImgURLs.put("GG", "map/00_thegroundfloor.png");
    mapImgURLs.put("1", "map/01_thefirstfloor.png");
    mapImgURLs.put("2", "map/02_thesecondfloor.png");
    mapImgURLs.put("3", "map/03_thethirdfloor.png");

    submitBtn.setOnMouseClicked(
        event -> {
          paths = graph.AStar(start, end);

          for (Path path : paths) {

            images.put(
                path.floor, new Image(Fapp.class.getResourceAsStream(mapImgURLs.get(path.floor))));
          }
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
          // remove all groups from mapanchor
          Iterator<javafx.scene.Node> itr = mapAnchor.getChildren().iterator();
          itr.next(); // skip first child which is the imageview

          while (itr.hasNext()) {
            itr.next();
            itr.remove();
          }

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
          g.setVisible(false);

          floors.add(path.floor);

          mapAnchor.getChildren().add(g);
        });
  }

  private void displayFloor(int floor) {

    mapImg.setImage(images.get(floors.get(floor)));

    Iterator<javafx.scene.Node> itr = mapAnchor.getChildren().iterator();
    itr.next(); // skip first child which is the imageview

    while (itr.hasNext()) {
      itr.next().setVisible(false);
    }
    mapAnchor.getChildren().get(floor + 1).setVisible(true);
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
