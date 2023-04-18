package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DAO.Observables.ObservableLocation;
import edu.wpi.fishfolk.database.DAO.Observables.ObservableMove;
import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Move;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
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
  @FXML MFXButton clearMove, clearLocation;

  private ObservableList<ObservableMove> observableMoves;

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
            dbConnection.getAllEntries(TableEntryType.MOVE).stream()
                .map(elt -> new ObservableMove((Move) elt))
                .toList());
    movetable.setItems(observableMoves);

    locationtable.setItems(
        FXCollections.observableList(
            dbConnection.getAllEntries(TableEntryType.LOCATION).stream()
                .map(elt -> new ObservableLocation((Location) elt))
                .toList()));

    nodeid.setOnEditCommit(this::editNodeID);

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

    /*submitlocation.setOnMouseClicked(
    event -> {
      Move newMove =
              new Move(
                      Integer.parseInt(nodetext.getText()),
                      movelongnametext.getText(),
                      ObservableMove.parseDate(datetext.getText()));

      observableMoves.addAll(new ObservableMove(newMove));

      dbConnection.insertEntry(newMove);
    });*/

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
}
