package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.pathfinding.TextDirection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TextInstructionController {

  @FXML Label directionText;
  @FXML Label distanceText;

  @FXML ImageView directionImage;

  public void setData(TextDirection direction, int num) {

    directionText.setText(num + ". " + direction.getText());
    distanceText.setText(direction.getDistance());
    System.out.println("Hello " + direction.getText());

    // TODO add images for elevator & stairs up & down
    switch (direction.getDirection()) {
      case STRAIGHT:
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/up-arrow.png")));
        break;

      case LEFT:
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/left-turn.png")));
        break;

      case RIGHT:
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/right-turn.png")));
        break;
    }
  }
}
