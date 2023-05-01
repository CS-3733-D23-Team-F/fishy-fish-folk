package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry.Location;
import edu.wpi.fishfolk.database.TableEntry.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.time.LocalDate;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

public class MapEditorLocationController {

  @FXML MFXTextField longnameText, shortnameText, typeText, nodeIDText;
  @FXML MFXDatePicker datePicker;
  @FXML MFXButton preview, submit;

  @Getter @Setter private boolean previewing = false;
  @Getter @Setter Node origin;
  private boolean validNodeID = false, validDate = false;
  int nodeID;
  LocalDate date;

  @FXML
  private void initialize() {

    preview.setDisable(true);
    submit.setDisable(true);

    nodeIDText.setOnAction(
        event -> {
          int nid = Integer.parseInt(nodeIDText.getText());
          validNodeID = MapEditorController.validateNodeID(nid);
          if (validNodeID) {
            nodeID = nid;
          }
          System.out.println(nid + " valid? " + validNodeID);
          updateButtons();
        });

    datePicker.setOnAction(
        event -> {
          LocalDate d = datePicker.getValue();
          validDate = MapEditorController.validateDate(d);
          if (validDate) {
            date = d;
          }
          System.out.println(d + " valid? " + validDate);
          updateButtons();
        });
  }

  public void setData(Location location) {

    shortnameText.setText(location.getShortName());
    typeText.setText(location.getNodeType().toString());
    longnameText.setText(location.getLongName());
  }

  private void updateButtons() {
    if (validNodeID) {
      preview.setDisable(false);
      if (validDate) {
        submit.setDisable(false);
      }
    }
  }
}
