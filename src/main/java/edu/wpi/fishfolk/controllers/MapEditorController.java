package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.DataEditQueue;
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
import java.time.LocalDate;
import java.util.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PopOver;

public class MapEditorController extends AbsController {

  @FXML MFXComboBox<String> floorSelector;
  @FXML ImageView mapImg;
  @FXML GesturePane gesturePane;
  @FXML public Group drawGroup;
  @FXML MFXButton nextButton;
  @FXML MFXButton backButton;

  @FXML MFXButton moveEditorNav, save, undo;
  @FXML MFXButton importCSV, exportCSV;
  @FXML MFXButton addNode, delNode;
  @FXML MFXRadioButton radioAllEdge, radioSelectedEdge, radioNoEdge;
  @FXML MFXButton addEdge, delEdge;
  @FXML MFXButton linearAlign, snapGrid;

  @FXML MFXFilterComboBox<String> locationSearch;
  @FXML CheckComboBox<NodeType> showLocationType;
  @FXML MFXButton clearLocationType;

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

  private Group nodeGroup, edgeGroup, locationGroup, labelPreviewGroup;

  // observable lists of map objects. listeners automatically update the ui
  // the new Observable[] {} syntax is called an extractor and allows listeners to receive update
  // calls

  private ObservableList<Node> nodes =
      FXCollections.observableArrayList(
          node -> new Observable[] {node.getNodeProperty(), node.getPointProperty()});
  // easy access to a specific node via its id
  private static HashMap<Integer, Integer> nodeID2idx = new HashMap<>();
  private HashMap<Integer, Node> removedNodes = new HashMap<>();

  private ObservableList<Edge> edges =
      FXCollections.observableArrayList(edge -> new Observable[] {edge.getEdgeProperty()});

  private ObservableList<Location> locations =
      FXCollections.observableArrayList(loc -> new Observable[] {loc.getLocationProperty()});
  // easy access to a specific location via its longname
  private HashMap<String, Integer> longname2idx = new HashMap<>();

  private ObservableList<Move> moves =
      FXCollections.observableArrayList(move -> new Observable[] {move.getMoveProperty()});

  // different listeners than the large lists above containing the entire map
  private ObservableList<Integer> selectedNodes = FXCollections.observableArrayList();
  private ObservableList<Edge> selectedEdges = FXCollections.observableArrayList();

  // cache ids of nodes on the selected floor
  private HashSet<Integer> nodesOnFloor = new HashSet<>();

  private HashMap<NodeType, Group> locationTypeGroups;
  private HashSet<NodeType> visibleNodeTypes = new HashSet<>();
  private final List<NodeType> observableNodeTypes =
      FXCollections.observableList(dbConnection.getNodeTypes());

  private final BuildingChecker buildingChecker = new BuildingChecker();

  private EDITOR_STATE state = EDITOR_STATE.IDLE;
  private int currentFloor = 2;
  private boolean controlPressed = false;

  private final MapEditQueue<Object> editQueue = new MapEditQueue<>();

  public MapEditorController() {
    super();
  }

  public static boolean validateNodeID(int nodeID) {
    return nodeID2idx.containsKey(nodeID);
  }

  public static boolean validateDate(LocalDate date) {
    // equivalent to matches today or is after today
    return !date.isBefore(today);
  }

