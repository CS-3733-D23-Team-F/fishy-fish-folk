package edu.wpi.fishfolk;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Preloader extends javafx.application.Preloader {
  public static Stage preloaderStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(Fapp.class.getResource("views/LoadingScreen.fxml"));
    Scene scene = new Scene(root);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setAlwaysOnTop(true);
    this.preloaderStage = stage;
    stage.show();
  }

  @Override
  public void handleStateChangeNotification(StateChangeNotification info) {
    if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
      preloaderStage.hide();
    }
  }
}
