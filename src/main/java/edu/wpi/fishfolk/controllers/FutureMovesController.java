package edu.wpi.fishfolk.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FutureMovesController {
  @FXML Text moveInfo;
  @FXML Text moveDate;

  public void setData(String longName, String date) {
    moveInfo.setText(longName + " -> " + longName);
    moveDate.setText(date);
  }
}
