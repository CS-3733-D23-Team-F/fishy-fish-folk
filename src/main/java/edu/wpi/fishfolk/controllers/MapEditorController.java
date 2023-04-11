package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.edit.InsertEdit;
import edu.wpi.fishfolk.database.edit.RemoveEdit;
import edu.wpi.fishfolk.database.edit.UpdateEdit;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.*;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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
  @FXML MFXButton closeServiceNav;
  @FXML MFXButton furnitureNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton conferenceNav;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton supplyNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton exitButton;
  @FXML AnchorPane slider;
  @FXML MFXButton serviceNav;

  private EDITOR_STATE state;
  private String currentEditingNode;
  private List<Location> currentEditingLocations;
  private int currentEditingLocationIdx;

  private Group nodesGroup;

  private int currentFloor = 2;
  private List<MicroNode> unodes;

  private BuildingRegion shapiroBuilding;

  public MapEditorController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    // mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    // officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    closeServiceNav.setVisible(false);
    closeServiceNav.setDisable(true);

    serviceNav.setOnMouseClicked(
        event -> {
          // menuWrap.setDisable(false);
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToY(490);
          slide.play();

          // slider.setTranslateY(-400);
          slider.setTranslateY(490);
          // menuWrap.setVisible(true);
          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(false);
                closeServiceNav.setVisible(true);
                serviceNav.setDisable(true);
                closeServiceNav.setDisable(false);
              });
        });

    closeServiceNav.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToY(0);
          slide.play();

          slider.setTranslateY(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(true);
                closeServiceNav.setVisible(false);
                serviceNav.setDisable(false);
                closeServiceNav.setDisable(true);
                serviceBar.setVisible(false);
                serviceBar.setDisable(true);
              });
        });

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

    pane.centreOn(new Point2D(1700, 1100));
    pane.zoomTo(0.4, new Point2D(2500, 1600));

    Polygon shapiroPoly = new Polygon();
    shapiroPoly
        .getPoints()
        .addAll(
            1774.133, 2266.667,
            1095.733, 2263.467,
            1081.333, 1839.467,
            1126.133, 1839.467,
            1129.333, 1799.467,
            1162.933, 1799.467,
            1162.933, 1748.267,
            1270.133, 1753.067,
            1271.733, 1769.067,
            1751.733, 1773.867,
            1751.733, 1799.467,
            1772.533, 1802.667);
    ArrayList<Polygon> shapiroPolyList = new ArrayList<Polygon>();
    shapiroPolyList.add(shapiroPoly);
    // shapiroBuilding = new BuildingRegion(shapiroPolyList, "Shapiro", "1");

    state = EDITOR_STATE.IDLE;
    currentEditingLocationIdx = 0;

    // prints mouse location to screen when clicked on map. Used to calculate building boundaries
    mapImg.setOnMouseClicked(
        event -> {
          System.out.println("X: " + event.getX() + " Y: " + event.getY());
          Point2D currPoint = new Point2D(event.getX(), event.getY());
          if (shapiroBuilding.isWithinRegion(currPoint, allFloors.get(currentFloor))) {
            System.out.println("I'm in Shapiro Building");
          } else {
            System.out.println("I'm outside");
          }

          if (state == EDITOR_STATE.ADDING) {
            System.out.println("adding at " + event.getX() + ", " + event.getY());
            insertNode(event.getX(), event.getY());
            state = EDITOR_STATE.IDLE;

          } else if (state == EDITOR_STATE.EDITING) {
            state = EDITOR_STATE.IDLE;
            currentEditingNode = "";
            clearMicroNodeFields();
            clearLocationFields();
          }
        });

    addNode.setOnMouseClicked(
        event -> {
          // go to adding state no matter the previous state
          state = EDITOR_STATE.ADDING;
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
        new MicroNode(Integer.parseInt(id), x, y, floor, BuildingRegion.getBuilding(x, y, floor));

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
