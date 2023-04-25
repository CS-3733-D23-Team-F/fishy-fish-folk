package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry.Location;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class MapEditorLocationController {

  @FXML MFXTextField longnameText;
  @FXML MFXTextField shortnameText;
  @FXML MFXTextField typeText;

  public void setData(Location location) {

    shortnameText.setText(location.getShortName());
    typeText.setText(location.getNodeType().toString());
    longnameText.setText(location.getLongName());
  }
}
