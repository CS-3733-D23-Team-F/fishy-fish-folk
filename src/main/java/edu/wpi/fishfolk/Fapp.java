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

  @Setter @Getter public static Stage primaryStage;
  @Setter @Getter public static BorderPane rootPane;

  @Override
  public void init() throws Exception {
    log.info("Starting Up");
    /* the fish :(
      LoadingScreenController loadingScreen = new LoadingScreenController();
      loadingScreen.checkFunctions();
    */
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    // Fapp.primaryStage = primaryStage;
    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();
    root.getLeft().setDisable(true);
    root.getLeft().setVisible(false);
    root.getTop().setDisable(true);
    root.getTop().setVisible(false);
    Fapp.rootPane = root;

    final Scene scene = new Scene(root);
    scene.getStylesheets().add(Fapp.class.getResource("Styles/style.css").toExternalForm());
    Stage stage = new Stage();

    stage.setScene(scene);
    stage.getIcons().add(new Image(Fapp.class.getResourceAsStream("images/magikarp.png")));
    // primaryStage.setMaximized(true);
    stage.setMaximized(true);
    Fapp.primaryStage = stage;
    this.primaryStage.show();
    Navigation.navigate(Screen.LOGIN);
    root.getLeft().setDisable(true);
    root.getLeft().setVisible(false);
    root.getTop().setDisable(true);
    root.getTop().setVisible(false);

    // root.setLeft(roots.getServiceBar());

  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
