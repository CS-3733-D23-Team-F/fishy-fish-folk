package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class AboutMeController // extends Application
 {
  @Setter @Getter private static Stage aboutpages;
  @Setter @Getter private static AnchorPane anchor;
  @FXML Label AboutM;
  @FXML MFXButton Jon;
  @FXML MFXButton Meg;
  @FXML MFXButton Sam;
  @FXML MFXButton Christian;
  @FXML MFXButton Max;
  @FXML MFXButton Charlie;
  @FXML MFXButton Louis;
  @FXML MFXButton Tristin;
  @FXML MFXButton Trajan;
  @FXML MFXButton Bernhardt;
  @FXML MFXButton Brendan;
  @FXML VBox AboutMe;
  @FXML StackPane stack;

  @FXML
  public void initialize() {

    Jon.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/JonPage.fxml"));
            VBox vbox = fxmlLoader.load();
            PopupController popupController = fxmlLoader.getController();
            stack.getChildren().add(vbox);

            popupController.back.setOnMouseClicked(
                event1 -> {
                  stack.getChildren().remove(vbox);
                });
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  // FXMLLoader name
  public void PopPage() {

    /*
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Fapp.class.getResource("views/popups/JonPage.fxml"));

      HBox namePane = fxmlLoader.load();
      Stage window = new Stage();
      Scene scene = new Scene(namePane);
      window.setScene(scene);
      window.show();
      // AboutMe.getChildren().clear();
      // AboutMe.getChildren().add(namePane);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }*/
  }
}
