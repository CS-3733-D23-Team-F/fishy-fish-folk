package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Move;
import edu.wpi.fishfolk.database.TableEntry.Node;
import edu.wpi.fishfolk.util.NodeType;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.time.LocalDate;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

public class MapEditorLocationController {

  @FXML MFXTextField longnameText, shortnameText, nodeIDText;
  @FXML MFXComboBox<NodeType> type;
  @FXML MFXDatePicker datePicker;
  @FXML MFXButton preview, delete, submit;

  @Getter @Setter Node origin;

  @Getter @Setter private int nodeID;
  @Getter @Setter private Location location;
  @Getter @Setter private LocalDate date;

  @Getter @Setter private boolean locationEdited = false;
  @Getter @Setter private boolean moveEdited = false;

  @Getter @Setter private boolean newMove = false;

  @FXML
  private void initialize() {

    preview.setDisable(true);
    submit.setDisable(true);

    type.getItems().addAll(MapEditorController.observableNodeTypes);

    shortnameText.setOnAction(
        event -> {
          locationEdited = true;
          updateButtons();
        });

    type.setOnAction(
        event -> {
          locationEdited = true;
          updateButtons();
        });

    nodeIDText.setOnAction(
        event -> {
          if (nodeIDText.getText().isEmpty()) {
            newMove = false;
          } else {

            if (MapEditorController.validateNodeID(Integer.parseInt(nodeIDText.getText()))) {
              newMove = true;
              moveEdited = false;
            }
          }
          updateButtons();
        });

    datePicker.setOnAction(
        event -> {

          // user changes date has two cases
          // if the node id is set, this is a new move
          // if the node is not set, this is updating the current move
          if (MapEditorController.validateDate(datePicker.getValue())) {
            moveEdited = !newMove;
          }
          updateButtons();
        });
  }

  public void setData(Location location, LocalDate date) {

    this.location = location;
    this.date = date;

    longnameText.setText(location.getLongName());

    shortnameText.setText(location.getShortName());

    type.setValue(location.getNodeType());
    type.setText(location.getNodeType().toString());

    datePicker.setValue(date);
  }

  public void acceptEdits() {
    location.setShortName(shortnameText.getText());
    location.setNodeType(type.getSelectedItem());
  }

  public void readNewDate() {
    date = datePicker.getValue();
  }

  public void readNodeID() {
    if (!nodeIDText.getText().isEmpty()) {
      nodeID = Integer.parseInt(nodeIDText.getText());
    }
  }

  public void readNewMove() {
    if (!nodeIDText.getText().isEmpty()) {
      nodeID = Integer.parseInt(nodeIDText.getText());
    }

    date = datePicker.getValue();
  }

  public Move getMove() {
    return new Move(nodeID, location.getLongName(), date);
  }

  private void updateButtons() {

    if (newMove) {
      preview.setDisable(false);
    }

    if (newMove || moveEdited || locationEdited) {
      submit.setDisable(false);
    }
  }
}
