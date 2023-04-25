package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FutureMovesController {
  @FXML Text moveInfo;
  @FXML Text moveDate;

  @FXML MFXButton notify;
  public String longname;
  public String sDate;

  public void setData(String longName, String date) {
    moveInfo.setText(longName + " will be moved");
    moveDate.setText(date);
    longname = longName;
    sDate = date;
  }
}
