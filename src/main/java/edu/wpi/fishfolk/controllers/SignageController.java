package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.navigation.Navigation;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class SignageController {
  @FXML MFXButton backButton;

  @FXML
  public void initialize() {

    backButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
  }
}
