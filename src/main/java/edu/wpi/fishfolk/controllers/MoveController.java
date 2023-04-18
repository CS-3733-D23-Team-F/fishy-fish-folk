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

public class MoveController extends AbsController {

  @FXML TableView<ObservableMove> movetable;
  @FXML TableColumn<ObservableMove, Integer> nodeid;
  @FXML TableColumn<ObservableMove, String> movelongname, date;

  @FXML TableView<ObservableLocation> locationtable;
  @FXML TableColumn<ObservableLocation, String> locationlongname, shortname, type;
  @FXML MFXTextField nodetext, movelongnametext, datetext;
  @FXML MFXButton submitmove;

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

    submitmove.setOnMouseClicked(
        event -> {
          Move newMove =
              new Move(
                  Integer.parseInt(nodetext.getText()),
                  movelongnametext.getText(),
                  ObservableMove.parseDate(datetext.getText()));

          observableMoves.addAll(new ObservableMove(newMove));

          dbConnection.insertEntry(newMove);
        });
  }
}
