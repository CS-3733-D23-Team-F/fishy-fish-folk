package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.edit.InsertEdit;
import edu.wpi.fishfolk.database.edit.RemoveEdit;
import edu.wpi.fishfolk.database.edit.UpdateEdit;
import edu.wpi.fishfolk.mapeditor.BuildingChecker;
import edu.wpi.fishfolk.mapeditor.CircleNode;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.*;
import java.util.List;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
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
  @FXML MFXButton addNode;
  @FXML MFXButton delNode;
  @FXML MFXTextField longnameText;
  @FXML MFXTextField shortnameText;
  @FXML MFXTextField typeText;
  @FXML MFXButton nextLocation;
  @FXML MFXButton prevLocation;
  @FXML MFXButton addEdge;
  @FXML MFXButton delEdge;
  @FXML MFXButton importCSV;
  @FXML MFXButton exportCSV;
  @FXML
  HBox buttonPane;
  @FXML
    VBox nodePane;
  @FXML
    MFXToggleButton toggleAll;
  @FXML MFXToggleButton toggleSelected;


  FileChooser fileChooser;
  DirectoryChooser dirChooser;

  private EDITOR_STATE state;
  private String currentEditingNode;
  private List<Location> currentEditingLocations;
  private int currentEditingLocationIdx;

  private Group nodesGroup;

  private int currentFloor = 2;
  private List<MicroNode> unodes;

  private BuildingChecker buildingChecker;

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
            currentEditingNode = "";
            clearMicroNodeFields();
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

    unodes =
        dbConnection.micronodeTable.executeQuery("SELECT *", "WHERE floor = '" + floor + "'")
            .stream()
            .map(
                elt -> {
                  MicroNode un = new MicroNode();
                  un.construct(new ArrayList(List.<String>of(elt)));
                  return un;
                })
            .toList();

    // System.out.println(unodes.size());

    unodes.forEach(this::drawNode);
  }

  // this also does some initialization now
  private void drawNode(MicroNode unode) {

    Point2D p = unode.point;
    CircleNode c = new CircleNode(unode.id, p.getX(), p.getY(), 4);
    c.setStrokeWidth(5);
    c.setFill(Color.rgb(12, 212, 252));
    c.setStroke(Color.rgb(12, 212, 252)); // #208036

    c.setOnMousePressed(
        event -> {
          state = EDITOR_STATE.EDITING;
          currentEditingNode = unode.id;
          currentEditingLocations =
              dbConnection.getMostRecentLocations(unode.id).stream()
                  .map(
                      elt -> {
                        Location loc = new Location();
                        loc.construct(new ArrayList<>(Arrays.asList(elt)));
                        return loc;
                      })
                  .toList();

          currentEditingLocationIdx = 0;

          fillMicroNodeFields(unode);

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

            unode.point = new Point2D(event.getX(), event.getY());
            unode.building = buildingChecker.getBuilding(unode.point, allFloors.get(currentFloor));

            fillMicroNodeFields(unode);
            fillLocationFields(unode.getLocations().get(currentEditingLocationIdx));

            // process edits for both x and y
            dbConnection.processEdit(
                new UpdateEdit(
                    DataTableType.MICRONODE,
                    "id",
                    unode.id,
                    "x",
                    Double.toString(unode.point.getX())));

            dbConnection.processEdit(
                new UpdateEdit(
                    DataTableType.MICRONODE,
                    "id",
                    unode.id,
                    "y",
                    Double.toString(unode.point.getY())));
          }
        });

    nodesGroup.getChildren().add(c);
  }

  /**
   * Fill in text fields for MicroNodes.
   *
   * @param unode the MicroNode whose data to fill in.
   */
  private void fillMicroNodeFields(MicroNode unode) {

    xText.setText(Double.toString(unode.point.getX()));
    yText.setText(Double.toString(unode.point.getY()));
    floorText.setText(unode.floor);
    buildingText.setText(unode.building);
  }

  private void clearMicroNodeFields() {
    xText.setText("");
    yText.setText("");
    floorText.setText("");
    buildingText.setText("");
  }

  private void fillLocationFields(Location loc) {

    shortnameText.setText(loc.shortname);
    typeText.setText(loc.type.toString());
    longnameText.setText(loc.longname);
  }

  private void clearLocationFields() {

    shortnameText.setText("");
    typeText.setText("");
    longnameText.setText("");
  }

  private void deleteNode(String id) {

    Iterator<Node> itr = nodesGroup.getChildren().iterator();
    while (itr.hasNext()) {

      CircleNode curr = (CircleNode) itr.next();

      if (curr.getCircleNodeID().equals(id)) {
        itr.remove();
        System.out.println("removed node" + curr.getCircleNodeID());
      }
    }

    dbConnection.processEdit(new RemoveEdit(DataTableType.MICRONODE, "id", id));

    currentEditingNode = "";
    state = EDITOR_STATE.IDLE;
  }

  private void insertNode(double x, double y) {

    String id = dbConnection.getNextID();

    String floor = allFloors.get(currentFloor);
    MicroNode unode =
        new MicroNode(
            Integer.parseInt(id),
            x,
            y,
            floor,
            buildingChecker.getBuilding(new Point2D(x, y), floor));

    drawNode(unode);
    // fillMicroNodeFields();

    dbConnection.processEdit(
        new InsertEdit(DataTableType.MICRONODE, "id", id, unode.deconstruct()));
  }
}

enum EDITOR_STATE {
  IDLE,
  ADDING,
  EDITING;
}
