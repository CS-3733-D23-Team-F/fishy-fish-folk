package edu.wpi.fishfolk.navigation;

import edu.wpi.fishfolk.Fapp;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class Navigation {

  public static void navigate(final Screen screen) {
    final String filename = screen.getFilename();

    try {
      final var resource = Fapp.class.getResource(filename);
      final FXMLLoader loader = new FXMLLoader(resource);

      Fapp.getRootPane().setCenter(loader.load());
      Node leftNode = Fapp.getRootPane().getLeft();
      Node topNode = Fapp.getRootPane().getTop();
      if (leftNode != null || topNode != null) {
        Fapp.getRootPane().getChildren().remove(leftNode);
        Fapp.getRootPane().getChildren().remove(topNode);
        Fapp.getRootPane().setLeft(leftNode);
        Fapp.getRootPane().setTop(topNode);
      }

    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }
}
