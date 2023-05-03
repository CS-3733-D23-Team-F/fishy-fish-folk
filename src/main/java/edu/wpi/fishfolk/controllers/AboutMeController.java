package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
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
  @FXML VBox vbox;
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
  int counter = 0;

  @FXML
  public void initialize() {

    // Everyone's about me page is set to their URL
    URL JonPage = Fapp.class.getResource("views/popups/JonPage.fxml");
    URL MaxPage = Fapp.class.getResource("views/popups/MaxPage.fxml");
    URL MegPage = Fapp.class.getResource("views/popups/MegPage.fxml");
    URL SamPage = Fapp.class.getResource("views/popups/SamPage.fxml");
    URL CharliePage = Fapp.class.getResource("views/popups/CharliePage.fxml");
    URL ChristianPage = Fapp.class.getResource("views/popups/ChristianPage.fxml");
    URL LouisPage = Fapp.class.getResource("views/popups/LouisPage.fxml");
    URL TristinPage = Fapp.class.getResource("views/popups/TristinPage.fxml");
    URL TrajanPage = Fapp.class.getResource("views/popups/TrajanPage.fxml");
    URL BernhardtPage = Fapp.class.getResource("views/popups/BernhardtPage.fxml");
    URL BrendanPage = Fapp.class.getResource("views/popups/BrendanPage.fxml");

    // everyone's button opens their page
    Jon.setOnMouseClicked(event -> PopPage(JonPage));
    Max.setOnMouseClicked(event -> PopPage(MaxPage));
    Meg.setOnMouseClicked(event -> PopPage(MegPage));
    Sam.setOnMouseClicked(event -> PopPage(SamPage));
    Charlie.setOnMouseClicked(event -> PopPage(CharliePage));
    Christian.setOnMouseClicked(event -> PopPage(ChristianPage));
    Louis.setOnMouseClicked(event -> PopPage(LouisPage));
    Tristin.setOnMouseClicked(event -> PopPage(TristinPage));
    Trajan.setOnMouseClicked(event -> PopPage(TrajanPage));
    Bernhardt.setOnMouseClicked(event -> PopPage(BernhardtPage));
    Brendan.setOnMouseClicked(event -> PopPage(BrendanPage));
  }

  // handler function for everyone's page
  public void PopPage(URL Page) {

    try {
      if (counter != 1) counter++;
      else stack.getChildren().remove(vbox);
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Page);
      vbox = fxmlLoader.load();
      PopupController popupController = fxmlLoader.getController();
      stack.getChildren().add(vbox);

      popupController.back.setOnMouseClicked(
          event1 -> {
            stack.getChildren().remove(vbox);
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