  @FXML
  private void initialize() {

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(allFloors);
    Collections.reverse(floorsReverse);
    floorSelector.getItems().addAll(floorsReverse);

    locationSearch.getItems().addAll(dbConnection.getDestLongnames());
    showLocationType.getItems().addAll(observableNodeTypes);

    nodeGroup = new Group();
    edgeGroup = new Group();
    locationGroup = new Group();
    labelPreviewGroup = new Group();
    locationGroup.getChildren().add(labelPreviewGroup);

    locationTypeGroups = new HashMap<>();

    // getter for a private final field
    dbConnection
        .getNodeTypes()
        .forEach(
            nodeType -> {
              Group g = new Group();
              locationGroup.getChildren().add(g);
              locationTypeGroups.put(nodeType, g);
            });

    drawGroup.getChildren().addAll(nodeGroup, edgeGroup, locationGroup);

    // set initial zoom and center
    gesturePane.centreOn(new Point2D(1700, 1100));
    gesturePane.zoomTo(0.4, new Point2D(2500, 1600));

    // other buttons in radio group are deselected by default
    radioNoEdge.setSelected(true);

    // ensure the vbox holding the new location & date fields is managed only when visible
    newLocationVbox.managedProperty().bind(newLocationVbox.visibleProperty());
    newLocationVbox.setDisable(true);

    // set up observable lists using data from the db

    // populate list of nodes
    dbConnection
        .getAllEntries(TableEntryType.NODE)
        .forEach(
            n -> {
              Node node = (Node) n;
              nodeID2idx.put(node.getNodeID(), nodes.size());
              nodes.add(node);
            });

    // populate list of edges
    dbConnection.getAllEntries(TableEntryType.EDGE).forEach(e -> edges.add((Edge) e));

    // populate list of locations
    dbConnection
        .getAllEntries(TableEntryType.LOCATION)
        .forEach(
            l -> {
              Location loc = (Location) l;
              longname2idx.put(loc.getLongName(), locations.size());
              locations.add(loc);
            });

    // populate list of moves
    // and link nodes to locations
    dbConnection
        .getAllEntries(TableEntryType.MOVE)
        .forEach(
            m -> {
              Move move = (Move) m;
              Node node = nodes.get(nodeID2idx.get(move.getNodeID()));
              Location location = locations.get(longname2idx.get(move.getLongName()));

              // link the location to the node and store the date too
              node.addMove(location, move.getDate());

              // link the node the location
              location.setNode(node);

              moves.add(move);
            });

    // set up listeners on observables
    // DO NOT modify the lists inside the change handlers nor in another thread

    nodes.addListener(
        new ListChangeListener<Node>() {
          @Override
          public void onChanged(Change<? extends Node> change) {
            while (change.next()) {

              // possible changes: update, remove, insert

              if (change.wasUpdated()) {

                HashMap<Integer, Node> updated = new HashMap<>();
                for (int i = change.getFrom(); i < change.getTo(); i++) {
                  updated.put(nodes.get(i).getNodeID(), nodes.get(i));
                }

                // update the nodecircles in the drawn group
                nodeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          NodeCircle nodeCircle = (NodeCircle) fxnode;

                          if (updated.containsKey(nodeCircle.getNodeID())) {

                            // update center of node circle
                            nodeCircle.setCenter(updated.get(nodeCircle.getNodeID()).getPoint());
                            fillNodeFields(updated.get(nodeCircle.getNodeID()));
                          }
                        });

                // update the edgelines connected to the updated nodes
                edgeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          EdgeLine edgeLine = (EdgeLine) fxnode;

                          updated.forEach(
                              (nodeID, node) -> {
                                if (edgeLine.containsNode(nodeID)) {
                                  edgeLine.updateEndpoint(node);
                                }
                              });
                        });

              } else { // handle remove and insert

                HashSet<Integer> removed =
                    new HashSet<>(change.getRemoved().stream().map(Node::getNodeID).toList());

                // since arraylist removal shifts all subsequent elements to the left, id2idx must
                // be updated
                change.getRemoved().forEach(node -> {});

                // remove nodecircles from the draw group
                nodeGroup
                    .getChildren()
                    .removeIf(node -> removed.contains(((NodeCircle) node).getNodeID()));

                // add new nodecircles to the node draw group
                change
                    .getAddedSubList()
                    .forEach(
                        node -> {
                          // save nodeID -> idx mapping
                          nodeID2idx.put(node.getNodeID(), nodes.indexOf(node));
                          nodeGroup.getChildren().add(drawNode(node));
                        });

                nodeGroup
                    .getChildren()
                    .addAll(change.getAddedSubList().stream().map(node -> drawNode(node)).toList());
              }
            }
          }
        });

    // update ui when edge list changes
    edges.addListener(
        new ListChangeListener<Edge>() {
          @Override
          public void onChanged(Change<? extends Edge> change) {
            while (change.next()) {
              // possible edge operations: insert, remove

              HashSet<Edge> removed = new HashSet<>(change.getRemoved());

              // remove edgelines that match an edge in removed
              edgeGroup.getChildren().removeIf(fxnode -> ((EdgeLine) fxnode).matchesOneOf(removed));

              // add new edgelines to the edge draw group
              edgeGroup
                  .getChildren()
                  .addAll(
                      change.getAddedSubList().stream()
                          .map(
                              // create new edgeline for each added edge
                              edge -> drawEdge(edge, radioAllEdge.isSelected()))
                          .toList());
            }
          }
        });

    // no listeners needed for general location and move lists

    // listeners for observable lists of selected items

    selectedNodes.addListener(
        new ListChangeListener<Integer>() {
          @Override
          public void onChanged(Change<? extends Integer> change) {

            while (change.next()) {
              // possible operations: remove or add

              HashSet<Integer> removed = new HashSet<>(change.getRemoved());
              HashSet<Integer> added = new HashSet<>(change.getAddedSubList());

              // update highlighted nodes on ui
              nodeGroup
                  .getChildren()
                  .forEach(
                      fxnode -> {
                        NodeCircle nodeCircle = (NodeCircle) fxnode;

                        if (removed.contains(nodeCircle.getNodeID())) {
                          nodeCircle.reset();

                        } else if (added.contains(nodeCircle.getNodeID())) {
                          nodeCircle.highlight();
                        }
                      });
            }

            // add locations to right side pane:
            // no nodes selected: empty node info pane, hide location pane
            // 1 node selected: show node info, locations at the node, box to add new location
            // >1 node selected: empty node info pane, hide location pane

            if (selectedNodes.isEmpty()) {

              // disable since no nodes are selected
              radioSelectedEdge.setDisable(true);
              delNode.setDisable(true);
              addEdge.setDisable(true);
              delEdge.setDisable(true);

              clearNodeFields();
              clearLocationFields();

              newLocationVbox.setDisable(true);

            } else {

              //
              if (selectedNodes.size() == 1) {

                Node node = nodes.get(nodeID2idx.get(selectedNodes.get(0)));
                fillNodeFields(node);
                fillLocationFields(node.getLocations(today));
                newLocationVbox.setDisable(false);

              } else {

                clearNodeFields();
                clearLocationFields();

                newLocationVbox.setDisable(true);
              }

              // for any number > 0 of selected nodes

              radioSelectedEdge.setDisable(false);
              delNode.setDisable(false);
              addEdge.setDisable(false);
              delEdge.setDisable(false);

              // have edges to show
              if (radioSelectedEdge.isSelected()) {
                edgeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          EdgeLine edgeLine = (EdgeLine) fxnode;
                          if (edgeLine.containsNodes(selectedNodes)) {
                            edgeLine.setVisible(true);
                          }
                        });
              }
            }
          }
        });

    selectedEdges.addListener(
        new ListChangeListener<Edge>() {
          @Override
          public void onChanged(Change<? extends Edge> change) {

            while (change.next()) {
              // possible operations: remove or add

              HashSet<Edge> removed = new HashSet<>(change.getRemoved());
              HashSet<Edge> added = new HashSet<>(change.getAddedSubList());

              // update highlighted edges on ui
              edgeGroup
                  .getChildren()
                  .forEach(
                      fxnode -> {
                        EdgeLine edgeLine = (EdgeLine) fxnode;

                        // reset the deselected edges
                        if (edgeLine.matchesOneOf(removed)) {
                          edgeLine.reset();

                          // highlight the newly selected edges
                        } else if (edgeLine.matchesOneOf(added)) {
                          edgeLine.highlight();
                        }
                      });
            }

            if (selectedEdges.isEmpty()) {

            } else {
              delEdge.setDisable(false);
            }
          }
        });

    // draw everything on current floor
    switchFloor(allFloors.get(currentFloor));

    // buttons and state switching

    moveEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));

    save.setOnMouseClicked(
        event -> {
          System.out.println("saving " + editQueue.size() + " edits");

          dbConnection.processEditQueue(editQueue);

          editQueue.clear();
        });

    // TODO WIP: instead of storing list of undone edits, just move pointer back in edit queue
    undo.setOnMouseClicked(
        event -> {
          if (!editQueue.canUndo()) {
            System.out.println("no edits to undo");

          } else {
            DataEdit<Object> lastEdit = editQueue.undoEdit();

            switch (lastEdit.getTable()) {
              case NODE:
                switch (lastEdit.getType()) {
                  case INSERT:
                    // undo node insertion by deleting the node
                    Node insertedNode = (Node) lastEdit.getNewEntry();

                    // delete the added node
                    selectedNodes.clear();
                    selectedNodes.add(insertedNode.getNodeID());
                    // this function removes and handles the index shifting
                    removeSelectedNodes(false);
                    break;

                  case REMOVE:
                    // undo node deletion by inserting the node
                    int removedNodeID = (int) lastEdit.getNewEntry();

                    // move the node from the map of removed nodes to the list of nodes
                    Node removedNode = removedNodes.remove(removedNodeID);
                    // add to observable list and let listeners handle drawing
                    nodes.add(removedNode);

                    break;

                  case UPDATE:
                    // undo moving node by moving it to its previous point
                    Node origNode = (Node) lastEdit.getOldEntry();
                    Node movedNode = (Node) lastEdit.getNewEntry();
                    // listeners will draw it in its original spot
                    movedNode.setPoint(origNode.getPoint());

                    break;
                }
                break;

              case EDGE:
                switch (lastEdit.getType()) {
                  case INSERT:
                    // undo edge insertion by removing it
                    Edge insertedEdge = (Edge) lastEdit.getNewEntry();

                    // delete the edge
                    selectedEdges.clear();
                    selectedEdges.add(insertedEdge);
                    removeSelectedEdges(false);
                    break;

                  case REMOVE:
                    // undo edge deletion by inserting it
                    Edge removedEdge = (Edge) lastEdit.getNewEntry();
                    edges.add(removedEdge);
                    break;

                  case UPDATE:
                    // no update edits for edges

                }

              case LOCATION:

              case MOVE:
            }
          }
        });

    importCSV.setOnMouseClicked(
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

          boolean importedNodes = dbConnection.importCSV(nodePath, false, TableEntryType.NODE);
          boolean importedLocations =
              dbConnection.importCSV(locationPath, false, TableEntryType.LOCATION);
          boolean importedMoves = dbConnection.importCSV(movePath, false, TableEntryType.MOVE);
          boolean importedEdges = dbConnection.importCSV(edgePath, false, TableEntryType.EDGE);

          if (!(importedNodes && importedLocations && importedMoves && importedEdges)) {
            PopOver errorPopup = new PopOver();

            Label label = new Label("Error importing CSVs, please try again");
            label.setStyle(" -fx-background-color: white;");
            label.setMinWidth(220);
            label.setMinHeight(30);

            errorPopup.setContentNode(label);

            errorPopup.show(importCSV);
          }

          // clear everything and re-init

          selectedNodes.clear();
          selectedEdges.clear();

          nodes.clear();
          nodeID2idx.clear();

          edges.clear();

          locations.clear();
          longname2idx.clear();

          moves.clear();

          editQueue.clear();
          nodesOnFloor.clear();

          locationTypeGroups.forEach((nodeType, group) -> group.getChildren().clear());
          visibleNodeTypes.clear();

          initialize();
        });

    exportCSV.setOnMouseClicked(
        event -> {
          dirChooser.setTitle("Select Export Directory");
          String exportPath = dirChooser.showDialog(Fapp.getPrimaryStage()).getAbsolutePath();
          dbConnection.exportCSV(exportPath, TableEntryType.NODE);
          dbConnection.exportCSV(exportPath, TableEntryType.LOCATION);
          dbConnection.exportCSV(exportPath, TableEntryType.MOVE);
          dbConnection.exportCSV(exportPath, TableEntryType.EDGE);
          // fileChooser.setInitialDirectory(new File(exportPath));
        });

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

    mapImg.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.ADDING_NODE) {
            insertNode(new Point2D(event.getX(), event.getY()));

            addEdge.setDisable(false);

            state = EDITOR_STATE.EDITING_NODE;

          } else if (state == EDITOR_STATE.EDITING_NODE
              || state == EDITOR_STATE.EDITING_EDGE
              || state == EDITOR_STATE.PREVIEWING) {

            // clear node stuff
            selectedNodes.clear();

            // clear & hide edge stuff
            selectedEdges.clear();

            // since no nodes are selected anymore, switch to no edge
            // note that if all edges (or none) are visible, this wont change anything
            if (radioSelectedEdge.isSelected()) {
              radioNoEdge.fire();
            }

            // clear location search box
            locationSearch.clearSelection();

            state = EDITOR_STATE.IDLE;
          }
        });

    addNode.setOnMouseClicked(
        event -> {
          // go to adding state no matter the previous state
          state = EDITOR_STATE.ADDING_NODE;
          // disable other buttons while user adds node
          delNode.setDisable(true);
          addEdge.setDisable(true);
          delEdge.setDisable(true);
        });

    delNode.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {
            removeSelectedNodes(true);
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

            // make sure the added edges are made visible
            if (!radioAllEdge.isSelected()) radioSelectedEdge.fire();
          }
        });

    delEdge.setOnMouseClicked(
        event -> {
          if (state == EDITOR_STATE.EDITING_EDGE) {
            removeSelectedEdges(true);
            state = EDITOR_STATE.IDLE;

          } else if (state == EDITOR_STATE.EDITING_NODE) {
            // remove all edges connected to any of the selected nodes
            edges.forEach(
                edge -> {
                  if (edge.containsOneOf(selectedNodes)) {
                    selectedEdges.add(edge);
                  }
                });
            removeSelectedEdges(true);

            // stay in node editing state
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
          if (radioSelectedEdge.isSelected()) {

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

    showLocationType
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            new ListChangeListener<NodeType>() {
              @Override
              public void onChanged(Change<? extends NodeType> c) {
                while (c.next()) {
                  c.getAddedSubList()
                      .forEach(
                          type -> {
                            visibleNodeTypes.add(type);
                            locationTypeGroups.get(type).setVisible(true);
                          });
                  c.getRemoved()
                      .forEach(
                          type -> {
                            visibleNodeTypes.remove(type);
                            locationTypeGroups.get(type).setVisible(false);
                          });
                }
                System.out.println(
                    "selected node types: " + showLocationType.getCheckModel().getCheckedItems());
              }
            });

    clearLocationType.setOnMouseClicked(
        event -> {
          visibleNodeTypes.clear();
          observableNodeTypes.forEach(
              type -> showLocationType.getItemBooleanProperty(type).setValue(false));
          // observableNodeTypes.forEach(type -> locationLabels.get(type).setVisible(false));
          // showLocationType.setTitle("");
        });

    locationSearch.setOnAction(
        event -> {
          Node searchedNode = locations.get(longname2idx.get(locationSearch.getValue())).getNode();
          if (searchedNode != null) {

            if (!searchedNode.getFloor().equals(allFloors.get(currentFloor))) {
              // floor selector's on action automatically switches floor
              floorSelector.setValue(searchedNode.getFloor());
              // switchFloor(node.getFloor());
            }
            gesturePane.centreOn(searchedNode.getPoint());
            gesturePane.zoomTo(1.25, searchedNode.getPoint());

            selectedNodes.clear();
            selectedNodes.add(searchedNode.getNodeID());

            state = EDITOR_STATE.EDITING_NODE;

          } else {
            System.out.println("location " + locationSearch.getValue() + " not at any node");
          }
        });

    Fapp.getPrimaryStage()
        .getScene()
        .setOnKeyPressed(
            event -> {
              System.out.println("key pressed: " + event.getCode());
              if (event.getCode() == KeyCode.DELETE) {

                removeSelectedNodes(true);
                removeSelectedEdges(true);

                // in case a node is deleted while dragging (gestures are disabled when dragging)
                gesturePane.setGestureEnabled(true);
                state = EDITOR_STATE.IDLE;

              } else if (event.getCode() == KeyCode.ESCAPE) {
                state = EDITOR_STATE.IDLE;
                selectedNodes.clear();
                selectedEdges.clear();
                locationSearch.clearSelection();

              } else if (event.getCode() == KeyCode.CONTROL) {
                controlPressed = true;

              } else if (event.getCode() == KeyCode.A) {
                moveSelectedNodes(2, MOVE_DIRECTION.LEFT);
              } else if (event.getCode() == KeyCode.W) {
                moveSelectedNodes(2, MOVE_DIRECTION.UP);
              } else if (event.getCode() == KeyCode.D) {
                moveSelectedNodes(2, MOVE_DIRECTION.RIGHT);
              } else if (event.getCode() == KeyCode.S && !controlPressed) {
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

                // find the previous positions and create the edit objects
                nodeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          NodeCircle nodeCircle = (NodeCircle) fxnode;
                          if (selectedNodes.contains(nodeCircle.getNodeID())) {
                            Node newNode = nodes.get(nodeID2idx.get(nodeCircle.getNodeID()));

                            Node oldNode = newNode.deepCopy();
                            oldNode.setX(nodeCircle.getPrevX());
                            oldNode.setY(nodeCircle.getPrevY());

                            editQueue.add(
                                new DataEdit<>(
                                    oldNode, newNode, DataEditType.UPDATE, TableEntryType.NODE),
                                false);
                          }
                        });
              }
            });

    linearAlign.setOnMouseClicked(
        event -> {
          // find the line of best fit through the selected points
          // project each point to the line
          // https://online.stat.psu.edu/stat501/lesson/1/1.2

          double xbar = 0, ybar = 0;

          List<Node> nodesToAlign =
              selectedNodes.stream().map(nodeID -> nodes.get(nodeID2idx.get(nodeID))).toList();

          for (Node node : nodesToAlign) {
            xbar += node.getX();
            ybar += node.getY();
          }

          xbar /= nodesToAlign.size();
          ybar /= nodesToAlign.size();

          double numerator = 0, denominator = 0;
          for (Node node : nodesToAlign) {
            numerator += (node.getX() - xbar) * (node.getY() - ybar);
            denominator += Math.pow(node.getX() - xbar, 2);
          }

          // if the line of best fit is essentially vertical, align each point to that line
          if (Math.abs(denominator) < 1) {

            double x =
                (nodesToAlign.get(0).getX() + nodesToAlign.get(nodesToAlign.size() - 1).getX()) / 2;
            nodesToAlign.forEach(node -> node.setPoint(new Point2D(x, node.getY())));

          } else {

            // equation of line in y = mx + b form
            double m = numerator / denominator;
            double b = ybar - m * xbar;

            // through origin with same slope as line
            Point2D line = new Point2D(500, 500 * m);

            double line2 = line.dotProduct(line);
            nodesToAlign.forEach(
                node -> {
                  Node oldNode = node.deepCopy();
                  Point2D v = node.getPoint().subtract(0, b);
                  Point2D proj = line.multiply(v.dotProduct(line) / line2);

                  // update node point in observable list and listeners will update ui
                  node.setPoint(proj.add(0, b));

                  // save edit
                  editQueue.add(
                      new DataEdit<>(oldNode, node, DataEditType.UPDATE, TableEntryType.NODE),
                      false);
                });

            // update prev x and y
            nodeGroup
                .getChildren()
                .forEach(
                    fxnode -> {
                      NodeCircle nodeCircle = (NodeCircle) fxnode;

                      if (selectedNodes.contains(nodeCircle.getNodeID())) {
                        Point2D p = nodes.get(nodeID2idx.get(nodeCircle.getNodeID())).getPoint();
                        nodeCircle.setPrevX(p.getX());
                        nodeCircle.setPrevY(p.getY());
                      }
                    });
          }
        });

    snapGrid.setOnMouseClicked(
        event -> {
          selectedNodes.forEach(
              nodeID -> {
                Node node = nodes.get(nodeID2idx.get(nodeID));
                Node oldNode = node.deepCopy();

                node.snapToGrid(5);

                // update prev x and y
                nodeGroup
                    .getChildren()
                    .forEach(
                        fxnode -> {
                          NodeCircle nodeCircle = (NodeCircle) fxnode;
                          if (nodeCircle.getNodeID() == node.getNodeID()) {
                            nodeCircle.setPrevX(node.getX());
                            nodeCircle.setPrevY(node.getY());
                          }
                        });

                // save edit
                editQueue.add(
                    new DataEdit<>(oldNode, node, DataEditType.UPDATE, TableEntryType.NODE), false);
              });
        });

    newLocationSubmit.setOnMouseClicked(
        event -> {
          Location newLocation =
              new Location(
                  newLocationLongname.getText(),
                  newLocationShortname.getText(),
                  NodeType.valueOf(newLocationType.getValue()));
          editQueue.add(
              new DataEdit<>(newLocation, DataEditType.INSERT, TableEntryType.LOCATION), false);

          Move move =
              new Move(selectedNodes.get(0), newLocation.getLongName(), newLocationDate.getValue());
          editQueue.add(new DataEdit<>(move, DataEditType.INSERT, TableEntryType.MOVE), false);
        });
  }

  private void switchFloor(String floor) {

    mapImg.setImage(images.get(floor));

    /* TODO optional
       one thread does nodes,
       one thread does edges,
       another thread does locations
    */

    nodesOnFloor.clear();
    nodeGroup.getChildren().clear();

    // add nodecircles from this floor to nodegroup
    nodeGroup
        .getChildren()
        .addAll(
            nodes.stream()
                // keep the nodes on the current floor
                .filter(node -> node.getFloor().equals(floor))
                .map(
                    node -> {
                      // cache the ids to use later
                      nodesOnFloor.add(node.getNodeID());
                      return drawNode(node);
                    })
                .toList());

    edgeGroup.getChildren().clear();

    // add edgelines from this floor to edge group
    edgeGroup
        .getChildren()
        .addAll(
            edges.stream()
                .filter(
                    // keep the edges on this floor
                    edge ->
                        nodesOnFloor.contains(edge.getStartNode())
                            || nodesOnFloor.contains(edge.getEndNode()))
                .map(
                    edge -> {
                      // create edgeline from edge and user defined visibility
                      return drawEdge(edge, radioAllEdge.isSelected());
                    })
                .toList());

    // clear groups in map (type -> group of labels)
    locationTypeGroups.forEach((key, value) -> value.getChildren().clear());

    // create location labels from this floor and add to location group based on type
    nodes.stream()
        .filter(node -> nodesOnFloor.contains(node.getNodeID()))
        .forEach(
            node -> {
              NodeText label = drawLocationLabel(node);

              List<NodeType> nodeTypes =
                  node.getLocations(today).stream().map(Location::getNodeType).toList();

              // add location labels to the correct group (dependent on node type)
              // nodes with multiple locations share a label so it gets added to both type groups
              nodeTypes.forEach(
                  nodeType -> locationTypeGroups.get(nodeType).getChildren().add(label));
            });

    // show only needed location labels
    locationTypeGroups.forEach(
        (nodeType, group) -> {
          group.setVisible(visibleNodeTypes.contains(nodeType));
        });

    if (state != EDITOR_STATE.PREVIEWING) {

      selectedNodes.clear();
      selectedEdges.clear();
    }

    // TODO bring back drawing locations
    // drawLocations(floor, toggleLocations.isSelected());
  }

  private NodeCircle drawNode(Node node) {

    NodeCircle nodeCircle = new NodeCircle(node, 6);
    // set default visual characteristics
    nodeCircle.reset();

    nodeCircle.setOnMousePressed(
        event -> {
          if (state == EDITOR_STATE.ADDING_EDGE) {
            // add edge between every selected node and this node
            selectedNodes.forEach(selectedNodeID -> insertEdge(selectedNodeID, node.getNodeID()));
            delEdge.setDisable(false);
          }

          state = EDITOR_STATE.EDITING_NODE;

          if (controlPressed) {

            // control click on a selected node to deselect it
            if (selectedNodes.contains(node.getNodeID())) {
              selectedNodes.remove(Integer.valueOf(node.getNodeID()));

            } else { // control click on unselected node
              selectedNodes.add(node.getNodeID());
            }

          } else { // control not pressed
            selectedNodes.clear();
            selectedNodes.add(node.getNodeID());
          }
        });

    nodeCircle.setOnMouseDragged(
        event -> {
          if (state == EDITOR_STATE.EDITING_NODE) {

            // TODO drag all selected nodes the same amount - later
            gesturePane.setGestureEnabled(false);

            // update this node's point which updates both its nodecircle and the connected edges
            node.setPoint(new Point2D(event.getX(), event.getY()));
          }
        });

    nodeCircle.setOnMouseReleased(
        event -> {
          // enable gesture pane outside if in case the state changed while dragging
          gesturePane.setGestureEnabled(true);
          if (state == EDITOR_STATE.EDITING_NODE) {

            // TODO: update all selected nodes - later

            // only move node if dragged more than 5 pixels
            double dist =
                Math.sqrt(
                    Math.pow((nodeCircle.getCenterX() - nodeCircle.getPrevX()), 2)
                        + Math.pow((nodeCircle.getCenterY() - nodeCircle.getPrevY()), 2));

            if (dist > 10) {

              Node oldNode = node.deepCopy();
              oldNode.setPoint(new Point2D(nodeCircle.getPrevX(), nodeCircle.getPrevY()));

              nodeCircle.setPrevX(event.getX());
              nodeCircle.setPrevY((event.getY()));

              // update node and listeners will handle updating the ui
              // node.setPoint(new Point2D(event.getX(), event.getY()));
              node.setBuilding(
                  buildingChecker.getBuilding(node.getPoint(), allFloors.get(currentFloor)));

              System.out.println(oldNode.getPoint() + " dragged to " + node.getPoint());

              // save edit
              editQueue.add(
                  new DataEdit<>(oldNode, node, DataEditType.UPDATE, TableEntryType.NODE), false);

            } else {
              // bring point back to the previous position
              node.setPoint(new Point2D(nodeCircle.getPrevX(), nodeCircle.getPrevY()));
            }
          }
        });

    return nodeCircle;
    // nodeGroup.getChildren().add(nodeCircle);
  }

  private EdgeLine drawEdge(Edge edge, boolean visibility) {

    Point2D startPoint = nodes.get(nodeID2idx.get(edge.getStartNode())).getPoint();
    Point2D endPoint = nodes.get(nodeID2idx.get(edge.getEndNode())).getPoint();

    EdgeLine line = new EdgeLine(edge, startPoint, endPoint);

    line.reset();

    line.setOnMousePressed(
        event -> {
          if (!controlPressed) selectedEdges.clear();
          selectedEdges.add(edge);
          state = EDITOR_STATE.EDITING_EDGE;
        });

    line.setVisible(visibility);

    return line;
  }

  private NodeText drawLocationLabel(Node node) {

    // concat shortnames from locations at this node into one label
    String labelText =
        String.join(", ", node.getLocations(today).stream().map(Location::getShortName).toList());

    return new NodeText(
        node.getNodeID(), node.getX() - labelText.length() * 5, node.getY() - 10, labelText);
  }

  private void insertNode(Point2D point) {

    // keep this call to the db which ensures the id is not given to anyone else
    int id = dbConnection.getNextNodeID();

    String floor = allFloors.get(currentFloor);
    Node node = new Node(id, point, floor, buildingChecker.getBuilding(point, floor));

    // add new node to observable list and listeners will handle the drawing
    nodes.add(node);

    selectedNodes.add(node.getNodeID());

    editQueue.add(new DataEdit<>(node, DataEditType.INSERT, TableEntryType.NODE), false);
  }

  /** @param track true if the edits should get added to the queue, false if not (ex: undoing). */
  private void removeSelectedNodes(boolean track) {

    // remove edges that contain nodes-to-be-removed before removing nodes
    // listeners on observable list of edges will handle ui updates

    Iterator<Edge> edgeItr = edges.iterator();
    while (edgeItr.hasNext()) {
      Edge edge = edgeItr.next();

      // remove edges connected to removed nodes
      // both from db and edge list
      if (selectedNodes.contains(edge.getStartNode())
          || selectedNodes.contains(edge.getEndNode())) {
        editQueue.add(new DataEdit<>(edge, DataEditType.REMOVE, TableEntryType.EDGE), false);
        edgeItr.remove();
      }
    }

    // remove nodes from nodes observable list and listeners will update the ui
    selectedNodes.forEach(
        nodeID -> {
          int removedIdx = nodeID2idx.get(nodeID);

          removedNodes.put(nodeID, nodes.get(removedIdx));

          for (int i = removedIdx + 1; i < nodes.size(); i++) {
            int oldIdx = nodeID2idx.get(nodes.get(i).getNodeID());
            nodeID2idx.put(nodes.get(i).getNodeID(), oldIdx - 1);
          }
          nodes.remove(removedIdx);

          if (track) {
            // record in edit queue
            editQueue.add(new DataEdit<>(nodeID, DataEditType.REMOVE, TableEntryType.NODE), false);
          }
        });

    selectedNodes.clear();
  }

  private void insertEdge(int start, int end) {

    Edge newEdge = new Edge(start, end);

    // let listeners on observable list handle ui
    edges.add(newEdge);

    editQueue.add(new DataEdit<>(newEdge, DataEditType.INSERT, TableEntryType.EDGE), false);
  }

  /** @param track true if the edit should get tracked, false if not (ex: undoing) */
  private void removeSelectedEdges(boolean track) {

    selectedEdges.forEach(
        edge -> {
          edges.remove(edge);
          if (track) {
            editQueue.add(new DataEdit<>(edge, DataEditType.REMOVE, TableEntryType.EDGE), false);
          }
        });

    selectedEdges.clear();
  }

  private void moveSelectedNodes(double dist, MOVE_DIRECTION dir) {

    nodeGroup
        .getChildren()
        .forEach(
            fxnode -> {
              NodeCircle nodeCircle = (NodeCircle) fxnode;
              if (selectedNodes.contains(nodeCircle.getNodeID())) {

                Node node = nodes.get(nodeID2idx.get(nodeCircle.getNodeID()));

                switch (dir) {
                  case LEFT:
                    node.incrX(-dist);
                    // nodeCircle.setPrevX(node.getX());
                    break;

                  case UP:
                    node.incrY(-dist);
                    // nodeCircle.setPrevY(node.getY());
                    break;

                  case RIGHT:
                    node.incrX(dist);
                    // nodeCircle.setPrevX(node.getX());
                    break;

                  case DOWN:
                    node.incrY(dist);
                    // nodeCircle.setPrevY(node.getY());
                    break;
                }
              }
            });
  }

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
  private void fillLocationFields(List<Location> locations) {

    locationScrollpane.setVisible(true);
    locationScrollpane.setDisable(false);
    locationScrollpane.setMaxHeight(300);

    locationsVbox.getChildren().clear();
    locationsVbox.setMaxHeight(300);

    try {

      for (Location location : locations) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/MapEditorLocation.fxml"));

        VBox entry = fxmlLoader.load();

        MapEditorLocationController controller = fxmlLoader.getController();
        controller.setData(location);

        // TODO make this UX better bc switching floors and deselecting the previous node is goofy
        controller.preview.setOnMouseClicked(
            event -> {

              // click preview to preview
              if (state != EDITOR_STATE.PREVIEWING) {

                state = EDITOR_STATE.PREVIEWING;

                if (!selectedNodes.isEmpty()) {
                  controller.setOrigin(nodes.get(nodeID2idx.get(selectedNodes.get(0))));
                }

                Node previewNode = nodes.get(nodeID2idx.get(controller.nodeID));

                if (!previewNode.getFloor().equals(allFloors.get(currentFloor))) {
                  // floor selector's onAction switches floors
                  floorSelector.setValue(previewNode.getFloor());
                }
                gesturePane.centreOn(previewNode.getPoint());
                gesturePane.zoomTo(1.0, previewNode.getPoint());

                NodeText label = drawLocationLabel(previewNode);
                labelPreviewGroup.getChildren().add(label);
                controller.preview.setText("Back");

              } else { // previewing, click back to go back

                Node origin = controller.getOrigin();

                // maybe unnecessary since the only way to fall into this else statement
                // is after the origin has already been set
                if (origin != null) {

                  // clear preview location label
                  labelPreviewGroup.getChildren().clear();

                  if (!origin.getFloor().equals(allFloors.get(currentFloor))) {
                    // floor selector's onAction switches floors
                    floorSelector.setValue(origin.getFloor());
                  }
                  gesturePane.centreOn(origin.getPoint());
                  gesturePane.zoomTo(1.0, origin.getPoint());

                  // highlight origin node again
                  nodeGroup
                      .getChildren()
                      .forEach(
                          fxnode -> {
                            NodeCircle nodeCircle = (NodeCircle) fxnode;
                            if (nodeCircle.getNodeID() == origin.getNodeID()) {
                              nodeCircle.highlight();
                            }
                          });

                  // back to editing the currently selected node
                  state = EDITOR_STATE.EDITING_NODE;
                }
                controller.preview.setText("Preview");
              }
            });

        controller.submit.setOnMouseClicked(
            event -> {
              Move move =
                  new Move(controller.nodeID, controller.longnameText.getText(), controller.date);
              editQueue.add(new DataEdit<>(move, DataEditType.INSERT), false);
            });

        locationsVbox.getChildren().add(entry);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void clearLocationFields() {

    locationsVbox.getChildren().clear();
    locationsVbox.setMaxHeight(0);

    locationScrollpane.setVisible(false);
    locationScrollpane.setDisable(true);
    locationScrollpane.setMaxHeight(0);
  }

  private void refreshSelectedNodes() {
    List<Integer> selectedNodesCopy = selectedNodes.stream().toList();
    selectedNodes.clear();
    selectedNodes.addAll(selectedNodesCopy);
  }

  private void refreshSelectedEdges() {
    List<Edge> selectedEdgesCopy = selectedEdges.stream().toList();
    selectedEdges.clear();
    selectedEdges.addAll(selectedEdgesCopy);
  }
}

class MapEditQueue<Object> extends DataEditQueue<Object> {

  // tracks the last edit displayed
  @Getter @Setter private int undoPointer;
  private int endPointer;

  MapEditQueue() {
    super();
    endPointer = 0;
    undoPointer = 0;
  }

  @Override
  public boolean add(DataEdit<Object> dataEdit, boolean countEntry) {

    // if edits exist beyond the undo pointer, clear them out
    if (undoPointer < endPointer) {
      dataEditQueue.subList(undoPointer, dataEditQueue.size()).clear();
      endPointer = undoPointer;
    }

    undoPointer++;
    endPointer++;

    System.out.println("undo " + undoPointer + " end " + endPointer);

    return dataEditQueue.add(dataEdit);
  }

  /**
   * Undoes the last edit by moving the pointer backwards one.
   *
   * @return
   */
  public DataEdit<Object> undoEdit() {

    // Check if queue is empty
    if (dataEditQueue.isEmpty()) {
      return null;
    }

    // back to first element
    if (undoPointer == 0) {
      return dataEditQueue.get(0);
    }

    // get the index at the decremented pointer
    return dataEditQueue.get(--undoPointer);
  }

  @Override
  public void clear() {
    dataEditQueue.clear();
    pointer = 0;
    editCount = 0;
    endPointer = 0;
    undoPointer = 0;
  }

  public boolean canUndo() {
    // can't undo beyond first element
    return undoPointer > 0;
  }

  // leave the DataEditQueue next() and hasNext() as they are
  // for iterating through the queue in fdb.processEdits(queue)
}

enum EDITOR_STATE {
  IDLE,
  PREVIEWING,

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
