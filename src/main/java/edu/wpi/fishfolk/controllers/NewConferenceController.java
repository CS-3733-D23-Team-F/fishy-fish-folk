package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class NewConferenceController extends AbsController {
  @FXML MFXButton confClearButton;
  @FXML MFXButton confCancelButton;
  @FXML MFXButton confSubmitButton;

  public NewConferenceController() {
    super();
  }

  @FXML
  public void initialize() {

    confCancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    confClearButton.setOnMouseClicked(event -> clearFields());
  }

  public void clearFields(){

  }
}
