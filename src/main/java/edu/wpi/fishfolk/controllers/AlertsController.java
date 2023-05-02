package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry.Alert;
import edu.wpi.fishfolk.util.AlertType;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlertsController {
  @FXML Label alertText, alertUsername, alertTime;
  @FXML MFXButton closeAlert;

  public void setData(Alert alert) {

    alertUsername.setText(alert.getUsername());
    if (LocalDate.now().equals(alert.getTimestamp().toLocalDate())) {

      alertTime.setText(
          "Today at " + alert.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")));

    } else {

      alertTime.setText(
          alert.getTimestamp().toLocalDate()
              + " "
              + alert.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    if (alert.getType() == AlertType.MOVE) {

      alertText.setText(alert.getLongName() + " will get moved on " + alert.getDate());

    } else if (alert.getType() == AlertType.OTHER) {

      alertText.setText(alert.getText());
    }
  }
}
