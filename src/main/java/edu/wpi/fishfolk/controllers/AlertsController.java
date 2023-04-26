package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry.Alert;
import edu.wpi.fishfolk.util.AlertType;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlertsController {
  @FXML Label alertText;
  @FXML MFXButton closeAlert;

  public void setData(Alert alert) {

    if (alert.getType() == AlertType.MOVE) {
      alertText.setText(alert.getLongName() + " will get moved on " + alert.getDate());

    } else if (alert.getType() == AlertType.OTHER) {
      alertText.setText(alert.getText());
    }
  }
}
