package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.database.TableEntry.TableEntryType.LOCATION;
import static edu.wpi.fishfolk.database.TableEntry.TableEntryType.MOVE;

import edu.wpi.fishfolk.database.DAO.Observables.ObservableLocation;
import edu.wpi.fishfolk.database.DAO.Observables.ObservableMove;
import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Move;
import edu.wpi.fishfolk.pathfinding.NodeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class MoveController extends AbsController {

  @FXML TableView<ObservableMove> movetable;
  @FXML TableColumn<ObservableMove, String> nodeid, movelongname, date;

  @FXML TableView<ObservableLocation> locationtable;
  @FXML TableColumn<ObservableLocation, String> locationlongname, shortname, type;
  @FXML MFXTextField nodetext, movelongnametext, datetext;
  @FXML MFXTextField locationlongnametext, shortnametext, typetext;
  @FXML MFXButton submitmove, submitlocation;
  @FXML MFXButton deletemove, deletelocation;
  @FXML MFXButton clearMove, clearLocation;

  private ObservableList<ObservableMove> observableMoves;
  private ObservableList<ObservableLocation> observableLocations;

  public MoveController() {
    super();
  }

  @FXML
  private void initialize() {

    nodeid.setCellValueFactory(new PropertyValueFactory<>("nodeid"));
    movelongname.setCellValueFactory(new PropertyValueFactory<>("longname"));
    date.setCellValueFactory(new PropertyValueFactory<>("date"));

    locationlongname.setCellValueFactory(new PropertyValueFactory<>("longname"));
    shortname.setCellValueFactory(new PropertyValueFactory<>("shortname"));
    type.setCellValueFactory(new PropertyValueFactory<>("type"));

    // make each cell editable
    nodeid.setCellFactory(TextFieldTableCell.forTableColumn());
    movelongname.setCellFactory(TextFieldTableCell.forTableColumn());
    date.setCellFactory(TextFieldTableCell.forTableColumn());
    locationlongname.setCellFactory(TextFieldTableCell.forTableColumn());
    shortname.setCellFactory(TextFieldTableCell.forTableColumn());
    type.setCellFactory(TextFieldTableCell.forTableColumn());

    observableMoves =
        FXCollections.observableArrayList(
            dbConnection.getAllEntries(MOVE).stream()
                .map(elt -> new ObservableMove((Move) elt))
                .toList());

    movetable.setItems(observableMoves);

    observableLocations =
        FXCollections.observableArrayList(
            dbConnection.getAllEntries(LOCATION).stream()
                .map(elt -> new ObservableLocation((Location) elt))
                .toList());

    locationtable.setItems(observableLocations);

    nodeid.setOnEditCommit(this::editNodeID);
    movelongname.setOnEditCommit(this::editMoveLongname);
    date.setOnEditCommit(this::editDate);

    locationlongname.setOnEditCommit(this::editLocationLongname);
    shortname.setOnEditCommit(this::editShortname);
    type.setOnEditCommit(this::editType);

    submitmove.setOnMouseClicked(
        event -> {
          Move newMove =
              new Move(
                  Integer.parseInt(nodetext.getText()),
                  movelongnametext.getText(),
                  ObservableMove.parseDate(datetext.getText()));

          // for some reason .add doesnt work, just use .addAll(... elements)
          observableMoves.addAll(new ObservableMove(newMove));

          System.out.println(newMove + " " + dbConnection.insertEntry(newMove));
        });

    submitlocation.setOnMouseClicked(
        event -> {
          Location newLocation =
              new Location(
                  locationlongnametext.getText(),
                  shortnametext.getText(),
                  NodeType.valueOf(typetext.getText()));

          observableLocations.addAll(new ObservableLocation(newLocation));

          dbConnection.insertEntry(newLocation);
        });

    deletemove.setOnMouseClicked(
        event -> {
          Move newMove =
              new Move(
                  Integer.parseInt(nodetext.getText()),
                  movelongnametext.getText(),
                  ObservableMove.parseDate(datetext.getText()));

          // for some reason .remove doesnt work, just use .removeAll(... elements)
          observableMoves.removeAll(new ObservableMove(newMove));

          System.out.println(newMove + " " + dbConnection.removeEntry(newMove, MOVE));
        });

    deletelocation.setOnMouseClicked(
        event -> {
          Location newLocation =
              new Location(
                  locationlongnametext.getText(),
                  shortnametext.getText(),
                  NodeType.valueOf(typetext.getText()));

          observableLocations.removeAll(new ObservableLocation(newLocation));

          dbConnection.removeEntry(newLocation, LOCATION);
        });

    clearMove.setOnMouseClicked(event -> MoveClear());
    clearLocation.setOnMouseClicked(event -> LocationClear());
  }

  private void MoveClear() {
    nodetext.setText("");
    movelongnametext.setText("");
    datetext.setText("");
  }

  private void LocationClear() {
    locationlongnametext.setText("");
    shortnametext.setText("");
    typetext.setText("");
  }

  private void editNodeID(TableColumn.CellEditEvent<ObservableMove, String> event) {

    String newnodeID = event.getNewValue();
    String oldlongname = movelongname.getCellObservableValue(event.getRowValue()).getValue();
    String olddate = date.getCellObservableValue(event.getRowValue()).getValue();

    observableMoves.removeIf(obsmove -> obsmove.matches(oldlongname, olddate));

    Move newMove =
        new Move(Integer.parseInt(newnodeID), oldlongname, ObservableMove.parseDate(olddate));
    observableMoves.addAll(new ObservableMove(newMove));

    System.out.println(newMove + " " + dbConnection.updateEntry(newMove));
  }

  private void editMoveLongname(TableColumn.CellEditEvent<ObservableMove, String> event) {

    String oldnodeID = nodeid.getCellObservableValue(event.getRowValue()).getValue();
    String oldlongname = event.getOldValue();
    String newlongname = event.getNewValue();
    String olddate = date.getCellObservableValue(event.getRowValue()).getValue();

    observableMoves.removeIf(obsmove -> obsmove.matches(oldlongname, olddate));
    dbConnection.removeEntry(oldlongname + ObservableMove.parseDate(olddate), MOVE);

    Move newMove =
        new Move(Integer.parseInt(oldnodeID), newlongname, ObservableMove.parseDate(olddate));
    observableMoves.addAll(new ObservableMove(newMove));

    dbConnection.insertEntry(newMove);
  }

  private void editDate(TableColumn.CellEditEvent<ObservableMove, String> event) {

    String oldnodeID = nodeid.getCellObservableValue(event.getRowValue()).getValue();
    String oldlongname = movelongname.getCellObservableValue(event.getRowValue()).getValue();
    String olddate = event.getOldValue();
    String newdate = event.getNewValue();

    observableMoves.removeIf(obsmove -> obsmove.matches(oldlongname, olddate));
    dbConnection.removeEntry(oldlongname + ObservableMove.parseDate(olddate), MOVE);

    Move newMove =
        new Move(Integer.parseInt(oldnodeID), oldlongname, ObservableMove.parseDate(newdate));
    observableMoves.addAll(new ObservableMove(newMove));

    dbConnection.insertEntry(newMove);
  }

  private void editLocationLongname(TableColumn.CellEditEvent<ObservableLocation, String> event) {

    String oldlongname = event.getOldValue();
    String newlongname = event.getNewValue();
    String oldshortname = shortname.getCellObservableValue(event.getRowValue()).getValue();
    String oldtype = type.getCellObservableValue(event.getRowValue()).getValue();

    observableLocations.removeIf(obsloc -> obsloc.getLongname().equals(oldlongname));
    dbConnection.removeEntry(oldlongname, LOCATION);

    Location newLocation = new Location(newlongname, oldshortname, NodeType.valueOf(oldtype));
    observableLocations.addAll(new ObservableLocation(newLocation));

    dbConnection.insertEntry(newLocation);
  }

  private void editShortname(TableColumn.CellEditEvent<ObservableLocation, String> event) {

    String oldlongname = locationlongname.getCellObservableValue(event.getRowValue()).getValue();
    // String oldshortname = event.getOldValue();
    String newshortname = event.getNewValue();
    String oldtype = type.getCellObservableValue(event.getRowValue()).getValue();

    observableLocations.removeIf(obsloc -> obsloc.getLongname().equals(oldlongname));
    dbConnection.removeEntry(oldlongname, LOCATION);

    Location newLocation = new Location(oldlongname, newshortname, NodeType.valueOf(oldtype));
    observableLocations.addAll(new ObservableLocation(newLocation));

    dbConnection.insertEntry(newLocation);
  }

  private void editType(TableColumn.CellEditEvent<ObservableLocation, String> event) {

    String oldlongname = locationlongname.getCellObservableValue(event.getRowValue()).getValue();
    String oldshortname = shortname.getCellObservableValue(event.getRowValue()).getValue();
    // String oldtype = event.getOldValue();
    String newtype = event.getNewValue();

    observableLocations.removeIf(obsloc -> obsloc.getLongname().equals(oldlongname));
    dbConnection.removeEntry(oldlongname, LOCATION);

    Location newLocation = new Location(oldlongname, oldshortname, NodeType.valueOf(newtype));
    observableLocations.addAll(new ObservableLocation(newLocation));

    dbConnection.insertEntry(newLocation);
  }
}
