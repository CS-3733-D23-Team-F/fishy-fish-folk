package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.ObservableNode;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class MapEditorController extends AbsController {
  @FXML private TableView<ObservableNode> table;
  @FXML private TableColumn<ObservableNode, String> id;
  @FXML private TableColumn<ObservableNode, String> x;
  @FXML private TableColumn<ObservableNode, String> y;
  @FXML private TableColumn<ObservableNode, String> floor;
  @FXML private TableColumn<ObservableNode, String> building;
  @FXML private TableColumn<ObservableNode, String> type;
  @FXML private TableColumn<ObservableNode, String> date;
  @FXML private TableColumn<ObservableNode, String> longName;
  @FXML private TableColumn<ObservableNode, String> shortName;
  @FXML private TableColumn<ObservableNode, String> edges;

  @FXML MFXButton backButton;

  private ArrayList<DataEdit> dataEdits;

  @FXML
  public void initialize() {
    // sets up the columns in the table
    id.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("id"));
    x.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("x"));
    y.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("y"));
    floor.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("floor"));
    building.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("building"));
    type.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("type"));
    date.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("date"));
    longName.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("shortName"));
    edges.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("edge"));

    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    // load data
    populateTable();

    table.setEditable(true);

    x.setCellFactory(TextFieldTableCell.forTableColumn());
    y.setCellFactory(TextFieldTableCell.forTableColumn());
    floor.setCellFactory(TextFieldTableCell.forTableColumn());
    building.setCellFactory(TextFieldTableCell.forTableColumn());
    type.setCellFactory(TextFieldTableCell.forTableColumn());
    date.setCellFactory(TextFieldTableCell.forTableColumn());
    longName.setCellFactory(TextFieldTableCell.forTableColumn());
    shortName.setCellFactory(TextFieldTableCell.forTableColumn());
    edges.setCellFactory(TextFieldTableCell.forTableColumn());

    x.setOnEditCommit(this::handleEditCommit_X);
    y.setOnEditCommit(this::handleEditCommit_Y);
    floor.setOnEditCommit(this::handleEditCommit_Floor);
    building.setOnEditCommit(this::handleEditCommit_Building);
    type.setOnEditCommit(this::handleEditCommit_Type);
    date.setOnEditCommit(this::handleEditCommit_Date);
    longName.setOnEditCommit(this::handleEditCommit_LongName);
    shortName.setOnEditCommit(this::handleEditCommit_ShortName);
    edges.setOnEditCommit(this::handleEditCommit_Edges);

    backButton.setOnAction(
        event -> {
          Navigation.navigate(Screen.HOME);
        });

    dataEdits = new ArrayList<>();
  }

  public void handleEditCommit_X(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell

    t.getTableView().getItems().get(t.getTablePosition().getRow()).x = t.getNewValue();

    // Verify
    boolean verified = true;
    double value = -1;

    // check value is a valid double
    try {
      value = Double.parseDouble(t.getNewValue());
    } catch (NumberFormatException e) {
      verified = false;
    }

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    // removeAnyOldCommits(nodeid, header); // not strictly necessary
    if (verified) {
      DataEdit edit = new DataEdit(node.id, "x", t.getNewValue());
      System.out.println("Verified edit to col X");
      dataEdits.add(edit);
      submitEdits();
      System.out.println("Submitted.");
    }
  }

  private void handleEditCommit_Y(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Floor(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Building(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Type(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_LongName(TableColumn.CellEditEvent<ObservableNode, String> t) {

    t.getTableView().getItems().get(t.getTablePosition().getRow()).longName = t.getNewValue();

    // nothing to verify for strings

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    // removeAnyOldCommits(nodeid, header); // not strictly necessary
    DataEdit edit = new DataEdit(node.id, "longname", t.getNewValue());
    System.out.println("Verified edit to col longname");
    dataEdits.add(edit);
    submitEdits();
    System.out.println("Submitted.");
  }

  private void handleEditCommit_ShortName(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Date(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Edges(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  public ObservableList<ObservableNode> getNodes() {

    ObservableList<ObservableNode> observableNodes = FXCollections.observableArrayList();

    Node[] nodes = dbConnection.nodeTable.getAllNodes();
    ArrayList<String> dates = dbConnection.nodeTable.getColumn("date");

    // map from node ids to index in array of nodes (equal index in list of observable nodes)
    HashMap<String, Integer> id2idx = new HashMap<>(nodes.length * 4 / 3 + 1);

    // put together node data and dates and leave edges blank for now
    for (int i = 0; i < nodes.length; i++) {
      id2idx.put(nodes[i].id, i);
      observableNodes.add(new ObservableNode(nodes[i], dates.get(i), new ArrayList<>()));
    }

    // two arraylists in an array
    // edgesRaw[0] is the list of start nodes
    // edgesRaw[1] is the list of end nodes
    // thus edge i is between edgesRaw[0].get(i) and edgesRaw[1].get(i)
    ArrayList<String>[] edgesRaw = dbConnection.edgeTable.getAll();

    for (int i = 0; i < edgesRaw[0].size(); i++) {

      //add end to start's adjacent nodes
      int startIdx = id2idx.get(edgesRaw[0].get(i));
      observableNodes.get(startIdx).addAdjNode(edgesRaw[1].get(i));

      //add start to end's adjacent nodes
      int endIdx = id2idx.get(edgesRaw[1].get(i));
      observableNodes.get(endIdx).addAdjNode(edgesRaw[0].get(i));
    }

    return observableNodes;
  }

  public void populateTable() {
    table.setItems(getNodes());
  }

  /**
   * Request NodeTable to queue edits to database. On each edit's success: 1. Make the BG of the
   * cell white, 2. Remove DataEdit from collection.
   */
  public void submitEdits() {

    for (DataEdit edit : dataEdits) {
      dbConnection.nodeTable.update(edit.id, edit.attr, edit.value);
    }

    //    dataEdits.removeIf(
    //        dataEdit ->
    //            dbConnection.nodeTable.update(
    //                dataEdit.getId(), dataEdit.getHeader(), dataEdit.getValue()));
  }
}
