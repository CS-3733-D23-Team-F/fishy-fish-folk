package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.ConferenceRequest;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.Recurring;
import io.github.palexdev.materialfx.controls.*;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

public class NewConferenceController extends AbsController {
  @FXML MFXButton confClearButton, confCancelButton, confSubmitButton;
  @FXML MFXFilterComboBox startTimeDrop, endTimeDrop, recurringDrop;
  @FXML MFXTextField numAttnBox, nameBox, notesBox;
  @FXML MFXRectangleToggleNode rec1, rec2, rec3, rec4, rec5, rec6, rec7;
  @FXML HBox confirmBlur;
  @FXML HBox confirmBox;
  @FXML AnchorPane confirmPane;
  @FXML MFXButton okButton;
  @FXML MFXDatePicker datePicker;
  Font oSans26;

  ArrayList<String> times = new ArrayList<>();
  ArrayList<Recurring> recurring = new ArrayList<>();

  public NewConferenceController() {
    super();
  }

  @FXML
  public void initialize() {
    addDropdownOptions();
    okButton.setOnAction(event -> Navigation.navigate(SharedResources.getHome()));
    confCancelButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
    confClearButton.setOnMouseClicked(event -> clearFields());
    confSubmitButton.setOnMouseClicked(event -> attemptSubmit());

    rec1.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec6, rec7));
    rec2.setOnMouseClicked(event -> deselect(rec1, rec3, rec4, rec5, rec6, rec7));
    rec3.setOnMouseClicked(event -> deselect(rec2, rec1, rec4, rec5, rec6, rec7));
    rec4.setOnMouseClicked(event -> deselect(rec2, rec3, rec1, rec5, rec6, rec7));
    rec5.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec1, rec6, rec7));
    rec6.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec1, rec7));
    rec7.setOnMouseClicked(event -> deselect(rec2, rec3, rec4, rec5, rec6, rec1));
    numAttnBox.setOnKeyReleased(event -> checkNumBox());

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
    endTimeDrop.setValue(null);
    recurringDrop.setValue(null);
    numAttnBox.clear();
    notesBox.clear();
    datePicker.setValue(null);
  }

  /** Initializing the dropdowns so they have all the required options. */
  public void addDropdownOptions() {

    // Dropdown options getting added to an arraylist called times
    times.add("6:00 AM");
    times.add("7:00 AM");
    times.add("8:00 AM");
    times.add("9:00 AM");
    times.add("10:00 AM");
    times.add("11:00 AM");
    times.add("12:00 PM");
    times.add("1:00 PM");
    times.add("2:00 PM");
    times.add("3:00 PM");
    times.add("4:00 PM");
    times.add("5:00 PM");
    times.add("6:00 PM");
    times.add("7:00 PM");
    times.add("8:00 PM");
    times.add("9:00 PM");
    times.add("10:00 PM");
    times.add("11:00 PM");

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

  public void checkNumBox() {
    try {
      if (Integer.parseInt(numAttnBox.getText()) > 20) {
        numAttnBox.clear();
      }
    } catch (Exception e) {
      numAttnBox.clear();
    }
  }

  /**
   * Finds which conference room was selected by the user.
   *
   * @return returns the name of the conference room
   */
  public String whichConf() {
    if (rec1.isSelected()) {
      return "BTM Conference Center";
    }
    if (rec2.isSelected()) {
      return "Duncan Reid Conference Room";
    }
    if (rec3.isSelected()) {
      return "Anesthesia Conf Floor L1";
    }
    if (rec4.isSelected()) {
      return "Medical Records Conference Room Floor L1";
    }
    if (rec5.isSelected()) {
      return "Abrams Conference Room";
    }
    if (rec6.isSelected()) {
      return "Carrie M. Hall Conference Center Floor 2";
    }
    if (rec7.isSelected()) {
      return "Shapiro Board Room MapNode 20 Floor 1";
    }
    return null;
  }

  /**
   * Checks to see if the end time comes after the start time
   *
   * @return true if it is, false if not
   */
  public boolean validEndTime() {
    int start = -1;
    for (int i = 0; i < times.size(); i++) {
      if (startTimeDrop.getText().equals(times.get(i))) {
        start = i;
      }
    }
    if (start == -1) {
      return false;
    }
    for (int i = start + 1; i < times.size(); i++) {
      if (endTimeDrop.getText().equals(times.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * checks to see if the given timeframe is interrupted by another booked conference
   *
   * @return returns true if there is a time interference
   */
  /*
  public boolean timeInterference(String start, String end){
    if (){

    }
  }*/

  /**
   * Checks to see if the meeting you want to schedule has any conflicts with already existing
   * meetings.
   *
   * @return returns true if there's no conflicts, false if there is
   */
  /*
  public boolean isItFree() {
    ArrayList<ConferenceRequest> allrequests =
        (ArrayList<ConferenceRequest>)
            dbConnection.getAllEntries(TableEntryType.CONFERENCE_REQUEST);
    for (int i = 0; i < allrequests.size(); i++) {
      if (recurringDrop.getText().equals(Recurring.NEVER)) {
        if (whichConf().equals(allrequests.get(i).getRoomName())) {
          if (datePicker.getValue().atStartOfDay().equals(allrequests.get(i).getDateReserved())) {
            if(timeInterference(allrequests.get(i).getStartTime(), allrequests.get(i).getEndTime())){
              return false;
            }
          }
        }
      }
    }
    return true;
  }
  */
  /** Attempts to submit the form, but if it Doesn't pass the tests it sends errors to the users. */
  public void attemptSubmit() {
    if (rec1.isSelected()
        || rec2.isSelected()
        || rec3.isSelected()
        || rec4.isSelected()
        || rec5.isSelected()
        || rec6.isSelected()
        || rec7.isSelected()) {
      if (!(startTimeDrop.getText().isEmpty())) {
        if (!(endTimeDrop.getText().isEmpty())) {
          if (validEndTime()) {
            if (!(datePicker.getValue() == null)) {
              if (datePicker
                  .getValue()
                  .atStartOfDay()
                  .isAfter(datePicker.getCurrentDate().atStartOfDay())) {
                if (!(numAttnBox.getText().isEmpty())) {
                  int numba = 75;
                  try {
                    numba = Integer.parseInt(numAttnBox.getText());
                  } catch (Exception e) {
                    submissionError("Nice Try Bernhardt.", numAttnBox);
                  }
                  if (numba < 21 && numba > 1 && numba != 75) {
                    if (!(recurringDrop.getText().isEmpty())) {
                      // if (isItFree()) {
                      submit();
                      /*} else {
                        submissionError(
                            "There is already a meeting scheduled for this time.",
                            confSubmitButton);
                      }*/
                    } else {
                      submissionError(
                          "You must choose your setting for recurring.",
                          confSubmitButton); // recurringDrop);
                    }
                  } else {
                    submissionError(
                        "Invalid Input for number of attendees.", confSubmitButton); // numAttnBox);
                  }
                } else {
                  submissionError(
                      "You must put in the number of attendees.", confSubmitButton); // numAttnBox);
                }
              } else {
                submissionError(
                    "You must choose a date in the future.", confSubmitButton); // datePicker);
              }
            } else {
              submissionError("You must include a date", confSubmitButton); // datePicker);
            }
          } else {
            submissionError("That's not how time works.", confSubmitButton); // endTimeDrop);
          }
        } else {
          submissionError("You must put in a time.", confSubmitButton); // endTimeDrop);
        }
      } else {
        submissionError("You must put in a time.", confSubmitButton); // startTimeDrop);
      }
    } else {
      submissionError("You must select a room.", confSubmitButton); // rec1);
    }
  }

  /**
   * Creates an error popup for the given values.
   *
   * @param error the error message you want to present.
   * @param node the area it will pop up next to.
   */
  private void submissionError(String error, Node node) {
    PopOver popup = new PopOver();
    Text popText = new Text(error);
    popText.setFont(oSans26);
    popup.setContentNode(popText);
    popup.show(node);
  }

  /**
   * Deselects all other options once a togglebox is selected
   *
   * @param rec1 rectangle 1
   * @param rec2 rectangle 2
   * @param rec3 rectangle 3
   * @param rec4 rectangle 4
   * @param rec5 rectangle 5
   * @param rec6 rectangle 6
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
  }

  /** imputs all values from the reservation into the database table. */
  private void submit() {
    ConferenceRequest res =
        new ConferenceRequest(
            notesBox.getText(),
            SharedResources.getCurrentUser().getUsername(),
            startTimeDrop.getText(),
            endTimeDrop.getText(),
            datePicker.getValue().atStartOfDay(),
            Recurring.valueOf(recurringDrop.getText()),
            Integer.parseInt(numAttnBox.getText()),
            whichConf());
    dbConnection.insertEntry(res);
    confirmBlur.setDisable(false);
    confirmBlur.setVisible(true);
    confirmBox.setDisable(false);
    confirmBox.setVisible(true);
    confirmPane.setVisible(true);
    confirmPane.setDisable(false);
    /*
     * String notes, String username, String startTime, String endTime, Recurring recurringOption,
     * int numAttendees, String roomName
     */
  }
}
