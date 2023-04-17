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

  public void setData(TextDirection dir, int num) {

    switch (dir.getDirection()) {
      case "straight":
        directionText.setText(num + ". Go " + dir.getDirection());
        distanceText.setText("for " + dir.getDistance());
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/up-arrow.png")));
        break;
      case "left":
        directionText.setText(num + ". Turn " + dir.getDirection());
        distanceText.setText("then stragiht for " + dir.getDistance());
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/left-turn.png")));
        break;
      case "right":
        directionText.setText(num + ". Turn " + dir.getDirection());
        distanceText.setText("then stragiht for " + dir.getDistance());
        directionImage.setImage(new Image(Fapp.class.getResourceAsStream("images/right-turn.png")));
        break;
      default:
        break;
    }
  }
}
