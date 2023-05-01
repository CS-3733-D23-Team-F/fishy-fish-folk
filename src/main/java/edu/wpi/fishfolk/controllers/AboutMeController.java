package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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

  @FXML
  public void initialize() {

    Jon.setOnMouseClicked(event -> PopPage());

    /*
    // none of this works

    final FXMLLoader JonPage = new FXMLLoader(AboutMeController.class.getResource("views/popups/JonPage.fxml"));
    FXMLLoader MaxPage = new FXMLLoader(getClass().getResource("MaxPage.fxml"));
    FXMLLoader MegPage = new FXMLLoader(getClass().getResource("MegPage.fxml"));
    FXMLLoader TrajanPage = new FXMLLoader(getClass().getResource("TrajanPage.fxml"));
    FXMLLoader TristinPage = new FXMLLoader(getClass().getResource("TristinPage.fxml"));
    FXMLLoader BernhardtPage = new FXMLLoader(getClass().getResource("BernhardtPage.fxml"));
    FXMLLoader LouisPage = new FXMLLoader(getClass().getResource("LouisPage.fxml"));
    FXMLLoader CharliePage = new FXMLLoader(getClass().getResource("CharliePage.fxml"));
    FXMLLoader SamPage = new FXMLLoader(getClass().getResource("SamPage.fxml"));
    FXMLLoader ChristianPage = new FXMLLoader(getClass().getResource("ChristianPage.fxml"));
    FXMLLoader BrendanPage = new FXMLLoader(getClass().getResource("BrendanPage.fxml"));
    Jon.setOnMouseClicked(event -> PopPage(JonPage));
    Meg.setOnMouseClicked(event -> PopPage(MegPage));
    Bernhardt.setOnMouseClicked(event -> PopPage(BernhardtPage));
    Trajan.setOnMouseClicked(event -> PopPage(TrajanPage));
    Tristin.setOnMouseClicked(event -> PopPage(TristinPage));
    Louis.setOnMouseClicked(event -> PopPage(LouisPage));
    Max.setOnMouseClicked(event -> PopPage(MaxPage));
    Charlie.setOnMouseClicked(event -> PopPage(CharliePage));
    Sam.setOnMouseClicked(event -> PopPage(SamPage));
    Christian.setOnMouseClicked(event -> PopPage(ChristianPage));
    Brendan.setOnMouseClicked(event -> PopPage(BrendanPage)); */
  }

  /*public void start(Stage stage) throws IOException
  {
    // set title for the stage
    stage.setTitle("Creating popup");

    // create a tile pane
    AnchorPane tilepane = new AnchorPane();

    // create a popup
    Popup popup = new Popup();

    final FXMLLoader JonPage = new FXMLLoader(AboutMeController.class.getResource("views/popups/JonPage.fxml"));
    final AnchorPane popupPage = JonPage.load();
    anchor = popupPage;

    Scene scene = new Scene(popupPage);

    aboutpages.setScene(scene);
    aboutpages.setMaximized(true);
    aboutpages.show();


    // add the label
    popup.getContent().;

    // action event
    EventHandler<ActionEvent> event =
            new EventHandler<ActionEvent>() {

              public void handle(ActionEvent e)
              {
                if (!popup.isShowing())
                  popup.show(stage);
                else
                  popup.hide();
              }
            };

    // add button
    tilepane.getChildren().add(button);

    // create a scene
    Scene scene = new Scene(tilepane, 200, 200);

    // set the scene
    stage.setScene(scene);

    stage.show();
  }*/
  // FXMLLoader name
  public void PopPage() {

    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(AboutMeController.class.getResource("views/popups/JonPage.fxml"));

      VBox vbox = fxmlLoader.load();

      // AlertsController alertsController = fxmlLoader.getController();

      // alertsController.closeAlert.setDisable(true);
      // alertsController.closeAlert.setVisible(false);

      AboutMe.getChildren().clear();
      AboutMe.getChildren().add(vbox);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
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
