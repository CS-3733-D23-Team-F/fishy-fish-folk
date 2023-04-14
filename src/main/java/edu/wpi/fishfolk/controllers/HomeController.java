package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class HomeController {

  // @FXML MFXButton navigateButton;

  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton furnitureNav;
  @FXML MFXButton viewFurniture;

  @FXML MFXButton sideBar;

  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;

  @FXML MFXButton viewOrderButton;
  @FXML MFXButton signageButton;
  @FXML MFXButton pathfindingButton;
  @FXML MFXButton mapEditorButton;
  @FXML MFXButton homeButton;
  @FXML AnchorPane orderList;
  @FXML MFXButton orderBack;
  @FXML MFXButton supplyOrder;
  @FXML MFXButton foodOrder;
  @FXML MFXButton furnitureOrder;

  @FXML MFXButton serviceButton;
  @FXML MFXButton conferenceButton;

  @FXML MFXButton loginBtn;

  @FXML
  public void initialize() {
    conferenceButton.setDisable(true);
    signageButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    serviceButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    // conferenceButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    pathfindingButton.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    mapEditorButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    foodOrder.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    supplyOrder.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    furnitureOrder.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FURNITURE_ORDERS));
    loginBtn.setOnMouseClicked(event -> Navigation.navigate(Screen.LOGIN));

    viewOrderButton.setOnMouseClicked(
        event -> {
          viewOrderButton.setVisible(false);
          viewOrderButton.setDisable(true);
          orderList.setVisible(true);
          orderList.setDisable(false);
        });
    orderBack.setOnMouseClicked(
        event -> {
          viewOrderButton.setVisible(true);
          viewOrderButton.setDisable(false);
          orderList.setVisible(false);
          orderList.setDisable(true);
        });

    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    viewFurniture.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FURNITURE_ORDERS));
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    furnitureNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FURNITURE_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    slider.setTranslateX(-400);
    sideBarClose.setVisible(false);
    menuWrap.setVisible(false);
    sideBar.setOnMouseClicked(
        event -> {
          menuWrap.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToX(400);
          slide.play();

          slider.setTranslateX(-400);
          menuWrap.setVisible(true);
          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(false);
                sideBarClose.setVisible(true);
              });
        });

    sideBarClose.setOnMouseClicked(
        event -> {
          menuWrap.setVisible(false);
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
}
