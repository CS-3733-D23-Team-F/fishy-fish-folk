package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.DataEdit;
import edu.wpi.fishfolk.database.EdgeEdit;
import edu.wpi.fishfolk.database.EdgeEditType;
import edu.wpi.fishfolk.database.ObservableNode;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

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
  @FXML MFXButton importCSVButton;
  @FXML MFXButton exportCSVButton;

  FileChooser fileChooser;
  DirectoryChooser dirChooser;

  private HashMap<String, Integer> id2row;

  private ArrayList<DataEdit> dataEdits;
  private ArrayList<EdgeEdit> edgeEdits;

  // set of nodes that are allowed as edge edits
  private HashSet<String> validNodes;
  private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");

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

    validNodes = new HashSet<>();
    id2row = new HashMap<>();
    // load data
    populateTable(getNodes());

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

    importCSVButton.setOnAction(
        event -> {
          fileChooser.setTitle("Select the Node CSV file");
          String microNodePath =
              fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Location CSV file");
          String locationPath =
              fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Move CSV file");
          String movePath = fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          fileChooser.setTitle("Select the Edge CSV file");
          String edgePath = fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();

          dbConnection.nodeTable.importCSV(microNodePath, locationPath, movePath, false);
          dbConnection.edgeTable.importCSV(edgePath, false);
          table.getItems().clear();
          initialize();
        });

    exportCSVButton.setOnAction(
        event -> {
          dirChooser.setTitle("Select Export Directory");
          String exportPath = dirChooser.showDialog(Fapp.getPrimaryStage()).getAbsolutePath();
          dbConnection.nodeTable.exportCSV(exportPath, exportPath, exportPath);
          dbConnection.edgeTable.exportCSV(exportPath);
          fileChooser.setInitialDirectory(new File(exportPath));
        });

    dataEdits = new ArrayList<>();
    edgeEdits = new ArrayList<>();

    fileChooser = new FileChooser();
    dirChooser = new DirectoryChooser();
  }

  public void handleEditCommit_X(TableColumn.CellEditEvent<ObservableNode, String> t) {

    boolean verified = true;

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());
    // check value is a valid double
    try {
      Double.parseDouble(t.getNewValue());
    } catch (NumberFormatException e) {
      verified = false;
    }

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).x = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.id, "x", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_X]: Successful update made to column X.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).x =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
  }

  private void handleEditCommit_Y(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
    boolean verified = true;

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());
    // check value is a valid double
    try {
      Double.parseDouble(t.getNewValue());
    } catch (NumberFormatException e) {
      verified = false;
    }

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).y = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.id, "y", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_y]: Successful update made to column y.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).y =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
  }

  private void handleEditCommit_Floor(TableColumn.CellEditEvent<ObservableNode, String> t) {

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    // nothing to verify for strings
    boolean verified = t.getNewValue().length() < 3;

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).floor = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.floor, "floor", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_floor]: Successful update made to column floor.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).floor =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
  }

  private void handleEditCommit_Building(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    t.getTableView().getItems().get(t.getTablePosition().getRow()).building = t.getNewValue();
    t.getTableView().refresh();
    DataEdit edit = new DataEdit(node.building, "building", t.getNewValue());
    dataEdits.add(edit);
    submitEdits();
    System.out.println(
        "[MapEditorController.handleEditCommit_building]: Successful update made to column building.");
  }

  private void handleEditCommit_Type(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
    boolean verified = true;

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    try {
      // Double.parseDouble(t.getNewValue());
      NodeType.valueOf(t.getNewValue());
    } catch (IllegalArgumentException e) {
      verified = false;
    }

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).type = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.id, "type", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_type]: Successful update made to column type.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).type =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
  }

  private void handleEditCommit_LongName(TableColumn.CellEditEvent<ObservableNode, String> t) {

    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());

    // nothing to verify for strings
    boolean verified = t.getNewValue().length() > 5;

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).longName = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.longName, "longName", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_longName]: Successful update made to column longName.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).longName =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }

    // removeAnyOldCommits(nodeid, header); // not strictly necessary
  }

  private void handleEditCommit_ShortName(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());
    // check value is a valid date structure

    boolean verified = t.getNewValue().length() < 25;

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).shortName = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.shortName, "shortName", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_shortName]: Successful update made to column shortName.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).shortName =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
  }

  private void handleEditCommit_Date(TableColumn.CellEditEvent<ObservableNode, String> t) {
    // t.getTableView().getItems().get(t.getTablePosition().getRow()) //node that was changed
    // t.getNewValue(); // new string value of cell
    ObservableNode node = t.getTableView().getItems().get(t.getTablePosition().getRow());
    // check value is a valid date structure

    boolean verified = DATE_PATTERN.matcher(t.getNewValue()).matches();

    if (verified) {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).date = t.getNewValue();
      t.getTableView().refresh();
      DataEdit edit = new DataEdit(node.id, "date", t.getNewValue());
      dataEdits.add(edit);
      submitEdits();
      System.out.println(
          "[MapEditorController.handleEditCommit_date]: Successful update made to column date.");
    } else {
      t.getTableView().getItems().get(t.getTablePosition().getRow()).date =
          "**" + t.getOldValue() + "**";
      t.getTableView().refresh();
    }
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
      while (pIdx < prev.length && cIdx < changed.length) {

        int comp = prev[pIdx].compareTo(changed[cIdx]);

        if (comp < 0) {
          // pidx got removed
          edgeEdits.add(new EdgeEdit(EdgeEditType.REMOVE, start, prev[pIdx]));
          pIdx++;

        } else if (comp > 0) {
          // cidx got added
          edgeEdits.add(new EdgeEdit(EdgeEditType.ADD, start, changed[cIdx]));
          cIdx++;

        } else {
          // equal so this edge is unchanged
          pIdx++;
          cIdx++;
        }
      }

      submitEdits();
      System.out.println("Submitted.");
    }
  }

  public static void sort(ObservableList<ObservableNode> list) {
    list.sort((o1, o2) -> (o1.getSortable()).compareTo(o2.getSortable()));
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

      // record in id2row
      id2row.put(nodes[i].id, i);
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

    sort(observableNodes);

    return observableNodes;
  }

  public void populateTable(ObservableList<ObservableNode> nodes) {
    table.setItems(nodes);
  }

  /**
   * Request NodeTable to queue edits to database. On each edit's success: 1. Make the BG of the
   * cell white, 2. Remove DataEdit from collection.
   */
  public void submitEdits() {

    // trackChanges();

    dataEdits.removeIf(edit -> dbConnection.nodeTable.update(edit.id, edit.attr, edit.value));

    for (EdgeEdit edit : edgeEdits) {

      switch (edit.type) {
        case ADD:
          dbConnection.edgeTable.insert(new ArrayList<>(List.of(edit.node1, edit.node2)));

          table.getItems().get(id2row.get(edit.node1)).addAdjNode(edit.node2);

          table.getItems().get(id2row.get(edit.node2)).addAdjNode(edit.node1);

          break;

        case REMOVE:
          table.getItems().get(id2row.get(edit.node1)).removeAdjNode(edit.node2);

          table.getItems().get(id2row.get(edit.node2)).removeAdjNode(edit.node1);

          /*
          EXAMPLE QUERY:

          DELETE FROM proto2db.edge
          WHERE (proto2db.edge.node1 = '<node1>' AND proto2db.edge.node2 = '<node2>')
          OR (proto2db.edge.node1 = '<node2>' AND proto2db.edge.node2 = '<node1>');
           */

          try {
            String query =
                "DELETE FROM "
                    + dbConnection.conn.getSchema()
                    + "."
                    + dbConnection.edgeTable.getTableName()
                    + " WHERE ("
                    + dbConnection.conn.getSchema()
                    + "."
                    + dbConnection.edgeTable.getTableName()
                    + ".node1 = '"
                    + edit.node1
                    + "' AND "
                    + dbConnection.conn.getSchema()
                    + "."
                    + dbConnection.edgeTable.getTableName()
                    + ".node2 = '"
                    + edit.node2
                    + "') OR ("
                    + dbConnection.conn.getSchema()
                    + "."
                    + dbConnection.edgeTable.getTableName()
                    + ".node1 = '"
                    + edit.node2
                    + "' AND "
                    + dbConnection.conn.getSchema()
                    + "."
                    + dbConnection.edgeTable.getTableName()
                    + ".node2 = '"
                    + edit.node1
                    + "');";

            Statement statement = dbConnection.conn.createStatement();
            statement.executeUpdate(query);

          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
          break;
      }
    }
    table.refresh();
  }
}
