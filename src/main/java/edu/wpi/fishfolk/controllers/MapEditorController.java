package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.TableEntry.Edge;
import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.mapeditor.BuildingChecker;
import edu.wpi.fishfolk.mapeditor.EdgeLine;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import java.util.*;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;
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

  @FXML HBox buttonPane;
  @FXML VBox nodePane;

  @FXML MFXTextField xText;
  @FXML MFXTextField yText;
  @FXML MFXTextField buildingText;
  @FXML MFXButton addNode;
  @FXML MFXButton delNode;
  @FXML MFXTextField longnameText;
  @FXML MFXTextField shortnameText;
  @FXML MFXTextField typeText;
  @FXML MFXButton moveEditorNav;
  @FXML MFXButton addEdge;
  @FXML MFXButton delEdge;
  @FXML MFXToggleButton toggleAll;
  @FXML MFXToggleButton toggleSelected;

  @FXML MFXButton importCSV;
  @FXML MFXButton exportCSV;

  FileChooser fileChooser;
  DirectoryChooser dirChooser;

  private EDITOR_STATE state;
  private int currentEditingNode;
  private List<Location> currentEditingLocations;
  private int currentEditingLocationIdx;

  private Group nodesGroup, edgesGroup;
  private HashSet<Edge> edgeSet;
  private Edge currentEditingEdge;

  private int currentFloor = 2;

  private BuildingChecker buildingChecker;

  public MapEditorController() {
    super();
  }

  @FXML
  private void initialize() {

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(allFloors);
    Collections.reverse(floorsReverse);

    floorSelector.getItems().addAll(floorsReverse);

    nodesGroup = new Group();
    edgesGroup = new Group();
    drawGroup.getChildren().add(nodesGroup);
    drawGroup.getChildren().add(edgesGroup);

    edgeSet = new HashSet<>();

    toggleAll.setSelected(true);
    toggleSelected.setSelected(false);

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

    moveEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));

    state = EDITOR_STATE.IDLE;
    currentEditingNode = -1;
    currentEditingLocationIdx = 0;
    currentEditingEdge = null;

    buildingChecker = new BuildingChecker();

    pane.centreOn(new Point2D(1700, 1100));
    pane.zoomTo(0.4, new Point2D(2500, 1600));

    fileChooser = new FileChooser();
    dirChooser = new DirectoryChooser();

    // buttons and state switching

    mapImg.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.ADDING_NODE) {
            // System.out.println("adding at " + event.getX() + ", " + event.getY());
            insertNode(event.getX(), event.getY());
            delNode.setDisable(false);
            state = EDITOR_STATE.IDLE;

          } else if (state == EDITOR_STATE.EDITING_NODE) {
            state = EDITOR_STATE.IDLE;
            currentEditingNode = -1;
            resetNodes();
            clearNodeFields();
            clearLocationFields();
          }
        });

    addNode.setOnMouseClicked(
        event -> {
          // go to adding state no matter the previous state
          state = EDITOR_STATE.ADDING_NODE;
          // disable deleting when adding
          delNode.setDisable(true);
        });

    delNode.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {

            deleteNode(currentEditingNode);
          }
        });

    addEdge.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {
            state = EDITOR_STATE.ADDING_EDGE;
          }
          delEdge.setDisable(true);
        });

    delEdge.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_EDGE) {
            removeEdge(currentEditingEdge);
            currentEditingNode = -1;
            currentEditingEdge = null;
            state = EDITOR_STATE.IDLE;
          }
        });

    toggleAll.setOnAction(
        event -> {
          edgesGroup.getChildren().forEach(fxnode -> fxnode.setVisible(toggleAll.isSelected()));
        });

    toggleSelected.setOnAction(
        event -> {
          if (toggleSelected.isSelected() && state == EDITOR_STATE.EDITING_NODE) {

            // highlight neighbor nodes
            dbConnection
                .getNeighborIDs(currentEditingNode)
                .forEach(
                    nid -> {
                      nodesGroup
                          .getChildren()
                          .forEach(
                              fxnode -> {
                                NodeCircle nodeCircle = (NodeCircle) fxnode;
                                if (nodeCircle.getNodeID() == nid) {
                                  nodeCircle.highlight();
                                }
                              });
                    });

            // show edges
            edgesGroup
                .getChildren()
                .forEach(
                    fxnode -> {
                      EdgeLine edgeLine = (EdgeLine) fxnode;
                      if (edgeLine.containsNode(currentEditingNode)) {
                        fxnode.setVisible(true);
                      } else {
                        fxnode.setVisible(false);
                      }
                    });

          } else if (!toggleSelected.isSelected()) {
            nodesGroup
                .getChildren()
                .forEach(
                    fxnode -> {
                      NodeCircle nodeCircle = (NodeCircle) fxnode;
                      nodeCircle.reset();
                    });
            edgesGroup.getChildren().forEach(fxnode -> fxnode.setVisible(false));
          }
        });

    Fapp.getPrimaryStage()
        .getScene()
        .setOnKeyPressed(
            event -> {
              if (event.getCode() == KeyCode.DELETE && state == EDITOR_STATE.EDITING_NODE) {
                deleteNode(currentEditingNode);
              }
            });
  }

  private void switchFloor(String floor) {

    mapImg.setImage(images.get(floor));

    nodesGroup.getChildren().clear();
    dbConnection.getNodesOnFloor(floor).forEach(this::drawNode);

    edgesGroup.getChildren().clear();
    dbConnection.getEdgesOnFloor(floor).forEach(edge -> drawEdge(edge, toggleAll.isSelected()));
  }

  private void drawNode(Node node) {

    Point2D p = node.getPoint();
    NodeCircle c = new NodeCircle(node.getNodeID(), p.getX(), p.getY(), 6);
    c.setStrokeWidth(3);
    c.reset();

    c.setOnMousePressed(
        event -> {
          if (state == EDITOR_STATE.ADDING_EDGE) {
            insertEdge(currentEditingNode, node.getNodeID());
            delEdge.setDisable(false);
          }

          state = EDITOR_STATE.EDITING_NODE;

          currentEditingNode = node.getNodeID();

          c.highlight();

          currentEditingLocations = dbConnection.getLocations(node.getNodeID(), today);
          currentEditingLocationIdx = 0;

          fillNodeFields(node);

          if (currentEditingLocations.size() > 0) {
            fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));
          } else {
            clearLocationFields();
          }
        });

    c.setOnMouseDragged(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {
            c.setCenterX(event.getX());
            c.setCenterY(event.getY());
            pane.setGestureEnabled(false);
          }
        });

    c.setOnMouseReleased(
        event -> {
          // enable gesture pane outside if in case the state changed while dragging
          pane.setGestureEnabled(true);
          if (state == EDITOR_STATE.EDITING_NODE) {

            c.setCenterX(event.getX());
            c.setCenterY(event.getY());

            node.setPoint(new Point2D(event.getX(), event.getY()));
            node.setBuilding(
                buildingChecker.getBuilding(node.getPoint(), allFloors.get(currentFloor)));

            fillNodeFields(node);

            if (currentEditingLocations.size() > 0) {
              fillLocationFields(currentEditingLocations.get(currentEditingLocationIdx));
            } else {
              clearLocationFields();
            }

            // update node in database with updated x & y
            dbConnection.updateEntry(node);

            // redraw edges associated with this node

          }
        });

    nodesGroup.getChildren().add(c);
  }

  private boolean drawEdge(Edge edge, boolean visibility) {

    if (edgeSet.contains(edge)) {
      return false;
    }

    edgeSet.add(edge);

    Point2D p1 =
        ((Node) dbConnection.getEntry(edge.getStartNode(), TableEntryType.NODE)).getPoint();
    Point2D p2 = ((Node) dbConnection.getEntry(edge.getEndNode(), TableEntryType.NODE)).getPoint();

    EdgeLine line =
        new EdgeLine(
            edge.getStartNode(), edge.getEndNode(), p1.getX(), p1.getY(), p2.getX(), p2.getY());

    line.getStrokeDashArray().addAll(5.0, 7.5);
    line.setStrokeWidth(1.5);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.reset();

    line.setOnMousePressed(
        event -> {
          currentEditingEdge = edge;
          line.highlight();
          state = EDITOR_STATE.EDITING_EDGE;
        });

    line.setVisible(visibility);

    edgesGroup.getChildren().add(line);
    return true;
  }

  private void resetNodes() {
    nodesGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((NodeCircle) fxnode).reset();
            });
  }

  /**
   * Fill in text fields for Nodes.
   *
   * @param node the Node whose data to fill in.
   */
  private void fillNodeFields(Node node) {

    xText.setText(Double.toString(node.getX()));
    yText.setText(Double.toString(node.getY()));
    buildingText.setText(node.getBuilding());
  }

  private void clearNodeFields() {
    xText.setText("");
    yText.setText("");
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

  private void insertEdge(int start, int end) {
    Edge newEdge = new Edge(start, end);
    dbConnection.insertEntry(newEdge);
    drawEdge(newEdge, true);
  }

  private void removeEdge(Edge edge) {
    edgeSet.remove(edge);

    Iterator<javafx.scene.Node> itr = edgesGroup.getChildren().iterator();
    while (itr.hasNext()) {
      if (((EdgeLine) itr.next()).matches(edge)) itr.remove();
    }

    dbConnection.removeEntry(edge, TableEntryType.EDGE);
  }
}

enum EDITOR_STATE {
  IDLE,
  ADDING_NODE,
  EDITING_NODE,
  ADDING_EDGE,
  EDITING_EDGE;
}
