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

    /*
        x.setOnEditCommit(this::handleEditCommit_X);
        y.setOnEditCommit(this::handleEditCommit_Y);
        floor.setOnEditCommit(this::handleEditCommit_Floor);
        building.setOnEditCommit(this::handleEditCommit_Building);
        date.setOnEditCommit(this::handleEditCommit_Date);
        type.setOnEditCommit(this::handleEditCommit_Type);
        longName.setOnEditCommit(this::handleEditCommit_LongName);
        shortName.setOnEditCommit(this::handleEditCommit_ShortName);
    */
    backButton.setOnAction(
        event -> {
          Navigation.navigate(Screen.HOME);
        });

    dataEdits = new ArrayList<>();
  }

  private void handleEditCommit_ShortName(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_LongName(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Type(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Date(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Building(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Floor(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  public void handleEditCommit_X(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  private void handleEditCommit_Y(TableColumn.CellEditEvent<Node, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
  }

  public ObservableList<ObservableNode> getNodes() {

    ObservableList<ObservableNode> nodes = FXCollections.observableArrayList();
    for (Node n : dbConnection.nodeTable.getAllNodes()) {
      ArrayList<String> next = n.deconstruct();
      ObservableNode update =
          new ObservableNode(
              next.get(0),
              next.get(1),
              next.get(2),
              next.get(3),
              next.get(4),
              next.get(5),
              next.get(6),
              next.get(7));
      nodes.add(update);
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
    dataEdits.removeIf(
        dataEdit ->
            dbConnection.nodeTable.update(
                dataEdit.getId(), dataEdit.getHeader(), dataEdit.getValue()));
  }
}
