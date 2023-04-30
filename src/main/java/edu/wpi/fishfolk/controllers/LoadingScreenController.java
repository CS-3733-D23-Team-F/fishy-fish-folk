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

  public ImageView evolved, evolving, defaultForm, evolvedA;
  public static ImageView evolvesf, evolvingf, defaultFormf, evolvedf;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    loadings = loading;
    evolvesf = evolved;
    evolvingf = evolving;
    defaultFormf = defaultForm;
    evolvedf = evolvedA;
  }

  public String checkFunctions() {
    final String[] message = {""};
    Thread t1 =
        new Thread(
            () -> {
              message[0] = "What?\nMAGIKARP is evolving!";
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
              message[0] = "What?\nMAGIKARP is evolving!";
              Timeline timeline = new Timeline();
              timeline.setCycleCount(1);
              Timeline timeline2 = new Timeline();
              timeline2.setCycleCount(1);
              Platform.runLater(
                  () -> {
                    loadings.setText(message[0]);

                    evolvesf.setVisible(false);
                    evolvingf.setVisible(false);

                    Duration zoomDuration = Duration.seconds(0.5);

                    FadeTransition fadeTransition =
                        new FadeTransition(Duration.seconds(0.5), defaultFormf);
                    fadeTransition.setFromValue(1);
                    fadeTransition.setToValue(0);
                    KeyValue keyValue = new KeyValue(defaultFormf.opacityProperty(), 0.0);
                    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), keyValue);
                    Timeline timeline1 = new Timeline(keyFrame);
                    ParallelTransition parallelTransition =
                        new ParallelTransition(fadeTransition, timeline1);
                    parallelTransition.play();

                    double zoomFactor = 1;

                    KeyValue zoomOutValue1 =
                        new KeyValue(evolvingf.scaleXProperty(), 1 - zoomFactor);
                    KeyValue zoomOutValue2 =
                        new KeyValue(evolvingf.scaleYProperty(), 1 - zoomFactor);
                    KeyValue zoomInValue1 = new KeyValue(evolvesf.scaleXProperty(), zoomFactor);
                    KeyValue zoomInValue2 = new KeyValue(evolvesf.scaleYProperty(), zoomFactor);

                    KeyValue zoomOutValue3 =
                        new KeyValue(evolvesf.scaleXProperty(), 1 - zoomFactor);
                    KeyValue zoomOutValue4 =
                        new KeyValue(evolvesf.scaleYProperty(), 1 - zoomFactor);
                    KeyValue zoomInValue3 = new KeyValue(evolvingf.scaleXProperty(), zoomFactor);
                    KeyValue zoomInValue4 = new KeyValue(evolvingf.scaleYProperty(), zoomFactor);

                    KeyFrame zoomOutFrame =
                        new KeyFrame(zoomDuration, zoomOutValue1, zoomOutValue2);
                    KeyFrame zoomInFrame =
                        new KeyFrame(zoomDuration.multiply(2), zoomInValue1, zoomInValue2);

                    KeyFrame zoomOutFrame2 =
                        new KeyFrame(zoomDuration, zoomOutValue3, zoomOutValue4);
                    KeyFrame zoomInFrame2 =
                        new KeyFrame(zoomDuration.multiply(2), zoomInValue3, zoomInValue4);

                    timeline.getKeyFrames().addAll(zoomOutFrame, zoomInFrame);

                    timeline2.getKeyFrames().addAll(zoomOutFrame2, zoomInFrame2);

                    parallelTransition.setOnFinished(
                        event -> {
                          evolvesf.setVisible(true);
                          evolvingf.setVisible(true);
                          timeline.play();
                          timeline.setOnFinished(event1 -> timeline2.play());
                          timeline2.setOnFinished(event1 -> timeline.play());
                        });
                  });
              try {
                Thread.sleep(10000);
                timeline2.stop();
                timeline.stop();
                evolvingf.setVisible(false);
                evolvesf.setVisible(false);
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

    Thread t4 =
        new Thread(
            () -> {
              message[0] = "Congratulations! Your MAGIKARP\nevolved into FISHY-FISH-FOLK";
              Platform.runLater(
                  () -> {
                    loadings.setText(message[0]);

                    evolvedf.setVisible(true);
                    evolvedf.toFront();
                    ScaleTransition scaleTransition =
                        new ScaleTransition(Duration.seconds(1), evolvedf);
                    scaleTransition.setFromX(0);
                    scaleTransition.setFromY(0);
                    scaleTransition.setToX(1);
                    scaleTransition.setToY(1);
                    KeyValue keyValue = new KeyValue(evolvedf.opacityProperty(), 1.0);
                    KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
                    Timeline timeline1 = new Timeline(keyFrame);
                    ParallelTransition parallelTransition =
                        new ParallelTransition(scaleTransition, timeline1);
                    parallelTransition.play();
                  });
              try {
                Thread.sleep(3000);

              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });

    try {

      t1.start();
      t3.start();
      t1.join();
      t3.join();
      t2.start();
      t2.join();
      t4.start();
      t4.join();

    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return message[0];
  }
}
