package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.database.TableEntry.UserAccount;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class LoginController extends AbsController {
  @FXML MFXButton loginBtn, GuestLoginBtn;
  @FXML MFXTextField loginIDField;
  @FXML MFXPasswordField loginPassField;
  @FXML Label errorBox;

  /** Initialize state and set event handlers. */
  @FXML
  private void initialize() {
    Fapp.getRootPane().getLeft().setDisable(true);
    Fapp.getRootPane().getLeft().setVisible(false);
    Fapp.getRootPane().getTop().setDisable(true);
    Fapp.getRootPane().getTop().setVisible(false);
    loginBtn.setOnMouseClicked(event -> attemptLogin());
    GuestLoginBtn.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
    loginPassField.setOnKeyReleased(this::attemptLoginOnEnterPressed);
    loginIDField.setOnKeyReleased(this::attemptLoginOnEnterPressed);
    errorBox.setText("");
    errorBox.setVisible(false);

    Platform.runLater(() -> loginIDField.requestFocus());
  }

  /**
   * Check on every key press if the key was Enter. If it was, attempt to log in.
   *
   * @param keyEvent the key event to check.
   */
  private void attemptLoginOnEnterPressed(KeyEvent keyEvent) {
    if (keyEvent.getCode().getCode() == 10) {
      attemptLogin();
    } else {
      clearError();
    }
  }

  /** Clear the error box after a new input */
  private void clearError() {
    errorBox.setText("");
    errorBox.setVisible(false);
  }

  /**
   * Checks if the username and password provided by the user match an account in the database. If
   * it does, stores the currently logged in account to AbsController.
   */
  private void attemptLogin() {
    String loginID = loginIDField.getText();
    String password = loginPassField.getText();

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
      errorBox.setVisible(true);
      errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
    } else {
      if (SharedResources.login(foundAccount, password)) {
        // valid account. We're already logged in if we get here!
        errorBox.setText("Logged in successfully!");
        errorBox.setStyle("-fx-alignment: center; -fx-background-color:  green;");
        System.out.println("perm: " + SharedResources.getCurrentUser().getLevel());
        switch (SharedResources.getCurrentUser().getLevel()) {
          case GUEST:
            Navigation.navigate(Screen.SIGNAGE);
            break;
          case STAFF:
            Navigation.navigate(Screen.STAFF_DASHBOARD);
            break;
          case ADMIN:
            Navigation.navigate(Screen.ADMIN_DASHBOARD);
            break;
          case ROOT:
            Navigation.navigate(Screen.ADMIN_DASHBOARD);
            break;
        }
      } else {
        errorBox.setText("Incorrect password.");
        errorBox.setVisible(true);
        errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
      }
    }
  }
}
