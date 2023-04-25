package edu.wpi.fishfolk;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import java.awt.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fapp extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    Fapp.primaryStage = primaryStage;

    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    primaryStage.getIcons().add(new Image(Fapp.class.getResourceAsStream("images/magikarp.png")));
    Fapp.rootPane = root;

    final Scene scene = new Scene(root);

    // primaryStage.setFullScreen(true);
    // primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESC"));
    // scene.getStylesheets().add("" + Fapp.class.getResource("Styles/style.css"));
    scene.getStylesheets().add(Fapp.class.getResource("Styles/style.css").toExternalForm());

    // scene.getStylesheets().add("../resources/edu/wpi/fishfolk/Styles/style.css");

    // primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESC"));
    primaryStage.setScene(scene);
    // primaryStage.setMaximized(true);
    // primaryStage.setHeight(1080);
    // primaryStage.setWidth(1920);
    primaryStage.show();

    Navigation.navigate(Screen.LOGIN);
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
