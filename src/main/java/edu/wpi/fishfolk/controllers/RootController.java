package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class RootController {
  @FXML MFXButton signageNav;
  @FXML MFXButton mealNav;
  @FXML MFXButton supplyNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton serviceNav;
  @FXML MFXButton exitButton;
  @FXML MFXButton switchAccountButton;
  @FXML MFXButton homeButton;
  @FXML MFXButton closeServiceNav;
  @FXML AnchorPane slider;
  // @FXML Text directionInstructions;
  @FXML MFXButton viewOrders;
  @FXML MFXButton furnitureNav;

  @FXML
  public void initialize() {
    updatePermissionsAccess();

    SharedResources.setRootController(this);

    viewOrders.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_MASTER_ORDER));

    flowerNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FLOWER_REQUEST));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.NEW_FOOD_ORDER));
    supplyNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    furnitureNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FURNITURE_REQUEST));

    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    switchAccountButton.setOnMouseClicked(
        event -> {
          SharedResources.logout();
          Navigation.navigate(Screen.LOGIN);
        });

    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    closeServiceNav.setVisible(false);
    closeServiceNav.setDisable(true);

    serviceNav.setOnMouseClicked(
        event -> {
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToY(420);
          slide.play();

          slider.setTranslateY(420);
          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(false);
                closeServiceNav.setVisible(true);
                serviceNav.setDisable(true);
                closeServiceNav.setDisable(false);
              });
        });

    closeServiceNav.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToY(0);
          slide.play();

          slider.setTranslateY(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(true);
                closeServiceNav.setVisible(false);
                serviceNav.setDisable(false);
                closeServiceNav.setDisable(true);
                serviceBar.setVisible(false);
                serviceBar.setDisable(true);
              });
        });
  }

  public void updatePermissionsAccess() {
    viewOrders.setDisable(false);

    flowerNav.setDisable(false);
    mealNav.setDisable(false);
    furnitureNav.setDisable(false);
    supplyNav.setDisable(false);

    signageNav.setDisable(false);
    mapEditorNav.setDisable(false);

    switch (SharedResources.getCurrentUser().getLevel()) {
      case GUEST:
        flowerNav.setDisable(true);
        furnitureNav.setDisable(true);
        supplyNav.setDisable(true);
        mealNav.setDisable(true);
        viewOrders.setDisable(true);
      case STAFF:
        mapEditorNav.setDisable(true);
      case ADMIN:
      case ROOT:
        break;
    }
  }
}
