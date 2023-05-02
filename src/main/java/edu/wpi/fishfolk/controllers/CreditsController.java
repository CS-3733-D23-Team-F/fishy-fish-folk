package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CreditsController {

  @FXML ImageView displayImage;

  @FXML Label link;

  @FXML
  HBox mapEditor,
      signage,
      request,
      pathfinding,
      up,
      down,
      upArrow,
      left,
      right,
      accounts,
      viewOrders;

  @FXML
  public void initialize() {
    mapEditor.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/mapEditor.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/drawing_3964687?term=map+and+pencil&page=1&position=8&origin=search&related_id=3964687");
        });

    request.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/serviceRequest.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/desk-bell_7879702?term=service+bell&page=1&position=7&origin=search&related_id=7879702");
        });

    accounts.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(
                  Fapp.class.getResourceAsStream("images/NavigationBar/justAddMorePeople.png")));
          link.setText("https://www.flaticon.com/free-icon/crowd_599979");
        });

    viewOrders.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/anotherTable.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/table_178916?term=table&page=1&position=14&origin=search&related_id=178916");
        });
  }
}
