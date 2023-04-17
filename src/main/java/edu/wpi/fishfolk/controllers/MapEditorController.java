package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.mapeditor.BuildingChecker;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.kurobako.gesturefx.GesturePane;

public class MapEditorController extends AbsController {

  @FXML MFXComboBox<String> floorSelector;
  @FXML ImageView mapImg;
  @FXML GesturePane pane;
  @FXML public Group drawGroup;
  @FXML MFXButton nextButton;
  @FXML MFXButton backButton;

  @FXML MFXTextField xText;
  @FXML MFXTextField yText;
  @FXML MFXTextField buildingText;
  @FXML MFXTextField floorText;

  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton furnitureNav;

  @FXML MFXButton closeServiceNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton conferenceNav;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton supplyNav;
  @FXML MFXButton serviceNav;

  @FXML MFXButton exitButton;

  @FXML AnchorPane slider;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;

  @FXML MFXButton addNode;
  @FXML MFXButton delNode;

  @FXML MFXTextField longnameText;
  @FXML MFXTextField shortnameText;
  @FXML MFXTextField typeText;
  @FXML MFXButton nextLocation;
  @FXML MFXButton prevLocation;

  FileChooser fileChooser;
  DirectoryChooser dirChooser;

  private EDITOR_STATE state;
  private int currentEditingNode;
  private List<Location> currentEditingLocations;
  private int currentEditingLocationIdx;

  private Group nodesGroup;

  private int currentFloor = 2;
  private List<Node> nodes;

  private BuildingChecker buildingChecker;

  private LocalDate today = LocalDate.of(2023, Month.JUNE, 1);

  public MapEditorController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(allFloors);
    Collections.reverse(floorsReverse);

    floorSelector.getItems().addAll(floorsReverse);

    nodesGroup = new Group();
    drawGroup.getChildren().add(nodesGroup);

    switchFloor(allFloors.get(currentFloor));

    floorSelector.setOnAction(
        event -> {
          currentFloor = allFloors.indexOf(floorSelector.getValue());
          switchFloor(allFloors.get(currentFloor));
          floorSelector.setText("Floor " + allFloors.get(currentFloor));
        });
    nextButton.setOnMouseClicked(
        event -> {
          if (currentFloor < allFloors.size() - 1) {
            currentFloor++;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText("Floor " + allFloors.get(currentFloor));
          }
        });
    backButton.setOnMouseClicked(
        event -> {
          if (currentFloor > 0) {
            currentFloor--;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText("Floor " + allFloors.get(currentFloor));
          }
        });

    state = EDITOR_STATE.IDLE;
    currentEditingLocationIdx = 0;

    buildingChecker = new BuildingChecker();

    pane.centreOn(new Point2D(1700, 1100));
    pane.zoomTo(0.4, new Point2D(2500, 1600));

