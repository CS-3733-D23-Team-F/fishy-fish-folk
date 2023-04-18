package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class HomeController {

  // @FXML MFXButton navigateButton;

  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton supplyNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton exitButton;
  @FXML AnchorPane slider;
  @FXML MFXButton serviceNav;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton closeServiceNav;
  @FXML MFXButton furnitureNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton conferenceNav;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton homeButton;
  @FXML ImageView pathfindingIcon;
  @FXML ImageView mealIcon;
  @FXML ImageView supplyIcon;
  @FXML ImageView mapIcon;
  @FXML ImageView signageIcon;
  @FXML ImageView orderIcon;

  @FXML
  public void initialize() {
    // navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));

    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    supplyNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    closeServiceNav.setVisible(false);
    closeServiceNav.setDisable(true);

    serviceNav.setOnMouseClicked(
        event -> {
          // menuWrap.setDisable(false);
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToY(490);
          slide.play();

          // slider.setTranslateY(-400);
          slider.setTranslateY(490);
          // menuWrap.setVisible(true);
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
