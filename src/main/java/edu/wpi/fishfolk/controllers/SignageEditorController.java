package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.SignagePreset;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javax.swing.*;

public class SignageEditorController extends AbsController {
  @FXML MFXTextField presetText;
  @FXML MFXDatePicker datePicker;
  @FXML MFXFilterComboBox<String> rooml0, rooml1, rooml2, rooml3;
  @FXML MFXFilterComboBox<String> roomr0, roomr1, roomr2, roomr3;
  @FXML ImageView iconl0, iconl1, iconl2, iconl3;
  @FXML ImageView iconr0, iconr1, iconr2, iconr3;
  @FXML MFXButton cancelButton, clearButton, submitButton;

  SignagePreset currentPreset = new SignagePreset();

  public void initialize() {
    loadRooms();

    fullDisable(iconl0);
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);

    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    rooml0.setOnAction(event -> fullEnable(iconl0));
    rooml1.setOnAction(event -> fullEnable(iconl1));
    rooml2.setOnAction(event -> fullEnable(iconl2));
    rooml3.setOnAction(event -> fullEnable(iconl3));

    roomr0.setOnAction(event -> fullEnable(iconr0));
    roomr1.setOnAction(event -> fullEnable(iconr1));
    roomr2.setOnAction(event -> fullEnable(iconr2));
    roomr3.setOnAction(event -> fullEnable(iconr3));

    iconl0.setOnMouseClicked(event -> multiRotate(iconl0));
    iconl1.setOnMouseClicked(event -> multiRotate(iconl1));
    iconl2.setOnMouseClicked(event -> multiRotate(iconl2));
    iconl3.setOnMouseClicked(event -> multiRotate(iconl3));

    iconr0.setOnMouseClicked(event -> multiRotate(iconr0));
    iconr1.setOnMouseClicked(event -> multiRotate(iconr1));
    iconr2.setOnMouseClicked(event -> multiRotate(iconr2));
    iconr3.setOnMouseClicked(event -> multiRotate(iconr3));

    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    clearButton.setOnMouseClicked(event -> clearAll());
    submitButton.setOnMouseClicked(event -> submit());
  }

  private void fullDisable(ImageView icon) {
    icon.setDisable(true);
    icon.setOpacity(0.5);
  }

  private void fullEnable(ImageView icon) {
    icon.setDisable(false);
    icon.setOpacity(1);
  }

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

  private void multiRotate(ImageView icon) {
    // System.out.println(icon.getRotate());
    icon.setRotate(icon.getRotate() + 90);
    if (icon.getRotate() == 360) icon.setRotate(0);
  }

  private void clearAll() {
    fullDisable(iconl0);
    fullDisable(iconl1);
    fullDisable(iconl2);
    fullDisable(iconl3);

    fullDisable(iconr0);
    fullDisable(iconr1);
    fullDisable(iconr2);
    fullDisable(iconr3);

    rooml0.setValue(null);
    rooml1.setValue(null);
    rooml2.setValue(null);
    rooml3.setValue(null);

    roomr0.setValue(null);
    roomr1.setValue(null);
    roomr2.setValue(null);
    roomr3.setValue(null);
  }

  private void submit() {
    currentPreset.setName(presetText.getText());
    currentPreset.setDate(datePicker.getValue());
    Navigation.navigate(Screen.HOME);
  }
}
