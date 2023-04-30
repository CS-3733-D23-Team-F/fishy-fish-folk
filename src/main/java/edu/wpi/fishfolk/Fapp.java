package edu.wpi.fishfolk;

import edu.wpi.fishfolk.controllers.LoadingScreenController;
import java.awt.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fapp extends Application {

  @Setter @Getter public static Stage primaryStage;
  @Setter @Getter public static BorderPane rootPane;

  @Override
  public void init() throws Exception {
    log.info("Starting Up");
    LoadingScreenController loadingScreen = new LoadingScreenController();
    loadingScreen.checkFunctions();
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    // Fapp.primaryStage = primaryStage;
    this.primaryStage.setAlwaysOnTop(true);
    this.primaryStage.show();
    // root.setLeft(roots.getServiceBar());

  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
