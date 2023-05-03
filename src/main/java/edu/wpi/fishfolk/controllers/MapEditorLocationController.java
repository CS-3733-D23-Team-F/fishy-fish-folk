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
  private boolean validNodeID = false, validDate = false;

  @Getter @Setter private int nodeID;
  @Getter @Setter private Location location;
  @Getter @Setter private LocalDate date;

  @Getter @Setter private boolean locationEdited = false;
  @Getter @Setter private boolean moveEdited = false;

  @FXML
  private void initialize() {

    preview.setDisable(true);
    submit.setDisable(true);

    type.getItems().addAll(MapEditorController.observableNodeTypes);

    shortnameText.setOnAction(
        event -> {
          location.setShortName(shortnameText.getText());
          locationEdited = true;
          updateButtons();
        });

    type.setOnAction(
        event -> {
          location.setNodeType(type.getSelectedItem());
          locationEdited = true;
          updateButtons();
        });

    nodeIDText.setOnAction(
        event -> {
          int nid = Integer.parseInt(nodeIDText.getText());
          validNodeID = MapEditorController.validateNodeID(nid);
          if (validNodeID) {
            nodeID = nid;
            moveEdited = true;
          }
          updateButtons();
        });

    datePicker.setOnAction(
        event -> {
          LocalDate d = datePicker.getValue();
          validDate = MapEditorController.validateDate(d);
          if (validDate) {
            date = d;
            moveEdited = true;
          }
          updateButtons();
        });
  }

  public void setData(Location location, LocalDate date) {

    this.location = location;
    this.date = date;

    System.out.println("trying to set location " + location);

    longnameText.setText(location.getLongName());

    shortnameText.setText(location.getShortName());
    type.setValue(location.getNodeType());

    datePicker.setValue(date);
  }

  public Move getMove() {
    return new Move(nodeID, location.getLongName(), date);
  }

  private void updateButtons() {

    if (validNodeID) {
      preview.setDisable(false);
    }
    if (validNodeID && validDate) {
      submit.setDisable(false);
    }

    if (locationEdited) {
      submit.setDisable(false);
    }
  }
}
