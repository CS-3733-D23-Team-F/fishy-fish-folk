package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class NewConferenceController extends AbsController {
  @FXML MFXButton confClearButton;
  @FXML MFXButton confCancelButton;
  @FXML MFXButton confSubmitButton;
  @FXML MFXFilterComboBox startTimeDrop;
  @FXML MFXFilterComboBox endTimeDrop;
  @FXML MFXFilterComboBox startAMPMDrop;
  @FXML MFXFilterComboBox endAMPMDrop;
  @FXML MFXFilterComboBox recurringDrop;
  @FXML MFXTextField numAttnBox;
  @FXML MFXTextField notesBox;
  @FXML MFXTextField nameBox;

  public NewConferenceController() {
    super();
  }

  @FXML
  public void initialize() {
    addDropdownOptions();
    confCancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    confClearButton.setOnMouseClicked(event -> clearFields());
  }

  public void clearFields() {
    startTimeDrop.setValue("TIME");
    startAMPMDrop.setValue("AM/PM");
    endAMPMDrop.setValue("AM/PM");
    endTimeDrop.setValue("TIME");
    recurringDrop.setValue(null);
    numAttnBox.clear();
    notesBox.clear();
    nameBox.clear();
  }

  /** Initializing the dropdowns so they have all the required options. */
  public void addDropdownOptions() {
    // Dropdown options for the AM/PM box on start time
    startAMPMDrop.getItems().add("AM");
    startAMPMDrop.getItems().add("PM");

    // Dropdown options for the AM/PM box on end time
    endAMPMDrop.getItems().add("AM");
    endAMPMDrop.getItems().add("PM");

    // Dropdown options for the time box on start time
    startTimeDrop.getItems().add("1:00");
    startTimeDrop.getItems().add("2:00");
    startTimeDrop.getItems().add("3:00");
    startTimeDrop.getItems().add("4:00");
    startTimeDrop.getItems().add("5:00");
    startTimeDrop.getItems().add("6:00");
    startTimeDrop.getItems().add("7:00");
    startTimeDrop.getItems().add("8:00");
    startTimeDrop.getItems().add("9:00");
    startTimeDrop.getItems().add("10:00");
    startTimeDrop.getItems().add("11:00");
    startTimeDrop.getItems().add("12:00");

    // Dropdown options for the time box on end time
    endTimeDrop.getItems().add("1:00");
    endTimeDrop.getItems().add("2:00");
    endTimeDrop.getItems().add("3:00");
    endTimeDrop.getItems().add("4:00");
    endTimeDrop.getItems().add("5:00");
    endTimeDrop.getItems().add("6:00");
    endTimeDrop.getItems().add("7:00");
    endTimeDrop.getItems().add("8:00");
    endTimeDrop.getItems().add("9:00");
    endTimeDrop.getItems().add("10:00");
    endTimeDrop.getItems().add("11:00");
    endTimeDrop.getItems().add("12:00");
  }
}
