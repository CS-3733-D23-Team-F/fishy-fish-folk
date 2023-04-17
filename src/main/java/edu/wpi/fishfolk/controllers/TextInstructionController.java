package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.pathfinding.TextDirection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class TextInstructionController {

  @FXML Label directionText;
  @FXML Label distanceText;

  @FXML ImageView directionImage;

  public void setData(TextDirection dir) {
    directionText.setText(dir.getDirection());
    distanceText.setText(dir.getDistance());
    // directionImage.setImage(Fapp.class.);
  }
}
