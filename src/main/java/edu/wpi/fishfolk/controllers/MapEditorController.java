package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.EdgeEdit;import edu.wpi.fishfolk.database.EdgeEditType;import edu.wpi.fishfolk.database.ObservableNode;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.*;
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
  @FXML private TableColumn<ObservableNode, String> date;
  @FXML private TableColumn<ObservableNode, String> edges;
  @FXML MFXButton backButton;

  private HashSet<String> validNodes;
  private ArrayList<DataEdit> dataEdits;
  private ArrayList<EdgeEdit> edgeEdits;

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
    date.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("date"));
    edges.setCellValueFactory(new PropertyValueFactory<ObservableNode, String>("adjacentNodes"));

    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    // important to initialize this before populating the table
    validNodes = new HashSet<>();

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
    date.setCellFactory(TextFieldTableCell.forTableColumn());
    edges.setCellFactory(TextFieldTableCell.forTableColumn());

    x.setOnEditCommit(this::handleEditCommit_X);
    y.setOnEditCommit(this::handleEditCommit_Y);
    floor.setOnEditCommit(this::handleEditCommit_Floor);
    building.setOnEditCommit(this::handleEditCommit_Building);
    type.setOnEditCommit(this::handleEditCommit_Type);
    longName.setOnEditCommit(this::handleEditCommit_LongName);
    shortName.setOnEditCommit(this::handleEditCommit_ShortName);
    date.setOnEditCommit(this::handleEditCommit_Date);
    edges.setOnEditCommit(this::handleEdit_Edges);

    backButton.setOnAction(
        event -> {
          Navigation.navigate(Screen.HOME);
        });

    dataEdits = new ArrayList<>();
    edgeEdits = new ArrayList<>();
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

  private void handleEdit_Edges(TableColumn.CellEditEvent<ObservableNode, String> t) {

    String[] prev = t.getOldValue().split(", ");
    String[] changed = t.getNewValue().split(", ");
    Arrays.sort(prev);
    Arrays.sort(changed);

    t.getTableView().getItems().get(t.getTablePosition().getRow()).adjacentNodes = t.getNewValue();

    // ensure that all nodes in the changed string are valid
    boolean verified = validNodes.containsAll(List.of(changed));

    if (verified) {

      System.out.println("Verified edit to adjacent nodes");

      String start = t.getTableView().getItems().get(t.getTablePosition().getRow()).id;

      // find differences between prev and changed
      // https://stackoverflow.com/questions/3476672/algorithm-to-get-changes-between-two-arrays
      int pIdx = 0, cIdx = 0;
      while(pIdx < prev.length && cIdx < changed.length){

        int comp = prev[pIdx].compareTo(changed[cIdx]);

        if(comp < 0){
          //pidx got removed
          edgeEdits.add(new EdgeEdit(EdgeEditType.REMOVE, start, prev[pIdx]));
          pIdx++;

        } else if (comp > 0){
          //cidx got added
          edgeEdits.add(new EdgeEdit(EdgeEditType.ADD, start, changed[cIdx]));
          cIdx++;

        } else {
          //equal so this edge is unchanged
          pIdx++;
          cIdx++;
        }
      }


      submitEdits();
      System.out.println("Submitted.");
    }
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

      // save valid node ids to make verifying edge edits faster
      validNodes.add(nodes[i].id);
    }

    // each element is an arraylist <str1, str2>
    // first element has the headers <"node1", "node2">
    // each element after that is <"startID", "endID">
    ArrayList<String>[] edgesRaw = dbConnection.edgeTable.getAll();

    for (int i = 1; i < edgesRaw.length; i++) {

      String start = edgesRaw[i].get(0);
      String end = edgesRaw[i].get(1);

      // add end to start's adjacent nodes
      int startIdx = id2idx.get(start);
      observableNodes.get(startIdx).addAdjNode(end);

      // add start to end's adjacent nodes
      int endIdx = id2idx.get(end);
      observableNodes.get(endIdx).addAdjNode(start);
    }

    for (ObservableNode obsNode : observableNodes) {
      obsNode.setAdjacentNodes();
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

    for(EdgeEdit edit : edgeEdits){
      //TODO Christian apply edits to edit table - perhaps execute query?
      //dbConnection.edgeTable.executeQuery
    }

    //    dataEdits.removeIf(
    //        dataEdit ->
    //            dbConnection.nodeTable.update(
    //                dataEdit.getId(), dataEdit.getHeader(), dataEdit.getValue()));
  }
}
