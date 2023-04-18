package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.TableEntry;
import edu.wpi.fishfolk.database.rewrite.TableEntry.TableEntryType;
import edu.wpi.fishfolk.database.rewrite.TableEntry.UserAccount;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.database.rewrite.Fdb;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LoginController extends AbsController {
  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton sideBar;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML MFXButton homeButton;
  @FXML MFXButton loginBtn;
  @FXML MFXTextField loginIDField;
  @FXML MFXPasswordField loginPassField;
  @FXML Label errorBox;

  edu.wpi.fishfolk.database.rewrite.Fdb dbConnection = new Fdb();



  @FXML
  private void initialize() {
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    loginBtn.setOnMouseClicked(loginHandler);
    errorBox.setText("");

    slider.setTranslateX(-400);
    sideBarClose.setVisible(false);

    sideBar.setOnMouseClicked(
        event -> {
          menuWrap.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToX(400);
          slide.play();

          slider.setTranslateX(-400);

          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(false);
                sideBarClose.setVisible(true);
              });
        });

    sideBarClose.setOnMouseClicked(
        event -> {
          menuWrap.setDisable(true);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToX(-400);
          slide.play();

          slider.setTranslateX(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(true);
                sideBarClose.setVisible(false);
              });
        });
  }

  public final EventHandler<MouseEvent> loginHandler =
      event -> {
        String loginID = loginIDField.getText();
        int passhash =
            loginPassField.getText().hashCode(); // literally never store the original password
        System.out.print("LoginID: ");
        System.out.println(loginID);
        System.out.print("Passhash: ");
        System.out.println(passhash);

        List<UserAccount> userAccounts = (List<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);

        UserAccount foundAccount = null;
        for (int i = 0; i < userAccounts.size(); i++)
        {
            UserAccount a = userAccounts.get(i);
            if (a.getUsername().equals(loginID)) {
                foundAccount = a;
                break;
            }
        }

        if (foundAccount == null) {
            errorBox.setText("Incorrect username or password!");
            errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
        } else {
            if (foundAccount.getPassword().equals(String.valueOf(passhash))) {
                // valid account
                currUser = foundAccount;
            } else {
                errorBox.setText("Incorrect username or password!");
                errorBox.setStyle("-fx-alignment: center; -fx-background-color:  red;");
            }
        }
      };
}
