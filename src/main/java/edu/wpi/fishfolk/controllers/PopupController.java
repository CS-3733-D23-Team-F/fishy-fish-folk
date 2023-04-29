package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class PopupController {
  @FXML MFXButton back;

  @FXML
  public void initialize() {
    back.setOnMouseClicked(event -> Navigation.navigate(Screen.ABOUTME));
  }
}
