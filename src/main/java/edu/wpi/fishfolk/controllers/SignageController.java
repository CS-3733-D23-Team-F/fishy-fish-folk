package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class SignageController {
  @FXML MFXButton backButton;

  @FXML
  public void initialize() {

    backButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
  }
}
