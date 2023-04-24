package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.Recurring;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import javafx.fxml.FXML;

public class NewConferenceController extends AbsController {
  @FXML MFXButton confClearButton, confCancelButton, confSubmitButton;
  @FXML MFXFilterComboBox startTimeDrop, endTimeDrop, startAMPMDrop, endAMPMDrop, recurringDrop;
  @FXML MFXTextField numAttnBox, notesBox, nameBox;
  @FXML MFXRectangleToggleNode rec1;

  ArrayList<String> AMPM = new ArrayList<>();
  ArrayList<String> times = new ArrayList<>();
  ArrayList<Recurring> recurring = new ArrayList<>();

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
    startTimeDrop.setValue(null);
    startAMPMDrop.setValue(null);
    endAMPMDrop.setValue(null);
    endTimeDrop.setValue(null);
    recurringDrop.setValue(null);
    numAttnBox.clear();
    notesBox.clear();
    nameBox.clear();
  }

  /** Initializing the dropdowns so they have all the required options. */
  public void addDropdownOptions() {

    // Dropdown options getting added to an arraylist called AMPM
    AMPM.add("AM");
    AMPM.add("PM");

    // Adding the arraylist of AM or PM to the dropdowns so they can be selected
    startAMPMDrop.getItems().addAll(AMPM);
    endAMPMDrop.getItems().addAll(AMPM);

    // Dropdown options getting added to an arraylist called times
    times.add("1:00");
    times.add("2:00");
    times.add("3:00");
    times.add("4:00");
    times.add("5:00");
    times.add("6:00");
    times.add("7:00");
    times.add("8:00");
    times.add("9:00");
    times.add("10:00");
    times.add("11:00");
    times.add("12:00");

    // Adding the arraylist of times to the dropdowns so they can be selected
    startTimeDrop.getItems().addAll(times);
    endTimeDrop.getItems().addAll(times);

    // Adding all possible recurring options to an arraylist
    recurring.add(Recurring.NEVER);
    recurring.add(Recurring.DAILY);
    recurring.add(Recurring.WEEKLY);
    recurring.add(Recurring.MONTHLY);
    recurring.add(Recurring.YEARLY);

    // Adding the arraylist to the dropdown box
    recurringDrop.getItems().addAll(recurring);
  }
}
