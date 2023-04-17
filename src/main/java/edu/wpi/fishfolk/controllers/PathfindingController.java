package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.pathfinding.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply, furnitureNav;
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
  int zoom;
  ArrayList<TextDirection> textDirections;

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

    List<String> nodeNames =
        dbConnection.locationTable
            .executeQuery("SELECT longname", "WHERE type != 'HALL' ORDER BY longname ASC").stream()
            .map(elt -> elt[0])
            .toList();

    startSelector.getItems().addAll(nodeNames);
    endSelector.getItems().addAll(nodeNames); // same options for start and end
    endSelector.setDisable(true);
    startSelector.setOnAction(
        event -> {
          pathGroup.getChildren().clear();
          pathGroup.getChildren().add(mapImg);
          // clear list of floors
          floors.clear();
          start = dbConnection.getMostRecentUNode(startSelector.getValue());
          // floor of the selected unode id
          currentFloor = 0;
          System.out.println("start node: " + start);
          endSelector.setVisible(true);
          endSelector.setDisable(false);
        });
    endSelector.setOnAction(
        event -> {
          end = dbConnection.getMostRecentUNode(endSelector.getValue());
          System.out.println("end node: " + end);
          pathfinder = new AStar(graph);
          paths = pathfinder.pathfind(start, end, true);

          // create segments for each path and put into groups

          drawPaths(paths);
          currentFloor = 0;
          pane.animate(Duration.millis(200))
              .interpolateWith(Interpolator.EASE_BOTH)
              .centreOn(paths.get(0).centerToPath(7));
          displayFloor(currentFloor);
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

              if (col == 4) {
                col = 0;
                row++;
              }

              grid.add(anchorPane, col++, row);

              grid.setMinWidth(Region.USE_COMPUTED_SIZE);
              grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
              grid.setMaxWidth(Region.USE_PREF_SIZE);

              grid.setMinHeight(Region.USE_COMPUTED_SIZE);
              grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
              grid.setMaxHeight(Region.USE_PREF_SIZE);

              GridPane.setMargin(anchorPane, new Insets(10));
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
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

    paths.forEach(
        path -> {
          LinkedList<Line> segments = new LinkedList<>();
          for (int i = 1; i < path.numNodes; i++) {
            segments.add(line(path.points.get(i - 1), path.points.get(i)));
          }

          if (!(path.numNodes == 1)
              || (path.equals(paths.get(0)))
              || (path.equals(paths.get(paths.size() - 1)))) {
            Group g = new Group();
            g.getChildren().addAll(segments);
            pathGroup.getChildren().add(g);
            g.setVisible(false);

            floors.add(path.floor);
          }
        });
  }

  private void displayFloor(int floor) {

    mapImg.setImage(images.get(floors.get(floor)));
    floorDisplay.setText("Floor " + floors.get(floor));
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
