package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlertsController {
  @FXML Label alert;
  @FXML MFXButton closeAlert;

  public void setData(String longName, String date) {
    alert.setText(longName + "will be moved on " + date);
  }

  public void setData(String alerts) {
    alert.setText(alerts);
  }
}
