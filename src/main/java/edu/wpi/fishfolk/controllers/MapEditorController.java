package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.mapeditor.BuildingChecker;
import edu.wpi.fishfolk.mapeditor.EdgeLine;
import edu.wpi.fishfolk.mapeditor.NodeCircle;
import edu.wpi.fishfolk.mapeditor.NodeText;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.util.NodeType;
import io.github.palexdev.materialfx.controls.*;
import java.io.IOException;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.kurobako.gesturefx.GesturePane;

public class MapEditorController extends AbsController {

  @FXML MFXComboBox<String> floorSelector;
  @FXML ImageView mapImg;
  @FXML GesturePane gesturePane;
  @FXML public Group drawGroup;
  @FXML MFXButton nextButton;
  @FXML MFXButton backButton;

  // @FXML HBox buttonPane;

  @FXML MFXButton moveEditorNav;
  @FXML MFXButton importCSV, exportCSV;
  @FXML MFXButton addNode, delNode;
  @FXML MFXRadioButton radioAllEdge, radioSelectedEdge, radioNoEdge;
  @FXML MFXButton addEdge, delEdge;
  @FXML MFXButton linearAlign, snapGrid;
  @FXML MFXToggleButton toggleLocations;

  @FXML MFXTextField nodeidText, xText, yText, buildingText;

  @FXML MFXScrollPane locationScrollpane;
  @FXML VBox locationsVbox;

  @FXML VBox newLocationVbox;
  @FXML MFXTextField newLocationLongname, newLocationShortname;
  @FXML MFXComboBox<String> newLocationType;
  @FXML MFXDatePicker newLocationDate;
  @FXML MFXButton newLocationSubmit;

  FileChooser fileChooser = new FileChooser();
  DirectoryChooser dirChooser = new DirectoryChooser();

  private Group nodeGroup, edgeGroup, locationGroup;

  private HashSet<Edge> edgeSet = new HashSet<>();

  // lists preserve order for easy access to first and last
  private LinkedList<Integer> selectedNodes = new LinkedList<>();
  private LinkedList<Edge> selectedEdges = new LinkedList<>();

  private LinkedList<Location> currentLocations = new LinkedList<>();

  private BuildingChecker buildingChecker = new BuildingChecker();

  private EDITOR_STATE state = EDITOR_STATE.IDLE;
  private int currentFloor = 2;
  private boolean controlPressed = false;

  public MapEditorController() {
    super();
  }

