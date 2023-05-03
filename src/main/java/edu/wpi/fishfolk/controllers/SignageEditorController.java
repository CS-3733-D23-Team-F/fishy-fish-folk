package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.Sign;
import edu.wpi.fishfolk.ui.SignagePreset;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.util.Duration;

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
  @FXML MFXTextField subtextl0, subtextl1, subtextl2, subtextl3;
  @FXML MFXTextField subtextr0, subtextr1, subtextr2, subtextr3;
  @FXML MFXFilterComboBox<String> presetSelect;
  @FXML Label error;
  @FXML
  MFXButton cancelButton,
      clearButton,
      submitButton,
      loadButton,
      deleteButton; // cancel form, clear fields, and submit form
  String identifier = "TEST";

  ArrayList<MFXFilterComboBox<String>> listTexts = new ArrayList<>();
  ArrayList<ImageView> listIcons = new ArrayList<>();
  ArrayList<MFXTextField> listSubText = new ArrayList<>();

  SignagePreset currentPreset =
      new SignagePreset(); // SignagePreset object stores room selectors and sign orientations

  ArrayList<edu.wpi.fishfolk.database.TableEntry.SignagePreset> allPresets =
      (ArrayList<edu.wpi.fishfolk.database.TableEntry.SignagePreset>)
          dbConnection.getAllEntries(TableEntryType.SIGNAGE_PRESET);
  private static TranslateTransition thugShaker;

  public void initialize() {
    setToBlue();
    clearError();
    loadRooms(); // read documentation for loadRooms()

    loadPresetSelect();
    loadListTexts();
    loadListIcons();
    loadListSubtexts();

    // all direction arrows are disabled at start
    fullDisable(iconl0);
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);
    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    subtextl0.setOpacity(0);
    subtextl1.setOpacity(0);
    subtextl2.setOpacity(0);
    subtextl3.setOpacity(0);
    subtextr0.setOpacity(0);
    subtextr1.setOpacity(0);
    subtextr2.setOpacity(0);
    subtextr3.setOpacity(0);

    // direction arrows are not interactable until the associated text box is filled
    rooml0.setOnAction(
        event -> {
          fullEnable(iconl0);
          subtextl0.setOpacity(1);
        });
    rooml1.setOnAction(
        event -> {
          fullEnable(iconl1);
          subtextl1.setOpacity(1);
        });
    rooml2.setOnAction(
        event -> {
          fullEnable(iconl2);
          subtextl2.setOpacity(1);
        });
    rooml3.setOnAction(
        event -> {
          fullEnable(iconl3);
          subtextl3.setOpacity(1);
        });
    roomr0.setOnAction(
        event -> {
          fullEnable(iconr0);
          subtextr0.setOpacity(1);
        });
    roomr1.setOnAction(
        event -> {
          fullEnable(iconr1);
          subtextr1.setOpacity(1);
        });
    roomr2.setOnAction(
        event -> {
          fullEnable(iconr2);
          subtextr2.setOpacity(1);
        });
    roomr3.setOnAction(
        event -> {
          fullEnable(iconr3);
          subtextr3.setOpacity(1);
        });

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
        event -> Navigation.navigate(SharedResources.getHome())); // cancel button just returns home
    clearButton.setOnMouseClicked(
        event -> clearAll()); // clear button clears and resets all objects on the form
    submitButton.setOnMouseClicked(
        event -> submit()); // submit button does submit(), read documentation for submit()
    loadButton.setOnMouseClicked(event -> loadPreset());
    deleteButton.setOnMouseClicked(event -> deletePreset());
  }

  private void loadListTexts() {
    listTexts.add(rooml0);
    listTexts.add(rooml1);
    listTexts.add(rooml2);
    listTexts.add(rooml3);
    listTexts.add(roomr0);
    listTexts.add(roomr1);
    listTexts.add(roomr2);
    listTexts.add(roomr3);
  }

  private void loadListIcons() {
    listIcons.add(iconl0);
    listIcons.add(iconl1);
    listIcons.add(iconl2);
    listIcons.add(iconl3);
    listIcons.add(iconr0);
    listIcons.add(iconr1);
    listIcons.add(iconr2);
    listIcons.add(iconr3);
  }

  private void loadListSubtexts() {
    listSubText.add(subtextl0);
    listSubText.add(subtextl1);
    listSubText.add(subtextl2);
    listSubText.add(subtextl3);
    listSubText.add(subtextr0);
    listSubText.add(subtextr1);
    listSubText.add(subtextr2);
    listSubText.add(subtextr3);
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
    setToBlue();
    clearError();
    rooml0.setValue(null); // set all eight room selector values to null
    rooml1.setValue(null);
    rooml2.setValue(null);
    rooml3.setValue(null);
    roomr0.setValue(null);
    roomr1.setValue(null);
    roomr2.setValue(null);
    roomr3.setValue(null);

    rooml0.setText(""); // set all eight room selector values to null
    rooml1.setText("");
    rooml2.setText("");
    rooml3.setText("");
    roomr0.setText("");
    roomr1.setText("");
    roomr2.setText("");
    roomr3.setText("");

    fullDisable(iconl0); // fully disables all eight direction arrows
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);
    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    subtextl0.setText("");
    subtextl1.setText("");
    subtextl2.setText("");
    subtextl3.setText("");
    subtextr0.setText("");
    subtextr1.setText("");
    subtextr2.setText("");
    subtextr3.setText("");

    subtextl0.setOpacity(0);
    subtextl1.setOpacity(0);
    subtextl2.setOpacity(0);
    subtextl3.setOpacity(0);
    subtextr0.setOpacity(0);
    subtextr1.setOpacity(0);
    subtextr2.setOpacity(0);
    subtextr3.setOpacity(0);

    presetText.setText(""); // resets preset name text box
    datePicker.setValue(null); // rests date picker with null value
  }

  // loads MFXFilterCombobox with list of Signage Preset names
  private void loadPresetSelect() {
    for (int i = 0; i < allPresets.size(); i++) {
      presetSelect.getItems().add(allPresets.get(i).getName());
    }
  }

  // when preset is selected in MFXFilterComboBox and Load button is pressed
  // loads given preset fields into editor
  private void loadPreset() {
    setToBlue();
    clearError();
    if (!(presetSelect.getValue() == null)) identifier = presetSelect.getValue();
    else return;
    clearAll();

    edu.wpi.fishfolk.database.TableEntry.SignagePreset preset =
        (edu.wpi.fishfolk.database.TableEntry.SignagePreset)
            dbConnection.getEntry(identifier, TableEntryType.SIGNAGE_PRESET);

    presetText.setText(preset.getName());
    datePicker.setValue(preset.getDate());

    for (int i = 0; i < 8; i++) {
      if (preset.getSigns()[i] == null) {
        listSubText.get(i).setOpacity(0);
      } else {
        listTexts.get(i).setOpacity(1);
        listTexts.get(i).setDisable(false);
        listIcons.get(i).setOpacity(1);
        listIcons.get(i).setDisable(false);
        listSubText.get(i).setOpacity(1);
        listSubText.get(i).setDisable(false);
        listTexts
            .get(i)
            .setText(
                preset.getSigns()[i]
                    .getLabel()); // otherwise set the i'th text to the i'th Sign's name
        listTexts
            .get(i)
            .setValue(
                preset.getSigns()[i].getLabel()); // the same thing again but with "value" instead
        listIcons
            .get(i)
            .setRotate(
                preset.getSigns()[i]
                    .getDirection()); // and same for the i'th direction for the arrow
        listSubText.get(i).setText(preset.getSigns()[i].getSubtext());
      }
    }
  }

  private void deletePreset() {
    setToBlue();
    clearError();
    for (int i = 0; i < allPresets.size(); i++) {
      if (presetSelect.getValue().equals(allPresets.get(i).getName())) {
        System.out.println("deleting " + allPresets.get(i).getName());
        dbConnection.removeEntry(allPresets.get(i).getName(), TableEntryType.SIGNAGE_PRESET);
        presetSelect.getItems().remove(i);
        return;
      }
    }
  }

  /** Sets all borders back to blue */
  public void setToBlue() {
    presetText.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    datePicker.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    rooml0.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    rooml1.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    rooml2.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    rooml3.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    roomr0.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    roomr1.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    roomr2.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
    roomr3.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5");
  }

  /** Clears the error field */
  private void clearError() {
    error.setText("");
    error.setVisible(false);
  }

  /**
   * Creates an error popup for the given values.
   *
   * @param errorLine the error message you want to present.
   * @param node the area it will pop up next to.
   */
  private void submissionError(String errorLine, Node node) {
    node.setStyle("-fx-border-color: red; -fx-border-radius: 5; -fx-border-width: 1;");
    if (thugShaker == null || thugShaker.getNode() != node) {
      thugShaker = new TranslateTransition(Duration.millis(100), node);
    }
    thugShaker.setFromX(0f);
    thugShaker.setCycleCount(4);
    thugShaker.setAutoReverse(true);
    thugShaker.setByX(15f);
    thugShaker.playFromStart();
    error.setText(errorLine);
    error.setVisible(true);
    error.setStyle("-fx-text-fill:  red;");
    error.setFont(Font.font("Open Sans", 15.0));
  }

  // submit() fills the created SignagePreset object with the fields of the form for database
  // storage, then navigates to HOME page
  private void submit() {
    clearError();
    setToBlue();

    if (rooml0.getText().equals("")
        && rooml1.getText().equals("")
        && rooml2.getText().equals("")
        && rooml3.getText().equals("")
        && roomr0.getText().equals("")
        && roomr1.getText().equals("")
        && roomr2.getText().equals("")
        && roomr3.getText().equals("")) {
      submissionError("", rooml0);
      submissionError("", rooml1);
      submissionError("", rooml2);
      submissionError("", rooml3);
      submissionError("", roomr0);
      submissionError("", roomr1);
      submissionError("", roomr2);
      submissionError("You must fill out at least one room field.", roomr3);
      return;
    }
    if (presetText.getText().equals("")) {
      submissionError("You must fill in the name of the signage preset.", presetText);
      return;
    }
    if (datePicker.getValue() == null) {
      submissionError("You must choose a date.", datePicker);
      return;
    }

    currentPreset.setName(presetText.getText()); // preset name
    currentPreset.setDate(datePicker.getValue()); // preset implementation date

    // assigns choice box and direction arrow direction to new Sign object and adds to
    // currentPreset's list of Signs
    if (!(iconl0.isDisable()))
      currentPreset.addSign(
          new Sign(rooml0.getValue(), iconl0.getRotate(), subtextl0.getText()), 0);
    if (!(iconl1.isDisable()))
      currentPreset.addSign(
          new Sign(rooml1.getValue(), iconl1.getRotate(), subtextl1.getText()), 1);
    if (!(iconl2.isDisable()))
      currentPreset.addSign(
          new Sign(rooml2.getValue(), iconl2.getRotate(), subtextl2.getText()), 2);
    if (!(iconl3.isDisable()))
      currentPreset.addSign(
          new Sign(rooml3.getValue(), iconl3.getRotate(), subtextl3.getText()), 3);

    if (!(iconr0.isDisable()))
      currentPreset.addSign(
          new Sign(roomr0.getValue(), iconr0.getRotate(), subtextr0.getText()), 4);
    if (!(iconr1.isDisable()))
      currentPreset.addSign(
          new Sign(roomr1.getValue(), iconr1.getRotate(), subtextr1.getText()), 5);
    if (!(iconr2.isDisable()))
      currentPreset.addSign(
          new Sign(roomr2.getValue(), iconr2.getRotate(), subtextr2.getText()), 6);
    if (!(iconr3.isDisable()))
      currentPreset.addSign(
          new Sign(roomr3.getValue(), iconr3.getRotate(), subtextr3.getText()), 7);

    edu.wpi.fishfolk.database.TableEntry.SignagePreset preset =
        new edu.wpi.fishfolk.database.TableEntry.SignagePreset(
            currentPreset.getPresetName(),
            currentPreset.getImplementationDate(),
            currentPreset.signs);
    dbConnection.insertEntry(preset);

    Navigation.navigate(SharedResources.getHome()); // go homes
  }
}
