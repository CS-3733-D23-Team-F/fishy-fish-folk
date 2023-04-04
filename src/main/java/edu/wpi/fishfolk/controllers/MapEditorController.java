package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;

public class MapEditorController extends AbsController {
  @FXML private TableView<Node> table;
  @FXML private TableColumn<Node, String> id;
  @FXML private TableColumn<Node, String> x;
  @FXML private TableColumn<Node, String> y;
  @FXML private TableColumn<Node, String> floor;
  @FXML private TableColumn<Node, String> building;
  @FXML private TableColumn<Node, String> date;
  @FXML private TableColumn<Node, String> type;
  @FXML private TableColumn<Node, String> longName;
  @FXML private TableColumn<Node, String> shortName;

  @FXML MFXButton backButton;

  private ArrayList<DataEdit> dataEdits;

  @FXML
  public void initialize() {
    // sets up the columns in the table
    id.setCellValueFactory(new PropertyValueFactory<Node, String>("id"));
    x.setCellValueFactory(new PropertyValueFactory<Node, String>("x"));
    y.setCellValueFactory(new PropertyValueFactory<Node, String>("y"));
    floor.setCellValueFactory(new PropertyValueFactory<Node, String>("floor"));
    building.setCellValueFactory(new PropertyValueFactory<Node, String>("building"));
    date.setCellValueFactory(new PropertyValueFactory<Node, String>("date"));
    type.setCellValueFactory(new PropertyValueFactory<Node, String>("type"));
    longName.setCellValueFactory(new PropertyValueFactory<Node, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<Node, String>("shortName"));
    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    // load data
    populateTable();

    table.setEditable(true);

    x.setCellFactory(TextFieldTableCell.forTableColumn());
    y.setCellFactory(TextFieldTableCell.forTableColumn());
    floor.setCellFactory(TextFieldTableCell.forTableColumn());
    building.setCellFactory(TextFieldTableCell.forTableColumn());
    date.setCellFactory(TextFieldTableCell.forTableColumn());
    type.setCellFactory(TextFieldTableCell.forTableColumn());
    longName.setCellFactory(TextFieldTableCell.forTableColumn());
    shortName.setCellFactory(TextFieldTableCell.forTableColumn());

    x.setOnEditCommit(this::handleEditCommit_X);
    y.setOnEditCommit(this::handleEditCommit_Y);
    floor.setOnEditCommit(this::handleEditCommit_Floor);
    building.setOnEditCommit(this::handleEditCommit_Building);
    date.setOnEditCommit(this::handleEditCommit_Date);
    type.setOnEditCommit(this::handleEditCommit_Type);
    longName.setOnEditCommit(this::handleEditCommit_LongName);
    shortName.setOnEditCommit(this::handleEditCommit_ShortName);

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

  public ObservableList<Node> getNodes() {
    String pointX = "1";
    String pointY = "3";
    ObservableList<Node> nodes = FXCollections.observableArrayList();
    // pointX = String.valueOf(node.point.getX());
    // pointY = String.valueOf(node.point.getY());
    nodes.add(
        new Node(38, new Point2D(0.0, 1.0), "floor", "building", NodeType.CONF, "name", "abb"));
    return nodes;
  }

  public void populateTable() {
    table.setItems(getNodes());
  }

  /**
   * Request NodeTable to queue edits to database. On each edit's success: 1. Make the BG of the
   * cell white, 2. Remove DataEdit from collection
   *
   * @return True on successful submission, false otherwise
   */
  public void submitEdits() {
    dataEdits.removeIf(dataEdit -> dbConnection.nodeTable.update(
            dataEdit.getId(),
            dataEdit.getHeader(),
            dataEdit.getValue()
    ));
  }
}
