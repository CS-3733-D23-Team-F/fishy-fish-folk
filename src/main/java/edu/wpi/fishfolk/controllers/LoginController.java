package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.rewrite.TableEntry.TableEntryType;
import edu.wpi.fishfolk.database.rewrite.TableEntry.UserAccount;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class LoginController extends AbsController {
  @FXML AnchorPane slider;
  @FXML MFXButton loginBtn;
  @FXML MFXTextField loginIDField;
  @FXML MFXPasswordField loginPassField;
  @FXML Label errorBox;

  /** Initialize state and set event handlers. */
  @FXML
  private void initialize() {
    loginBtn.setOnMouseClicked(loginHandler);
    errorBox.setText("");

    slider.setTranslateX(-400);
  }

  /**
   * Checks if the username and password provided by the user match an account in the database. If
   * it does, stores the currently logged in account to AbsController.
   */
  public final EventHandler<MouseEvent> loginHandler =
      event -> {
        String loginID = loginIDField.getText();
        String password = loginPassField.getText();
        //        System.out.print("LoginID: ");
        //        System.out.println(loginID);
        //        System.out.print("Passhash: ");
        //        System.out.println(passhash);

        List<UserAccount> userAccounts =
            (List<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);

        UserAccount foundAccount = null;
        for (int i = 0; i < userAccounts.size(); i++) {
          UserAccount a = userAccounts.get(i);
          if (a.getUsername().equals(loginID)) {
            foundAccount = a;
            break;
          }
        }

        if (foundAccount == null) {
          errorBox.setText("Account not found.");
          errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
        } else {
          if (SharedResources.login(foundAccount, password)) {
            // valid account. We're already logged in if we get here!
            errorBox.setText("Logged in successfully!");
            errorBox.setStyle("-fx-alignment: center; -fx-background-color:  green;");
            Navigation.navigate(Screen.HOME);
          } else {
            errorBox.setText("Incorrect password.");
            errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
          }
        }
      };
}
