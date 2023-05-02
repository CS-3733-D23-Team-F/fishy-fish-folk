package edu.wpi.fishfolk.navigation;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.SharedResources;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class Navigation {

  public static void navigate(final Screen screen) {
    final String filename = screen.getFilename();

    try {
      final var resource = Fapp.class.getResource(filename);
      final FXMLLoader loader = new FXMLLoader(resource);
      Fapp.getRootPane().getLeft().setDisable(false);
      Fapp.getRootPane().getLeft().setVisible(true);
      Fapp.getRootPane().getTop().setDisable(false);
      Fapp.getRootPane().getTop().setVisible(true);

      Fapp.getRootPane().setCenter(loader.load());
      Node leftNode = Fapp.getRootPane().getLeft();
      Node topNode = Fapp.getRootPane().getTop();
      if (leftNode != null || topNode != null) {
        Fapp.getRootPane().getChildren().remove(leftNode);
        Fapp.getRootPane().getChildren().remove(topNode);
        Fapp.getRootPane().setLeft(leftNode);
        Fapp.getRootPane().setTop(topNode);
      }
      Label username = (Label) Fapp.getRootPane().getTop().lookup("#username");
      Label welcome = (Label) Fapp.getRootPane().getTop().lookup("#welcome");

      if (screen.equals(Screen.ADMIN_DASHBOARD) || screen.equals(Screen.STAFF_DASHBOARD)) {
        username.setVisible(true);
        welcome.setVisible(true);
        username.setText("" + SharedResources.getCurrentUser().getUsername());
      } else {
        username.setVisible(false);
        welcome.setVisible(false);
      }
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }
}