    // prints mouse location to screen when clicked on map. Used to calculate building boundaries
    mapImg.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.ADDING) {
            // System.out.println("adding at " + event.getX() + ", " + event.getY());
            insertNode(event.getX(), event.getY());
            delNode.setDisable(false);
            state = EDITOR_STATE.IDLE;

          } else if (state == EDITOR_STATE.EDITING) {
            state = EDITOR_STATE.IDLE;
            currentEditingNode = -1;
            clearNodeFields();
            clearLocationFields();
          }
        });

    fileChooser = new FileChooser();
    dirChooser = new DirectoryChooser();

    addNode.setOnMouseClicked(
        event -> {
          // go to adding state no matter the previous state
          state = EDITOR_STATE.ADDING;
          // disable deleting when adding
          delNode.setDisable(true);
        });

    delNode.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING) {

            deleteNode(currentEditingNode);
          }
        });

    Fapp.getPrimaryStage()
        .getScene()
        .setOnKeyPressed(
            event -> {
              if (event.getCode() == KeyCode.DELETE && state == EDITOR_STATE.EDITING) {
                deleteNode(currentEditingNode);
              }
            });

    nextLocation.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING) {
            if (currentEditingLocationIdx < currentEditingLocations.size() - 1) {
              currentEditingLocationIdx++;
            }
            fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));
          }
        });

    prevLocation.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING) {
            if (currentEditingLocationIdx > 0) {
              currentEditingLocationIdx--;
            }
            fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));
          }
        });
  }

  private void switchFloor(String floor) {

    mapImg.setImage(images.get(floor));

    nodesGroup.getChildren().clear();

    nodes = dbConnection.getNodesOnFloor(floor);

    // System.out.println(unodes.size());

    nodes.forEach(this::drawNode);
  }

  // this also does some initialization now
  private void drawNode(Node node) {

    Point2D p = node.getPoint();
    NodeCircle c = new NodeCircle(node.getNodeID(), p.getX(), p.getY(), 4);
    c.setStrokeWidth(5);
    c.setFill(Color.rgb(12, 212, 252));
    c.setStroke(Color.rgb(12, 212, 252)); // #208036

    c.setOnMousePressed(
        event -> {
          state = EDITOR_STATE.EDITING;

          currentEditingNode = node.getNodeID();

          currentEditingLocations = dbConnection.getLocations(node.getNodeID(), today);
          currentEditingLocationIdx = 0;

          fillNodeFields(node);

          if (currentEditingLocations.size() > 1) {
            nextLocation.setVisible(true);
            prevLocation.setVisible(true);
          } else {
            nextLocation.setVisible(false);
            prevLocation.setVisible(false);
          }

          if (currentEditingLocations.size() > 0) {
            fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));
          } else {
            clearLocationFields();
          }
        });

    c.setOnMouseDragged(
        event -> {
          if (state == EDITOR_STATE.EDITING) {
            c.setCenterX(event.getX());
            c.setCenterY(event.getY());
            pane.setGestureEnabled(false);
          }
        });

    c.setOnMouseReleased(
        event -> {
          if (state == EDITOR_STATE.EDITING) {

            pane.setGestureEnabled(true);

            c.setCenterX(event.getX());
            c.setCenterY(event.getY());

            node.setPoint(new Point2D(event.getX(), event.getY()));
            node.setBuilding(
                buildingChecker.getBuilding(node.getPoint(), allFloors.get(currentFloor)));

            fillNodeFields(node);
            fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));

            // update node in database with updated x & y
            dbConnection.updateEntry(node);
          }
        });

    nodesGroup.getChildren().add(c);
  }

  /**
   * Fill in text fields for Nodes.
   *
   * @param node the Node whose data to fill in.
   */
  private void fillNodeFields(Node node) {

    xText.setText(Double.toString(node.getX()));
    yText.setText(Double.toString(node.getY()));
    floorText.setText(node.getFloor());
    buildingText.setText(node.getBuilding());
  }

  private void clearNodeFields() {
    xText.setText("");
    yText.setText("");
    floorText.setText("");
    buildingText.setText("");
  }

  private void fillLocationFields(Location loc) {

    shortnameText.setText(loc.getShortName());
    typeText.setText(loc.getNodeType().toString());
    longnameText.setText(loc.getLongName());
  }

  private void clearLocationFields() {

    shortnameText.setText("");
    typeText.setText("");
    longnameText.setText("");
  }

  private void deleteNode(int nodeID) {

    Iterator<javafx.scene.Node> itr = nodesGroup.getChildren().iterator();
    while (itr.hasNext()) {

      NodeCircle curr = (NodeCircle) itr.next();

      if (curr.getNodeID() == nodeID) {
        itr.remove();
        System.out.println("removed node" + nodeID);
      }
    }

    // remove from database
    dbConnection.removeEntry(nodeID, TableEntryType.NODE);

    currentEditingNode = -1;
    state = EDITOR_STATE.IDLE;
  }

  private void insertNode(double x, double y) {

    int id = dbConnection.getNextNodeID();

    String floor = allFloors.get(currentFloor);
    Node node =
        new Node(
            id, new Point2D(x, y), floor, buildingChecker.getBuilding(new Point2D(x, y), floor));

    drawNode(node);
    dbConnection.insertEntry(node);
  }
}

enum EDITOR_STATE {
  IDLE,
  ADDING,
  EDITING;
}
