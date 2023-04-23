package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

import javax.swing.*;

public class SignageEditorController extends AbsController {
  @FXML MFXTextField presetText;
  @FXML MFXFilterComboBox<String> rooml0, rooml1, rooml2, rooml3;
  @FXML MFXFilterComboBox<String> roomr0, roomr1, roomr2, roomr3;
  @FXML MFXComboBox<Icon> arrowl0, arrowl1, arrowl2, arrowl3;
  @FXML MFXComboBox<String> arrowr0, arrowr1, arrowr2, arrowr3;

  public void initialize() {
    loadRooms();
    loadArrows();
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

  private void loadArrows() {
    arrowl0.getItems().add(edu.wpi.fishfolk.images.Signage.img);
    arrowl0.getItems().add("←");
    arrowl0.getItems().add("←");
    arrowl0.getItems().add("←");

    arrowl1.getItems().add("←");
    arrowl1.getItems().add("↑");
    arrowl1.getItems().add("→");
    arrowl1.getItems().add("↓");

    arrowl2.getItems().add("←");
    arrowl2.getItems().add("↑");
    arrowl2.getItems().add("→");
    arrowl2.getItems().add("↓");

    arrowl3.getItems().add("←");
    arrowl3.getItems().add("↑");
    arrowl3.getItems().add("→");
    arrowl3.getItems().add("↓");

    arrowr0.getItems().add("←");
    arrowr0.getItems().add("↑");
    arrowr0.getItems().add("→");
    arrowr0.getItems().add("↓");

    arrowr1.getItems().add("←");
    arrowr1.getItems().add("↑");
    arrowr1.getItems().add("→");
    arrowr1.getItems().add("↓");

    arrowr2.getItems().add("←");
    arrowr2.getItems().add("↑");
    arrowr2.getItems().add("→");
    arrowr2.getItems().add("↓");

    arrowr3.getItems().add("←");
    arrowr3.getItems().add("↑");
    arrowr3.getItems().add("→");
    arrowr3.getItems().add("↓");
  }
}
