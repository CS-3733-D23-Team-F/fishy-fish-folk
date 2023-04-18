package edu.wpi.fishfolk.controllers;

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
  // @FXML MFXButton supplyNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton moveEditorNav;
  @FXML MFXButton mapEditorNav;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton serviceNav;

  @FXML MFXButton exitButton;

  @FXML MFXButton closeServiceNav;
  @FXML AnchorPane slider;
  // @FXML Text directionInstructions;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply, furnitureNav;

  @FXML
  public void initialize() {
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    furnitureNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FURNITURE_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    moveEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    signageNav.setOnMouseClicked(
        event -> Navigation.navigate(Screen.SIGNAGE)); // why is this repeated??
    // mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    // officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(
        event -> Navigation.navigate(Screen.MAP_EDITOR)); // why is this repeated??
    pathfindingNav.setOnMouseClicked(
        event -> Navigation.navigate(Screen.PATHFINDING)); // why is this repeated??
    exitButton.setOnMouseClicked(event -> System.exit(0));

    closeServiceNav.setVisible(false);
    closeServiceNav.setDisable(true);

    serviceNav.setOnMouseClicked(
        event -> {
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToY(490);
          slide.play();

          slider.setTranslateY(490);
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
}
