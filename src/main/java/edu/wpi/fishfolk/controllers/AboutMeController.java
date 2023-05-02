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

public class AboutMeController {
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

    Max.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/MaxPage.fxml"));
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

    Meg.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/MegPage.fxml"));
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

    Sam.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/SamPage.fxml"));
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

    Charlie.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/CharliePage.fxml"));
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

    Christian.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/ChristianPage.fxml"));
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

    Louis.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/LouisPage.fxml"));
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

    Tristin.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/TristinPage.fxml"));
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

    Trajan.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/TrajanPage.fxml"));
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

    Bernhardt.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/BernhardtPage.fxml"));
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

    Brendan.setOnMouseClicked(
        event -> {
          try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Fapp.class.getResource("views/popups/BrendanPage.fxml"));
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