  @FXML
  private void initialize() {

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(allFloors);
    Collections.reverse(floorsReverse);

    floorSelector.getItems().addAll(floorsReverse);

    nodeGroup = new Group();
    edgeGroup = new Group();
    locationGroup = new Group();
    drawGroup.getChildren().addAll(nodeGroup, edgeGroup, locationGroup);

    // other buttons in radio group are deselected by default
    radioNoEdge.setSelected(true);

    toggleLocations.setSelected(false);

    // draw everything on current floor
    switchFloor(allFloors.get(currentFloor));

    // ensure the vbox holding the new location & date fields is managed only when visible
    newLocationVbox.managedProperty().bind(newLocationVbox.visibleProperty());

    floorSelector.setOnAction(
        event -> {
          currentFloor = allFloors.indexOf(floorSelector.getValue());
          switchFloor(allFloors.get(currentFloor));
          floorSelector.setText(allFloors.get(currentFloor));
        });
    nextButton.setOnMouseClicked(
        event -> {
          if (currentFloor < allFloors.size() - 1) {
            currentFloor++;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText(allFloors.get(currentFloor));
          }
        });
    backButton.setOnMouseClicked(
        event -> {
          if (currentFloor > 0) {
            currentFloor--;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText(allFloors.get(currentFloor));
          }
        });

    moveEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));

    gesturePane.centreOn(new Point2D(1700, 1100));
    gesturePane.zoomTo(0.4, new Point2D(2500, 1600));

    // buttons and state switching

    mapImg.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.ADDING_NODE) {
            // System.out.println("adding at " + event.getX() + ", " + event.getY());
            insertNode(event.getX(), event.getY());
            delNode.setDisable(false);
            // TODO decide if previous or new node is selected - currently only previous
            state = EDITOR_STATE.EDITING_NODE;

          } else if (state == EDITOR_STATE.EDITING_NODE || state == EDITOR_STATE.EDITING_EDGE) {

            state = EDITOR_STATE.IDLE;

            // clear node stuff
            deselectAllNodes();
            clearNodeFields();
            clearLocationFields();

            radioSelectedEdge.setDisable(true);

            // clear & hide edge stuff
            deselectAllEdges();
            edgeGroup.getChildren().forEach(fxnode -> fxnode.setVisible(radioAllEdge.isSelected()));
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
            removeSelectedNodes();
            state = EDITOR_STATE.IDLE;
          }
        });

    addEdge.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {

            // if multiple nodes selected, draw all edges between them
            int n = selectedNodes.size();
            if (n > 1) {

              for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                  insertEdge(selectedNodes.get(i), selectedNodes.get(j));
                }
              }
              // stay in editing nodes state

              // ensure delete edge button is available
              delEdge.setDisable(false);

            } else {
              // just one node selected, allow user to choose second node of edge
              state = EDITOR_STATE.ADDING_EDGE;
              delEdge.setDisable(true);
            }
          }
        });

    delEdge.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_EDGE) {
            removeSelectedEdges();
            state = EDITOR_STATE.IDLE;
          }
        });

    radioAllEdge.setOnAction(
        event -> {
          if (radioAllEdge.isSelected()) {

            edgeGroup.getChildren().forEach(fxnode -> fxnode.setVisible(true));

          } else {
            edgeGroup.getChildren().forEach(fxnode -> fxnode.setVisible(false));
          }
        });

    radioSelectedEdge.setOnAction(
        event -> {
          if (radioSelectedEdge.isSelected()) { // && state == EDITOR_STATE.EDITING_NODE) {

            // show edges that connect to at least one of the selected nodes
            edgeGroup
                .getChildren()
                .forEach(
                    fxnode -> {
                      EdgeLine edgeLine = (EdgeLine) fxnode;
                      fxnode.setVisible(edgeLine.containsNodes(selectedNodes));
                    });

          } else {

            // hide all edges
            edgeGroup.getChildren().forEach(fxnode -> fxnode.setVisible(false));
          }
        });

    radioNoEdge.setOnAction(
        event -> {
          if (radioNoEdge.isSelected()) {
            // hide all edges
            edgeGroup.getChildren().forEach(fxnode -> fxnode.setVisible(false));
          }
        });

    toggleLocations.setOnAction(
        event -> {
          locationGroup.setVisible(toggleLocations.isSelected());
        });

    Fapp.getPrimaryStage()
        .getScene()
        .setOnKeyPressed(
            event -> {
              System.out.println("key pressed: " + event.getCode());
              if (event.getCode() == KeyCode.DELETE) {

                removeSelectedNodes();
                removeSelectedEdges();

                // in case a node is deleted while dragging (gestures are disabled when dragging)
                gesturePane.setGestureEnabled(true);
                state = EDITOR_STATE.IDLE;

              } else if (event.getCode() == KeyCode.CONTROL) {
                controlPressed = true;

              } else if (event.getCode() == KeyCode.A) {
                moveSelectedNodes(2, MOVE_DIRECTION.LEFT);
              } else if (event.getCode() == KeyCode.W) {
                moveSelectedNodes(2, MOVE_DIRECTION.UP);
              } else if (event.getCode() == KeyCode.D) {
                moveSelectedNodes(2, MOVE_DIRECTION.RIGHT);
              } else if (event.getCode() == KeyCode.S) {
                moveSelectedNodes(2, MOVE_DIRECTION.DOWN);
              }
            });

    Fapp.getPrimaryStage()
        .getScene()
        .setOnKeyReleased(
            event -> {
              if (event.getCode() == KeyCode.CONTROL) {
                controlPressed = false;

              } else if (event.getCode() == KeyCode.A
                  || event.getCode() == KeyCode.W
                  || event.getCode() == KeyCode.D
                  || event.getCode() == KeyCode.S) {

                // for each drawn node, if its selected update it in the db
                nodeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          NodeCircle nodeCircle = (NodeCircle) fxnode;
                          if (selectedNodes.contains(nodeCircle.getNodeID())) {
                            Node newNode =
                                (Node)
                                    dbConnection.getEntry(
                                        nodeCircle.getNodeID(), TableEntryType.NODE);
                            newNode.setPoint(
                                new Point2D(nodeCircle.getCenterX(), nodeCircle.getCenterY()));
                            dbConnection.updateEntry(newNode);
                          }
                        });

                // update edgelines containing the moved nodes
                edgeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          EdgeLine edgeLine = (EdgeLine) fxnode;
                          selectedNodes.forEach(
                              nodeID -> {
                                if (edgeLine.containsNode(nodeID)) {
                                  edgeLine.updateEndpoint(
                                      (Node) dbConnection.getEntry(nodeID, TableEntryType.NODE));
                                }
                              });
                        });
              }
            });

    importCSV.setOnAction(
        event -> {
          fileChooser.setTitle("Select the Node CSV file");
          String nodePath = fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Location CSV file");
          String locationPath =
              fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Move CSV file");
          String movePath = fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Edge CSV file");
          String edgePath = fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          dbConnection.importCSV(nodePath, false, TableEntryType.NODE);
          dbConnection.importCSV(locationPath, false, TableEntryType.LOCATION);
          dbConnection.importCSV(movePath, false, TableEntryType.MOVE);
          dbConnection.importCSV(edgePath, false, TableEntryType.EDGE);

          initialize();
        });

    exportCSV.setOnAction(
        event -> {
          dirChooser.setTitle("Select Export Directory");
          String exportPath = dirChooser.showDialog(Fapp.getPrimaryStage()).getAbsolutePath();
          dbConnection.exportCSV(exportPath, TableEntryType.NODE);
          dbConnection.exportCSV(exportPath, TableEntryType.LOCATION);
          dbConnection.exportCSV(exportPath, TableEntryType.MOVE);
          dbConnection.exportCSV(exportPath, TableEntryType.EDGE);
          // fileChooser.setInitialDirectory(new File(exportPath));
        });

    linearAlign.setOnAction(
        event -> {
          // find the line of best fit through the selected points
          // project each point to the line
          // https://online.stat.psu.edu/stat501/lesson/1/1.2

          double xbar = 0, ybar = 0;

          List<Node> nodes = new LinkedList<>();

          selectedNodes.forEach(
              nodeID -> nodes.add((Node) dbConnection.getEntry(nodeID, TableEntryType.NODE)));

          for (Node node : nodes) {
            xbar += node.getX();
            ybar += node.getY();
          }

          xbar /= nodes.size();
          ybar /= nodes.size();

          double numerator = 0, denominator = 0;
          for (Node node : nodes) {
            numerator += (node.getX() - xbar) * (node.getY() - ybar);
            denominator += Math.pow(node.getX() - xbar, 2);
          }

          // if the line of best fit is essentially vertical, align each point to that line
          if (Math.abs(denominator) < 1) {

            double x = (nodes.get(0).getX() + nodes.get(nodes.size() - 1).getX()) / 2;
            nodes.forEach(node -> node.setPoint(new Point2D(x, node.getY())));

          } else {

            // equation of line in y = mx + b form
            double m = numerator / denominator;
            double b = ybar - m * xbar;

            // through origin with same slope as line
            Point2D line = new Point2D(500, 500 * m);

            double line2 = line.dotProduct(line);
            nodes.forEach(
                node -> {
                  Point2D v = node.getPoint().subtract(0, b);
                  Point2D proj = line.multiply(v.dotProduct(line) / line2);
                  node.setPoint(proj.add(0, b));
                });

            nodes.forEach(
                node -> {
                  // draw NodeCircle at updated node points
                  nodeGroup
                      .getChildren()
                      .forEach(
                          fxnode -> {
                            NodeCircle nodeCircle = (NodeCircle) fxnode;
                            if (nodeCircle.getNodeID() == node.getNodeID()) {
                              nodeCircle.setCenterX(node.getX());
                              nodeCircle.setPrevX(node.getX());
                              nodeCircle.setCenterY(node.getY());
                              nodeCircle.setPrevY(node.getY());
                            }
                          });

                  // update db
                  dbConnection.updateEntry(node);
                });
          }
        });

    snapGrid.setOnAction(
        event -> {
          List<Node> nodes = new LinkedList<>();

          selectedNodes.forEach(
              nodeID -> nodes.add((Node) dbConnection.getEntry(nodeID, TableEntryType.NODE)));

          nodes.forEach(
              node -> {
                node.snapToGrid(2.5);

                // draw NodeCircle at updated node points
                nodeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          NodeCircle nodeCircle = (NodeCircle) fxnode;
                          if (nodeCircle.getNodeID() == node.getNodeID()) {
                            nodeCircle.setCenterX(node.getX());
                            nodeCircle.setPrevX(node.getX());
                            nodeCircle.setCenterY(node.getY());
                            nodeCircle.setPrevY(node.getY());
                          }
                        });

                // update db
                dbConnection.updateEntry(node);
              });
        });

    newLocationSubmit.setOnAction(
        event -> {
          Location newLocation =
              new Location(
                  newLocationLongname.getText(),
                  newLocationShortname.getText(),
                  NodeType.valueOf(newLocationType.getValue()));
          dbConnection.insertEntry(newLocation);

          Move move =
              new Move(selectedNodes.get(0), newLocation.getLongName(), newLocationDate.getValue());
          dbConnection.insertEntry(move);
        });
  }

  private void switchFloor(String floor) {

    mapImg.setImage(images.get(floor));

    /* TODO optional
       one thread does nodes,
       one thread does edges,
       another thread does locations
    */

    nodeGroup.getChildren().clear();
    dbConnection.getNodesOnFloor(floor).forEach(this::drawNode);
    currentLocations.clear();
    deselectAllNodes();

    radioSelectedEdge.setDisable(true);

    edgeGroup.getChildren().clear();
    edgeSet.clear();
    dbConnection.getEdgesOnFloor(floor).forEach(edge -> drawEdge(edge, radioAllEdge.isSelected()));
    deselectAllEdges();

    locationGroup.getChildren().clear();
    drawLocations(floor, toggleLocations.isSelected());
  }

  private void drawNode(Node node) {

    Point2D p = node.getPoint();
    NodeCircle nodeCircle = new NodeCircle(node.getNodeID(), p.getX(), p.getY(), 6);
    // set default visual characteristics
    nodeCircle.reset();

    nodeCircle.setOnMousePressed(
        event -> {
          if (state == EDITOR_STATE.ADDING_EDGE) {
            insertEdge(selectedNodes.getLast(), node.getNodeID());
            insertEdge(selectedNodes.getLast(), node.getNodeID());
            delEdge.setDisable(false);
          }

          state = EDITOR_STATE.EDITING_NODE;

          if (!controlPressed) deselectAllNodes();
          selectNode(node.getNodeID());

          radioSelectedEdge.setDisable(false);

          // at least one node selected so visible and disabled if > 1 nodes selected
          newLocationVbox.setVisible(true);
          newLocationVbox.setDisable(selectedNodes.size() > 1);

          currentLocations.addAll(dbConnection.getLocations(node.getNodeID(), today));

          fillNodeFields(node);
          fillLocationFields();
        });

    nodeCircle.setOnMouseDragged(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {

            // TODO drag all selected nodes the same amount
            gesturePane.setGestureEnabled(false);

            nodeCircle.setCenterX(event.getX());
            nodeCircle.setCenterY(event.getY());

            // TODO redraw edges connected to this node during drag
          }
        });

    nodeCircle.setOnMouseReleased(
        event -> {
          // enable gesture pane outside if in case the state changed while dragging
          gesturePane.setGestureEnabled(true);
          if (state == EDITOR_STATE.EDITING_NODE) {

            // TODO: update all selected nodes

            // only move node if dragged more than 5 pixels
            double dist =
                Math.sqrt(
                    Math.pow((nodeCircle.getCenterX() - nodeCircle.getPrevX()), 2)
                        + Math.pow((nodeCircle.getCenterY() - nodeCircle.getPrevY()), 2));
            if (dist > 25) {

              nodeCircle.setCenterX(event.getX());
              nodeCircle.setPrevX(event.getX());

              nodeCircle.setCenterY(event.getY());
              nodeCircle.setPrevY((event.getY()));

              node.setPoint(new Point2D(event.getX(), event.getY()));
              node.setBuilding(
                  buildingChecker.getBuilding(node.getPoint(), allFloors.get(currentFloor)));

              fillNodeFields(node);

              // update edgelines containing this node
              edgeGroup
                  .getChildren()
                  .forEach(
                      fxnode -> {
                        EdgeLine edgeLine = (EdgeLine) fxnode;
                        if (edgeLine.containsNode(node.getNodeID())) {
                          edgeLine.updateEndpoint(node);
                        }
                      });

              // update node in database with updated x & y
              dbConnection.updateEntry(node);

            } else {
              nodeCircle.setCenterX(nodeCircle.getPrevX());
              nodeCircle.setCenterY(nodeCircle.getPrevY());
            }
          }
        });

    nodeGroup.getChildren().add(nodeCircle);
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

    line.reset();

    line.setOnMousePressed(
        event -> {
          if (!controlPressed) deselectAllEdges();
          selectEdge(edge);
          state = EDITOR_STATE.EDITING_EDGE;
        });

    line.setVisible(visibility);

    edgeGroup.getChildren().add(line);
    return true;
  }

  private void drawLocations(String floor, boolean visibility) {

    dbConnection
        .getNodesOnFloor(floor)
        .forEach(
            node -> {
              List<String> shortnames =
                  dbConnection.getLocations(node.getNodeID(), today).stream()
                      .filter(Location::isDestination)
                      .map(Location::getShortName)
                      .toList();

              if (!shortnames.isEmpty()) {
                String label = String.join(", ", shortnames);
                locationGroup
                    .getChildren()
                    .add(
                        new NodeText(
                            node.getNodeID(),
                            node.getX() - label.length() * 5,
                            node.getY() - 10,
                            label));
              }
            });

    locationGroup.setVisible(visibility);
  }

  private void selectNode(int nodeID) {
    selectedNodes.add(nodeID);
    nodeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((NodeCircle) fxnode).highlightIf(nodeID);
            });
  }

  private void deselectNode(int nodeID) {
    selectedNodes.remove(Integer.valueOf(nodeID));
    nodeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((NodeCircle) fxnode).resetIf(nodeID);
            });
  }

  private void deselectAllNodes() {

    // only allow adding locations to nodes if some are selected
    newLocationVbox.setVisible(false);
    newLocationVbox.setDisable(true);

    selectedNodes.clear();
    nodeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((NodeCircle) fxnode).reset();
            });
  }

  private void selectEdge(Edge edge) {
    selectedEdges.add(edge);
    edgeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((EdgeLine) fxnode).highlightIf(edge);
            });
  }

  private void deselectEdge(Edge edge) {
    selectedEdges.remove(edge);
    edgeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((EdgeLine) fxnode).resetIf(edge);
            });
  }

  private void deselectAllEdges() {
    selectedEdges.clear();
    edgeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              ((EdgeLine) fxnode).reset();
            });
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

  private void removeNode(int nodeID) {

    Iterator<javafx.scene.Node> itr = nodeGroup.getChildren().iterator();
    while (itr.hasNext()) {

      NodeCircle curr = (NodeCircle) itr.next();

      if (curr.getNodeID() == nodeID) {
        itr.remove();
        System.out.println("removed node" + nodeID);
      }
    }

    // remove from database
    dbConnection.removeEntry(nodeID, TableEntryType.NODE);
    // must be Integer to remove object since int removes that index
    selectedNodes.remove(Integer.valueOf(nodeID));
  }

  private void removeSelectedNodes() {

    Iterator<Integer> selectedItr = selectedNodes.iterator();
    while (selectedItr.hasNext()) {
      int nodeID = selectedItr.next();

      // remove corresponding nodecircle from group
      Iterator<javafx.scene.Node> groupItr = nodeGroup.getChildren().iterator();
      while (groupItr.hasNext()) {
        NodeCircle curr = (NodeCircle) groupItr.next();

        if (curr.getNodeID() == nodeID) {
          groupItr.remove();
        }
      }

      // remove edges containing this node
      Iterator<Edge> edgeItr = edgeSet.iterator();
      while (edgeItr.hasNext()) {
        Edge curr = edgeItr.next();
        if (curr.containsNode(nodeID)) {
          dbConnection.removeEntry(curr, TableEntryType.EDGE);
          edgeItr.remove();
        }
      }

      Iterator<javafx.scene.Node> edgeGroupItr = edgeGroup.getChildren().iterator();
      while (edgeGroupItr.hasNext()) {
        EdgeLine curr = (EdgeLine) edgeGroupItr.next();

        if (curr.containsNode(nodeID)) {
          edgeGroupItr.remove();
        }
      }

      // remove from database
      dbConnection.removeEntry(nodeID, TableEntryType.NODE);

      // remove from selected nodes
      selectedItr.remove();
    }

    clearNodeFields();
    clearLocationFields();
  }

  private void insertEdge(int start, int end) {
    Edge newEdge = new Edge(start, end);
    dbConnection.insertEntry(newEdge);
    drawEdge(newEdge, true);
  }

  private void removeEdge(Edge edge) {
    edgeSet.remove(edge);

    edgeGroup.getChildren().removeIf(node -> ((EdgeLine) node).matches(edge));

    dbConnection.removeEntry(edge, TableEntryType.EDGE);
  }

  private void removeSelectedEdges() {
    Iterator<Edge> selectedItr = selectedEdges.iterator();
    while (selectedItr.hasNext()) {
      Edge edge = selectedItr.next();

      // remove corresponding edgeline from group
      Iterator<javafx.scene.Node> groupItr = edgeGroup.getChildren().iterator();
      while (groupItr.hasNext()) {
        EdgeLine curr = (EdgeLine) groupItr.next();

        if (curr.matches(edge)) {
          groupItr.remove();
        }
      }

      // remove from database
      dbConnection.removeEntry(edge, TableEntryType.EDGE);

      // remove from selected edges
      selectedItr.remove();
    }
  }

  private void moveSelectedNodes(double dist, MOVE_DIRECTION dir) {

    nodeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              NodeCircle nodeCircle = (NodeCircle) fxnode;
              if (selectedNodes.contains(nodeCircle.getNodeID())) {

                switch (dir) {
                  case LEFT:
                    nodeCircle.setCenterX(nodeCircle.getCenterX() - dist);
                    nodeCircle.setPrevX(nodeCircle.getCenterX());
                    break;

                  case UP:
                    nodeCircle.setCenterY(nodeCircle.getCenterY() - dist);
                    nodeCircle.setPrevY(nodeCircle.getCenterY());
                    break;

                  case RIGHT:
                    nodeCircle.setCenterX(nodeCircle.getCenterX() + dist);
                    nodeCircle.setPrevX(nodeCircle.getCenterX());
                    break;

                  case DOWN:
                    nodeCircle.setCenterY(nodeCircle.getCenterY() + dist);
                    nodeCircle.setPrevY(nodeCircle.getCenterY());
                    break;
                }
              }
            });
  }

  private void updateEdges() {}

  /**
   * Fill in text fields for Nodes.
   *
   * @param node the Node whose data to fill in.
   */
  private void fillNodeFields(Node node) {

    nodeidText.setText(Integer.toString(node.getNodeID()));
    xText.setText(String.format("%.1f", node.getX()));
    yText.setText(String.format("%.1f", node.getY()));
    buildingText.setText(node.getBuilding());
  }

  private void clearNodeFields() {
    nodeidText.setText("");
    xText.setText("");
    yText.setText("");
    buildingText.setText("");
  }

  /** Display current locations in info pane on UI. */
  private void fillLocationFields() {

    if (currentLocations.isEmpty()) {
      clearLocationFields();

    } else {
      locationScrollpane.setVisible(true);
      locationScrollpane.setDisable(false);
      locationScrollpane.setMaxHeight(300);
    }

    try {

      for (Location location : currentLocations) {

        System.out.println(location.toString());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/MapEditorLocation.fxml"));

        VBox entry = fxmlLoader.load();

        MapEditorLocationController controller = fxmlLoader.getController();
        controller.setData(location);

        locationsVbox.getChildren().add(entry);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void clearLocationFields() {

    currentLocations.clear();

    locationScrollpane.setVisible(false);
    locationScrollpane.setDisable(true);
    locationScrollpane.setMaxHeight(0);

    locationsVbox.getChildren().clear();
  }
}

enum EDITOR_STATE {
  IDLE,

  ADDING_NODE,
  EDITING_NODE,

  ADDING_EDGE,
  EDITING_EDGE;
}

enum MOVE_DIRECTION {
  LEFT,
  UP,
  RIGHT,
  DOWN;
}
