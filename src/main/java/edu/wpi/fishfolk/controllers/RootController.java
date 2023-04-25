package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RootController {
  @FXML MFXButton signageNav;
  @FXML MFXButton mealNav;
  @FXML MFXButton supplyNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML VBox serviceBar;
  @FXML MFXButton serviceNav;
  @FXML MFXButton exitButton;
  // @FXML MFXButton switchAccountButton;
  // @FXML MFXButton accountManagerNav;
  @FXML MFXButton homeButton;
  //  @FXML MFXButton closeServiceNav;
  // @FXML AnchorPane slider;
  // @FXML Text directionInstructions;
  @FXML MFXButton viewOrders;
  @FXML MFXButton furnitureNav;
  @FXML StackPane sidebar;

  // @FXML MFXButton moveEditorNav;
  // @FXML AnchorPane sideBar;

  @FXML
  public void initialize() throws IOException {

    updatePermissionsAccess();

    SharedResources.setRootController(this);

    viewOrders.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_MASTER_ORDER));

    flowerNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FLOWER_REQUEST));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.NEW_FOOD_ORDER));
    supplyNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    furnitureNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FURNITURE_REQUEST));

    // accountManagerNav.setOnMouseClicked(event -> Navigation.navigate(Screen.ACCOUNT_MANAGER));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    /*
    switchAccountButton.setOnMouseClicked(
        event -> {
          SharedResources.logout();
          Navigation.navigate(Screen.LOGIN);
        });


    */
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    // moveEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));

    serviceNav.setOnMouseEntered(
        event -> {
          serviceNav.setStyle(
              "-fx-background-color: #0e4675;\n"
                  + "    -fx-border-color: transparent transparent transparent #F0BF4C;\n"
                  + "    -fx-border-width: 4px;");
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          serviceBar.setOnMouseEntered(
              event1 -> {
                serviceBar.setVisible(true);
                serviceBar.setDisable(false);
              });
          serviceBar.setOnMouseExited(
              event1 -> {
                serviceBar.setVisible(false);
                serviceBar.setDisable(true);
                serviceNav.setStyle(null);
              });
        });
    serviceNav.setOnMouseExited(
        event -> {
          serviceBar.setVisible(false);
          serviceBar.setDisable(true);
        });

    /*
       serviceNav.setOnMouseExited(
           event -> {
             serviceBar.setVisible(false);
             serviceBar.setDisable(true);
           });
    */
  }

  public void updatePermissionsAccess() {
    viewOrders.setDisable(false);

    flowerNav.setDisable(false);
    mealNav.setDisable(false);
    furnitureNav.setDisable(false);
    supplyNav.setDisable(false);
    serviceNav.setDisable(false);

    signageNav.setDisable(false);
    // accountManagerNav.setDisable(false);
    pathfindingNav.setDisable(false);

    // moveEditorNav.setDisable(false);
    mapEditorNav.setDisable(false);

    switch (SharedResources.getCurrentUser().getLevel()) {
      case GUEST:
        serviceNav.setDisable(true);
        flowerNav.setDisable(true);
        furnitureNav.setDisable(true);
        supplyNav.setDisable(true);
        mealNav.setDisable(true);
        viewOrders.setDisable(true);
      case STAFF:
        mapEditorNav.setDisable(true);
        // accountManagerNav.setDisable(true);
        // moveEditorNav.setDisable(true);
      case ADMIN:
      case ROOT:
        break;
    }
  }
}
