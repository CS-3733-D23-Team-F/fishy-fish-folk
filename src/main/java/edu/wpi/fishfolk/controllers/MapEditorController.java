package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MapEditorController {
  @FXML private TableView<Node> table;
  @FXML private TableColumn<Node, String> id;
  @FXML private TableColumn<Node, String> x;
  @FXML private TableColumn<Node, String> y;
  @FXML private TableColumn<Node, String> floor;
  @FXML private TableColumn<Node, String> building;
  @FXML private TableColumn<Node, String> date;
  @FXML private TableColumn<Node, NodeType> type;
  @FXML private TableColumn<Node, String> longName;
  @FXML private TableColumn<Node, String> shortName;

  public void initialize(Node node) {
    // sets up the columns in the table
    id.setCellValueFactory(new PropertyValueFactory<Node, String>("ID"));
    x.setCellValueFactory(new PropertyValueFactory<Node, String>("X"));
    y.setCellValueFactory(new PropertyValueFactory<Node, String>("Y"));
    floor.setCellValueFactory(new PropertyValueFactory<Node, String>("floor"));
    building.setCellValueFactory(new PropertyValueFactory<Node, String>("building"));
    date.setCellValueFactory(new PropertyValueFactory<Node, String>("Date"));
    type.setCellValueFactory(new PropertyValueFactory<Node, NodeType>("type"));
    longName.setCellValueFactory(new PropertyValueFactory<Node, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<Node, String>("shortName"));

    // load data
    table.setItems(getNodes());
  }

  public ObservableList<Node> getNodes() {
    ObservableList<Node> nodes = FXCollections.observableArrayList();
    nodes.add(new Node("id", null, "floor", "building", null, "name", "abb"));
    return nodes;
  }
}
