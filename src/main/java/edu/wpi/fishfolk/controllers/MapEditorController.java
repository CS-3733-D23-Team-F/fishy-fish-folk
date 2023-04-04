package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.ObservableNode;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
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
  @FXML private TableColumn<ObservableNode, String> longName;
  @FXML private TableColumn<ObservableNode, String> shortName;

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
    longName.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("shortName"));
    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    // load data
    populateTable();

    table.setEditable(true);

    x.setCellFactory(TextFieldTableCell.forTableColumn());
    y.setCellFactory(TextFieldTableCell.forTableColumn());
    floor.setCellFactory(TextFieldTableCell.forTableColumn());
    building.setCellFactory(TextFieldTableCell.forTableColumn());
    type.setCellFactory(TextFieldTableCell.forTableColumn());
    longName.setCellFactory(TextFieldTableCell.forTableColumn());
    shortName.setCellFactory(TextFieldTableCell.forTableColumn());

    x.setOnEditCommit(this::handleEditCommit_X);
    y.setOnEditCommit(this::handleEditCommit_Y);
    floor.setOnEditCommit(this::handleEditCommit_Floor);
    building.setOnEditCommit(this::handleEditCommit_Building);
    type.setOnEditCommit(this::handleEditCommit_Type);
    longName.setOnEditCommit(this::handleEditCommit_LongName);
    shortName.setOnEditCommit(this::handleEditCommit_ShortName);

    backButton.setOnAction(
        event -> {
          Navigation.navigate(Screen.HOME);
        });

    dataEdits = new ArrayList<>();
  }

  private void handleEditCommit_ShortName(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_LongName(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Type(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Date(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Building(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Floor(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
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

    // removeAnyOldCommits(nodeid, POINT_X);
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

  public ObservableList<ObservableNode> getNodes() {

    ObservableList<ObservableNode> nodes = FXCollections.observableArrayList();
    for (Node node : dbConnection.nodeTable.getAllNodes()) {
      nodes.add(new ObservableNode(node));
    }
    return nodes;
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
      // TODO: Pass ObservableNode object through DataEdit
      // TODO: Convert to Node object (store in ObservableNode?)
      Node newNode = new Node();
      dbConnection.nodeTable.update(newNode, "9/9/99");
    }

    dataEdits.removeIf(
        dataEdit ->
            dbConnection.nodeTable.update(
                dataEdit.getId(), dataEdit.getHeader(), dataEdit.getValue()));
  }
}
