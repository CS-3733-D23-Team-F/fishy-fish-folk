package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class SupplyRequestController {
  @FXML MFXButton cancelButton;
  @FXML MFXButton supplySubmitButton;

  @FXML
  public void initialize() {
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    supplySubmitButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
  }
}
