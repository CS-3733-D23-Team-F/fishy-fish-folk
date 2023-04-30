package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingScreenController implements Initializable {

  public Label loading;
  public static Label loadings;

  public ImageView evolved, evolving, defaultForm;
  public static ImageView evolvesf, evolvingf, defaultFormf;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    loadings = loading;
    evolvesf = evolved;
    evolvingf = evolving;
    defaultFormf = defaultForm;
  }

  public String checkFunctions() {
    final String[] message = {""};
    Thread t1 =
        new Thread(
            () -> {
              message[0] = "What?\nMagikarp is evolving!";
              Platform.runLater(() -> loadings.setText(message[0]));
              try {
                Thread.sleep(10000);

              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });

    Thread t2 =
        new Thread(
            () -> {
              message[0] = "Magikarp evolved into Fishy-Fish-Folks";
              Platform.runLater(
                  () -> {
                    loadings.setText(message[0]);

                    Duration zoomDuration = Duration.seconds(1);
                    Timeline timeline = new Timeline();
                    timeline.setCycleCount(Timeline.INDEFINITE);

                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), defaultFormf);
                    fadeTransition.setFromValue(0);
                    fadeTransition.setToValue(1);

                    double zoomFactor = 1.2;
                    KeyValue zoomOutValue1 =
                        new KeyValue(evolvingf.scaleXProperty(), 1 / zoomFactor);
                    KeyValue zoomOutValue2 =
                        new KeyValue(evolvesf.scaleYProperty(), 1 / zoomFactor);
                    KeyValue zoomInValue1 = new KeyValue(evolvingf.scaleXProperty(), zoomFactor);
                    KeyValue zoomInValue2 = new KeyValue(evolvesf.scaleYProperty(), zoomFactor);

                    KeyFrame zoomOutFrame =
                        new KeyFrame(zoomDuration, zoomOutValue1, zoomOutValue2);
                    KeyFrame zoomInFrame =
                        new KeyFrame(zoomDuration.multiply(2), zoomInValue1, zoomInValue2);

                    timeline.getKeyFrames().addAll(zoomOutFrame, zoomInFrame);


                    timeline.play();
                  });
              try {
                Thread.sleep(10000);

              } catch (Exception e) {
                e.printStackTrace();
              }
            });

    Thread t3 =
        new Thread(
            () -> {
              Platform.runLater(
                  new Runnable() {
                    @Override
                    public void run() {
                      try {
                        Thread.sleep(1000);
                        final FXMLLoader loader =
                            new FXMLLoader(Fapp.class.getResource("views/Root.fxml"));
                        final BorderPane root = loader.load();
                        root.getLeft().setDisable(true);
                        root.getLeft().setVisible(false);
                        root.getTop().setDisable(true);
                        root.getTop().setVisible(false);
                        Fapp.rootPane = root;

                        final Scene scene = new Scene(root);
                        scene
                            .getStylesheets()
                            .add(Fapp.class.getResource("Styles/style.css").toExternalForm());
                        Stage stage = new Stage();

                        stage.setScene(scene);
                        stage
                            .getIcons()
                            .add(new Image(Fapp.class.getResourceAsStream("images/magikarp.png")));
                        // primaryStage.setMaximized(true);
                        stage.setWidth(1536);
                        stage.setHeight(864);
                        Fapp.primaryStage = stage;

                        Navigation.navigate(Screen.LOGIN);
                        root.getLeft().setDisable(true);
                        root.getLeft().setVisible(false);
                        root.getTop().setDisable(true);
                        root.getTop().setVisible(false);

                      } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                      }
                    }
                  });
            });

    System.out.println(
        "state: " + t3.getState() + " t1: " + t1.getState() + " t2 " + t2.getState());
    try {

      t1.start();
      t3.start();
      if (t1.getState() == Thread.State.WAITING) System.out.println("deadge");
      System.out.println(
          "state: " + t3.getState() + " t1: " + t1.getState() + " t2 " + t2.getState());
      t1.join();
      System.out.println(
          "state: " + t3.getState() + " t1: " + t1.getState() + " t2 " + t2.getState());

      t3.join();
      t2.start();
      t2.join();
      System.out.println(
          "state: " + t3.getState() + " t1: " + t1.getState() + " t2 " + t2.getState());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return message[0];
  }
}
