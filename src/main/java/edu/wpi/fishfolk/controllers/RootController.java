package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RootController {
  @FXML MFXButton signageNav;
  @FXML MFXButton mealNav;
  @FXML MFXButton supplyNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton conferenceNav;
  @FXML MFXButton switchAccsButton;
  @FXML MFXButton AccManagerBtn;
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
  @FXML MFXButton aboutButton;
  @FXML MFXButton creditButton;
  @FXML StackPane sidebar;
  @FXML HBox serviceBox;
  @FXML VBox buttonsBox;
  // @FXML MFXButton moveEditorNav;
  // @FXML AnchorPane sideBar;

  @FXML
  public void initialize() throws IOException {

    updatePermissionsAccess();

    SharedResources.setRootController(this);

    viewOrders.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_MASTER_ORDER));
    creditButton.setOnMouseClicked(event -> Navigation.navigate(Screen.CREDITS));
    aboutButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ABOUTME));
    flowerNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FLOWER_REQUEST));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.NEW_FOOD_ORDER));
    supplyNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    furnitureNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FURNITURE_REQUEST));
    conferenceNav.setOnMouseClicked(event -> Navigation.navigate(Screen.CONFERENCE));

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
    switchAccsButton.setOnMouseClicked(event -> accSwitch());
    AccManagerBtn.setOnMouseClicked(event -> Navigation.navigate(Screen.ACCOUNT_MANAGER));
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.NEW_SIGNAGE));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    homeButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));

    setupServiceNavButton();
    /*
       serviceNav.setOnMouseExited(
           event -> {
             serviceBar.setVisible(false);
             serviceBar.setDisable(true);
           });
    */
  }

  public void setupServiceNavButton() {
    serviceNav.setOnMouseClicked(
        event -> {
          serviceNav.setStyle(
              "-fx-background-color: #0e4675;\n"
                  + "    -fx-border-color: transparent transparent transparent #F0BF4C;\n"
                  + "    -fx-border-width: 4px;");
          serviceBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          buttonsBox.setAlignment(Pos.TOP_LEFT);
          serviceNav.setOnMouseClicked(
              event2 -> {
                serviceNav.setStyle(null);
                serviceBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                serviceBar.setVisible(false);
                serviceBar.setDisable(true);
                buttonsBox.setAlignment(Pos.TOP_RIGHT);
                setupServiceNavButton();
              });
        });
  }

  /** logs user out of their current account and brings them back to the login screen */
  public void accSwitch() {
    SharedResources.logout();
    Navigation.navigate(Screen.LOGIN);
  }

  /** updates what permissions you have access to depending on what account you're signed into */
  public void updatePermissionsAccess() {
    switch (SharedResources.getCurrentUser().getLevel()) {
      case GUEST:
        // Features that are inaccessible
        serviceNav.setDisable(true);
        flowerNav.setDisable(true);
        furnitureNav.setDisable(true);
        supplyNav.setDisable(true);
        mealNav.setDisable(true);
        viewOrders.setDisable(true);
        AccManagerBtn.setDisable(true);
        serviceNav.setVisible(false);
        flowerNav.setVisible(false);
        furnitureNav.setVisible(false);
        supplyNav.setVisible(false);
        mealNav.setVisible(false);
        viewOrders.setVisible(false);
        AccManagerBtn.setVisible(false);
        mapEditorNav.setDisable(true);
        mapEditorNav.setVisible(false);
        conferenceNav.setDisable(true);
        conferenceNav.setVisible(false);
        break;
      case STAFF:
        // Features that are inaccessible
        mapEditorNav.setDisable(true);
        mapEditorNav.setVisible(false);
        AccManagerBtn.setDisable(true);
        AccManagerBtn.setVisible(false);
        viewOrders.setVisible(false);
        viewOrders.setDisable(true);

        // Features that are accessible
        serviceNav.setDisable(false);
        flowerNav.setDisable(false);
        furnitureNav.setDisable(false);
        supplyNav.setDisable(false);
        mealNav.setDisable(false);
        serviceNav.setVisible(true);
        flowerNav.setVisible(true);
        furnitureNav.setVisible(true);
        supplyNav.setVisible(true);
        mealNav.setVisible(true);
        conferenceNav.setDisable(false);
        conferenceNav.setVisible(true);
        break;
      case ADMIN:
        // Features that are accessible
        serviceNav.setDisable(false);
        flowerNav.setDisable(false);
        furnitureNav.setDisable(false);
        supplyNav.setDisable(false);
        mealNav.setDisable(false);
        viewOrders.setDisable(false);
        AccManagerBtn.setDisable(false);
        serviceNav.setVisible(true);
        flowerNav.setVisible(true);
        furnitureNav.setVisible(true);
        supplyNav.setVisible(true);
        mealNav.setVisible(true);
        viewOrders.setVisible(true);
        AccManagerBtn.setVisible(true);
        mapEditorNav.setDisable(false);
        mapEditorNav.setVisible(true);
        conferenceNav.setDisable(false);
        conferenceNav.setVisible(true);
        break;
      case ROOT:
        // Features that are accessible
        serviceNav.setDisable(false);
        flowerNav.setDisable(false);
        furnitureNav.setDisable(false);
        supplyNav.setDisable(false);
        mealNav.setDisable(false);
        viewOrders.setDisable(false);
        AccManagerBtn.setDisable(false);
        serviceNav.setVisible(true);
        flowerNav.setVisible(true);
        furnitureNav.setVisible(true);
        supplyNav.setVisible(true);
        mealNav.setVisible(true);
        viewOrders.setVisible(true);
        AccManagerBtn.setVisible(true);
        mapEditorNav.setDisable(false);
        mapEditorNav.setVisible(true);
        conferenceNav.setDisable(false);
        conferenceNav.setVisible(true);
        break;
    }
  }
}
