package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.ConferenceRequest;
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
  @FXML MFXTextField numAttnBox, nameBox, notesBox;
  @FXML MFXRectangleToggleNode rec1, rec2, rec3, rec4, rec5, rec6, rec7;

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
    confSubmitButton.setOnMouseClicked(event -> attemptSubmit());

    rec1.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec6, rec7));
    rec2.setOnMouseClicked(event -> deselect(rec1, rec3, rec4, rec5, rec6, rec7));
    rec3.setOnMouseClicked(event -> deselect(rec2, rec1, rec4, rec5, rec6, rec7));
    rec4.setOnMouseClicked(event -> deselect(rec2, rec3, rec1, rec5, rec6, rec7));
    rec5.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec1, rec6, rec7));
    rec6.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec1, rec7));
    rec7.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec6, rec1));

    // Sets the name box to the name of the current user on default.
    nameBox.setText(SharedResources.getCurrentUser().getUsername());
  }

  /** Clears all fields and boxes, activated when you hit the clear button. */
  public void clearFields() {
    rec1.setSelected(false);
    rec2.setSelected(false);
    rec3.setSelected(false);
    rec4.setSelected(false);
    rec5.setSelected(false);
    rec6.setSelected(false);
    rec7.setSelected(false);
    startTimeDrop.setValue(null);
    startAMPMDrop.setValue(null);
    endAMPMDrop.setValue(null);
    endTimeDrop.setValue(null);
    recurringDrop.setValue(null);
    numAttnBox.clear();
    notesBox.clear();
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

  /** Attempts to submit the form, but if it Doesn't pass the tests it sends errors to the users. */
  public void attemptSubmit() {
    if (rec1.isSelected()
        || rec2.isSelected()
        || rec3.isSelected()
        || rec4.isSelected()
        || rec5.isSelected()
        || rec6.isSelected()
        || rec7.isSelected()) {
      System.out.println("Sufficient fields filled");
      submit();
    } else {
      System.out.println("Sufficient fields not filled");
    }
  }

  /**
   * Deselects all other options once a togglebox is selected
   *
   * @param rec1
   * @param rec2
   * @param rec3
   * @param rec4
   * @param rec5
   * @param rec6
   */
  public void deselect(
      MFXRectangleToggleNode rec1,
      MFXRectangleToggleNode rec2,
      MFXRectangleToggleNode rec3,
      MFXRectangleToggleNode rec4,
      MFXRectangleToggleNode rec5,
      MFXRectangleToggleNode rec6) {
    rec1.setSelected(false);
    rec2.setSelected(false);
    rec3.setSelected(false);
    rec4.setSelected(false);
    rec5.setSelected(false);
    rec6.setSelected(false);
    rec7.setSelected(false);
  }

  private void submit() {
    ConferenceRequest res = new ConferenceRequest();
    if (rec1.isSelected()) res.setRoomName("BTM Conference Center");
    if (rec2.isSelected()) res.setRoomName("Duncan Reid Conference Room");
    if (rec3.isSelected()) res.setRoomName("Anesthesia Conf Floor L1");
    if (rec4.isSelected()) res.setRoomName("Medical Records Conference Room Floor L1");
    if (rec5.isSelected()) res.setRoomName("Abrams Conference Room");
    if (rec6.isSelected()) res.setRoomName("Carrie M. Hall Conference Center Floor 2");
    if (rec7.isSelected()) res.setRoomName("Shapiro Board Room MapNode 20 Floor 1");
    res.setNotes(notesBox.getText());
    res.setUsername(SharedResources.getCurrentUser().getUsername());
    res.setNumAttendees(Integer.parseInt(numAttnBox.getText()));
    res.setRecurringOption(Recurring.valueOf(recurringDrop.getText()));
    res.setStartTime(startTimeDrop.getText() + " " + startAMPMDrop.getText());
    res.setEndTime(endTimeDrop.getText() + " " + endAMPMDrop.getText());
    dbConnection.insertEntry(res);
    Navigation.navigate(Screen.HOME);
  }
}
