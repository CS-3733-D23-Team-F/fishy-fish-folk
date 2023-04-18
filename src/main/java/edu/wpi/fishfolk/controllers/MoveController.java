package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.database.DataTableType.LOCATION;
import static edu.wpi.fishfolk.database.DataTableType.MOVE;

import edu.wpi.fishfolk.database.edit.InsertEdit;
import edu.wpi.fishfolk.database.edit.RemoveEdit;
import edu.wpi.fishfolk.database.edit.UpdateEdit;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;

public class MoveController extends AbsController {
  @FXML MFXButton submit;
  @FXML MFXButton cancel;
  @FXML MFXButton delete;
  @FXML MFXButton Update;
  @FXML MFXButton submitMove;
  @FXML MFXButton cancelMove;
  @FXML MFXButton deleteMove;
  @FXML MFXButton UpdateMove;
  @FXML MFXTextField longname;
  @FXML MFXTextField shortname;
  @FXML MFXTextField type;
  @FXML MFXTextField longnameMove;
  @FXML MFXTextField ID;
  @FXML MFXDatePicker date;

  public MoveController() {
    super();
  }

  @FXML
  private void initialize() {
    submit.setOnMouseClicked(
        event -> insertToDatabase(longname.getText(), shortname.getText(), type.getText()));
    cancel.setOnMouseClicked(event -> clear());
    delete.setOnMouseClicked(event -> RemoveToDatabase(longname.getText()));
    Update.setOnMouseClicked(
        event -> UpdateToDatabase(longname.getText(), shortname.getText(), type.getText()));
    submitMove.setOnMouseClicked(
        event -> insertToDatabaseMove(ID.getText(), longnameMove.getText(), date.getText()));
    cancelMove.setOnMouseClicked(event -> clearMove());
    deleteMove.setOnMouseClicked(event -> RemoveToDatabaseMove(ID.getText()));
    UpdateMove.setOnMouseClicked(
        event -> UpdateToDatabaseMove(ID.getText(), longnameMove.getText(), date.getText()));
  }

  private void insertToDatabase(String longname, String shortname, String Type) {
    ArrayList<String> data = new ArrayList<>(List.of(longname, shortname, Type));
    InsertEdit insert = new InsertEdit(LOCATION, "longname", longname, data);
    dbConnection.processEdit(insert);
  }

  private void insertToDatabaseMove(String id, String longname, String date) {
    ArrayList<String> data = new ArrayList<>(List.of(id, longname, date));
    InsertEdit insert = new InsertEdit(MOVE, "longname", longname, data);
    dbConnection.processEdit(insert);
  }

  private void UpdateToDatabase(String longname, String shortname, String Type) {
    UpdateEdit updateShort = new UpdateEdit(LOCATION, "longname", longname, "shortname", shortname);
    UpdateEdit updateType = new UpdateEdit(LOCATION, "longname", longname, "type", Type);

    dbConnection.processEdit(updateShort);
    dbConnection.processEdit(updateType);
  }

  private void UpdateToDatabaseMove(String id, String longname, String date) {
    UpdateEdit updateName = new UpdateEdit(MOVE, "ID", id, "longname", longname);
    UpdateEdit updateDate = new UpdateEdit(MOVE, "ID", id, "date", date);

    dbConnection.processEdit(updateName);
    dbConnection.processEdit(updateDate);
  }

  private void RemoveToDatabase(String longname) {
    RemoveEdit removeName = new RemoveEdit(LOCATION, "longname", longname);
    dbConnection.processEdit(removeName);
  }

  private void RemoveToDatabaseMove(String id) {
    RemoveEdit removeName = new RemoveEdit(MOVE, "id", id);
    dbConnection.processEdit(removeName);
  }

  private void clear() {
    longname.setText("");
    shortname.setText("");
    type.setText("");
  }

  private void clearMove() {
    longnameMove.setText("");
    ID.setText("");
    date.setText("");
  }
}
