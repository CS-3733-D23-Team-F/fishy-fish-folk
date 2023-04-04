package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.NodeTable;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class MapEditorController {
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
    date.setCellValueFactory(new PropertyValueFactory<Node, String>("date")); // doesn't exist
    type.setCellValueFactory(new PropertyValueFactory<Node, String>("type"));
    longName.setCellValueFactory(new PropertyValueFactory<Node, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<Node, String>("shortName"));
    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    // load data
    populateTable();

    table.setEditable(true);

    id.setCellFactory(TextFieldTableCell.forTableColumn());
    x.setCellFactory(TextFieldTableCell.forTableColumn());
    y.setCellFactory(TextFieldTableCell.forTableColumn());
    floor.setCellFactory(TextFieldTableCell.forTableColumn());
    building.setCellFactory(TextFieldTableCell.forTableColumn());
    date.setCellFactory(TextFieldTableCell.forTableColumn());
    //    type.setCellFactory(TextFieldTableCell.forTableColumn()); // type isn't a string, doesn't
    // work with this
    longName.setCellFactory(TextFieldTableCell.forTableColumn());
    shortName.setCellFactory(TextFieldTableCell.forTableColumn());

    id.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setId(t.getNewValue()));
    x.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setX(t.getNewValue()));
    y.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow())).setY(t.getNewValue()));
    floor.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setFloor(t.getNewValue()));
    building.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setBuilding(t.getNewValue()));
    //    date.setOnEditCommit(
    //            (TableColumn.CellEditEvent<Node, String> t) ->
    //                    ( t.getTableView().getItems().get(
    //                            t.getTablePosition().getRow())
    //                    ).setDate(t.getNewValue()) //IMPORTANT: there is no setter for date
    // because it's not a field of Node
    //    );                                         // therefore it won't work with this :/
    //    type.setOnEditCommit( // type isn't a string, doesn't work with this yet
    //            (TableColumn.CellEditEvent<Node, String> t) ->
    //                    ( t.getTableView().getItems().get(
    //                            t.getTablePosition().getRow())
    //                    ).setType(t.getNewValue())
    //    );
    longName.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setLongName(t.getNewValue()));
    shortName.setOnEditCommit(
        (TableColumn.CellEditEvent<Node, String> t) ->
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setShortName(t.getNewValue()));

    backButton.setOnAction(
        event -> {
          Navigation.navigate(Screen.HOME);
        });

    dataEdits = new ArrayList<>();
  }

  public ObservableList<Node> getNodes() {
    String pointX = "1";
    String pointY = "3";
    ObservableList<Node> nodes = FXCollections.observableArrayList();
    // pointX = String.valueOf(node.point.getX());
    // pointY = String.valueOf(node.point.getY());
    nodes.add(
        new Node("38", new Point2D(0.0, 1.0), "floor", "building", NodeType.CONF, "name", "abb"));
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
  public boolean submitRequest(NodeTable nodeTable) {

    boolean result = true;

    for (DataEdit edit : dataEdits) {
      boolean made = nodeTable.queueEdit(edit);

      if (made) {
        // Cell color = white
        dataEdits.remove(edit);
      } else {
        result = false;
      }
    }

    return result;
  }
}
