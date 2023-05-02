package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class PopupController extends AboutMeController {
  @FXML MFXButton back;

  @FXML
  public void initialize() {
    back.setOnMouseClicked(
        event -> {
          try {
            stack.getChildren().clear();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }
}
