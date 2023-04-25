package edu.wpi.fishfolk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlertsController {
  @FXML Label moveStart, alertDate;

  public void setData(String longName, String date) {
    moveStart.setText(longName);
    alertDate.setText(date);
  }
}
