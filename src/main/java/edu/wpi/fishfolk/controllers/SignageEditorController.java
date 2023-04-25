package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.Sign;
import edu.wpi.fishfolk.ui.SignagePreset;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class SignageEditorController extends AbsController {
  @FXML MFXTextField presetText; // name for the signage preset
  @FXML MFXDatePicker datePicker; // picks the date of preset implementation
  @FXML
  MFXFilterComboBox<String> rooml0,
      rooml1,
      rooml2,
      rooml3; // room selectors on left side (0-3 is top-bottom)
  @FXML
  MFXFilterComboBox<String> roomr0,
      roomr1,
      roomr2,
      roomr3; // room selectors on right side (0-3 is top-bottom)
  @FXML
  ImageView iconl0, iconl1, iconl2, iconl3; // direction arrows for left side (0-3 is top-bottom)
  @FXML
  ImageView iconr0, iconr1, iconr2, iconr3; // direction arrows for right side (0-3 is top-bottom)
  @FXML
  MFXButton cancelButton, clearButton, submitButton; // cancel form, clear fields, and submit form

  SignagePreset currentPreset =
      new SignagePreset(); // SignagePreset object stores room selectors and sign orientations

  public void initialize() {
    loadRooms(); // read documentation for loadRooms()

    // all direction arrows are disabled at start
    fullDisable(iconl0);
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);
    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    // direction arrows are not interactable until the associated text box is filled
    rooml0.setOnAction(event -> fullEnable(iconl0));
    rooml1.setOnAction(event -> fullEnable(iconl1));
    rooml2.setOnAction(event -> fullEnable(iconl2));
    rooml3.setOnAction(event -> fullEnable(iconl3));
    roomr0.setOnAction(event -> fullEnable(iconr0));
    roomr1.setOnAction(event -> fullEnable(iconr1));
    roomr2.setOnAction(event -> fullEnable(iconr2));
    roomr3.setOnAction(event -> fullEnable(iconr3));

    // clicking on any not-disabled direction arrow causes multiRotate for associated arrow
    iconl0.setOnMouseClicked(event -> multiRotate(iconl0));
    iconl1.setOnMouseClicked(event -> multiRotate(iconl1));
    iconl2.setOnMouseClicked(event -> multiRotate(iconl2));
    iconl3.setOnMouseClicked(event -> multiRotate(iconl3));
    iconr0.setOnMouseClicked(event -> multiRotate(iconr0));
    iconr1.setOnMouseClicked(event -> multiRotate(iconr1));
    iconr2.setOnMouseClicked(event -> multiRotate(iconr2));
    iconr3.setOnMouseClicked(event -> multiRotate(iconr3));

    cancelButton.setOnMouseClicked(
        event -> Navigation.navigate(Screen.HOME)); // cancel button just returns home
    clearButton.setOnMouseClicked(
        event -> clearAll()); // clear button clears and resets all objects on the form
    submitButton.setOnMouseClicked(
        event -> submit()); // submit button does submit(), read documentation for submit()
  }

  // fullDisable() resets the direction arrows by disabling them, lowering their opacity, and resets
  // their orientation
  private void fullDisable(ImageView icon) {
    icon.setDisable(true);
    icon.setOpacity(0.5);
    icon.setRotate(0);
  }

  // fullEnable() enables the button and triggers full opacity for the arrow to signify usability
  private void fullEnable(ImageView icon) {
    icon.setDisable(false);
    icon.setOpacity(1);
  }

  // loadRooms() fills the room selectors with the names of the rooms from the database
  private void loadRooms() {
    rooml0.getItems().addAll(dbConnection.getDestLongnames());
    rooml1.getItems().addAll(dbConnection.getDestLongnames());
    rooml2.getItems().addAll(dbConnection.getDestLongnames());
    rooml3.getItems().addAll(dbConnection.getDestLongnames());
    roomr0.getItems().addAll(dbConnection.getDestLongnames());
    roomr1.getItems().addAll(dbConnection.getDestLongnames());
    roomr2.getItems().addAll(dbConnection.getDestLongnames());
    roomr3.getItems().addAll(dbConnection.getDestLongnames());
  }

  // multiRotate() sets the orientation of the direction arrow to its previous orientation + 90
  // degrees, resets at 360
  private void multiRotate(ImageView icon) {
    icon.setRotate(icon.getRotate() + 90);
    if (icon.getRotate() == 360) icon.setRotate(0);
  }

  // clearAll() sets room selectors to default null values and disables all direction arrows
  private void clearAll() {
    rooml0.setValue(null); // set all eight room selector values to null
    rooml1.setValue(null);
    rooml2.setValue(null);
    rooml3.setValue(null);
    roomr0.setValue(null);
    roomr1.setValue(null);
    roomr2.setValue(null);
    roomr3.setValue(null);

    fullDisable(iconl0); // fully disables all eight direction arrows
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);
    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    presetText.setText(""); // resets preset name text box
    datePicker.setValue(null); // rests date picker with null value
  }

  // submit() fills the created SignagePreset object with the fields of the form for database
  // storage, then navigates to HOME page
  private void submit() {
    currentPreset.setName(presetText.getText()); // preset name
    currentPreset.setDate(datePicker.getValue()); // preset implementation date

    // assigns choice box and direction arrow direction to new Sign object and adds to
    // currentPreset's list of Signs
    currentPreset.addSign(new Sign(rooml0.getValue(), iconl0.getRotate()), 0);
    currentPreset.addSign(new Sign(rooml1.getValue(), iconl1.getRotate()), 1);
    currentPreset.addSign(new Sign(rooml2.getValue(), iconl2.getRotate()), 2);
    currentPreset.addSign(new Sign(rooml3.getValue(), iconl3.getRotate()), 3);

    currentPreset.addSign(new Sign(roomr0.getValue(), iconr0.getRotate()), 4);
    currentPreset.addSign(new Sign(roomr1.getValue(), iconr1.getRotate()), 5);
    currentPreset.addSign(new Sign(roomr2.getValue(), iconr2.getRotate()), 6);
    currentPreset.addSign(new Sign(roomr3.getValue(), iconr3.getRotate()), 7);

    edu.wpi.fishfolk.database.TableEntry.SignagePreset preset =
        new edu.wpi.fishfolk.database.TableEntry.SignagePreset(
            currentPreset.getPresetName(),
            currentPreset.getImplementationDate(),
            currentPreset.signs);
    dbConnection.insertEntry(preset);

    Navigation.navigate(Screen.HOME); // go homes
  }
}