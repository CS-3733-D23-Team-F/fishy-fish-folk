package edu.wpi.fishfolk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CreditsController {

  @FXML Label CredM;

  @FXML
  public void initialize() {
    CredM.setWrapText(true);
    CredM.setText(
        "APIs and Components Used\n Environment:\n - Java 17, via openjdk-17.0.6\n - JavaFX version 19.0.2.1\n - Gradle\n - PostgreSQL\n Development Tools:\n - IntelliJ IDEA 2022.3.3\n - SceneBuilder v19\n - MaterialFX 11.13.5 \n - GestureFX\n - ControlsFX\n - pgAdmin 4 v6\n");
  }
}
