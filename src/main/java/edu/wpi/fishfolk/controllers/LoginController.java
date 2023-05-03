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
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class LoginController extends AbsController {
  @FXML MFXButton loginBtn, GuestLoginBtn;
  @FXML MFXTextField loginIDField;
  @FXML MFXPasswordField loginPassField;
  @FXML Label errorBox;
  private static TranslateTransition thugShaker;

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
      setToBlue();
    }
  }

  /** Clear the error box after a new input */
  private void clearError() {
    errorBox.setText("");
    errorBox.setVisible(false);
  }

  /** Sets all borders back to blue */
  public void setToBlue() {
    loginIDField.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 0; -fx-border-width: 0 0 0.5 0;");
    loginPassField.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 0; -fx-border-width: 0 0 0.5 0;");
  }

  /**
   * Creates an error popup for the given values.
   *
   * @param error the error message you want to present.
   * @param node the area it will pop up next to.
   */
  private void submissionError(String error, Node node) {
    node.setStyle("-fx-border-color: red; -fx-border-radius: 0; -fx-border-width: 0 0 0.5 0;");
    if (thugShaker == null || thugShaker.getNode() != node) {
      thugShaker = new TranslateTransition(Duration.millis(100), node);
    }
    thugShaker.setFromX(0f);
    thugShaker.setCycleCount(4);
    thugShaker.setAutoReverse(true);
    thugShaker.setByX(15f);
    thugShaker.playFromStart();
    errorBox.setText(error);
    errorBox.setVisible(true);
    errorBox.setStyle("-fx-text-fill:  red; -fx-text-alignment: center;");
    errorBox.setFont(Font.font("Open Sans", 15.0));
  }

  /**
   * Checks if the username and password provided by the user match an account in the database. If
   * it does, stores the currently logged in account to AbsController.
   */
  private void attemptLogin() {
    String loginID = loginIDField.getText();
    String password = loginPassField.getText();

    if (!loginID.matches("^[a-zA-Z0-9._]+$")) {
      submissionError(
          "Invalid username: Please only use a-Z, 0-9, dots, and underlines.", loginIDField);
      return;
    }

    if (!password.matches("^[a-zA-Z0-9._]*$")) { // empty is technically allowed!
      submissionError(
          "Invalid password: Please only use a-Z, 0-9, dots, and underlines.", loginPassField);
      return;
    }

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
      submissionError("Account not found.", loginIDField);
    } else {
      if (SharedResources.login(foundAccount, password)) {
        // valid account. We're already logged in if we get here!
        errorBox.setText("Logged in successfully!");
        errorBox.setStyle("-fx-alignment: center; -fx-text-fill:  green;");
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
        submissionError("Incorrect password.", loginPassField);
      }
    }
  }
}
