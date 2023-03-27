package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class HomeController {

  // @FXML MFXButton navigateButton;

  @FXML MFXButton signageNav;
  @FXML MFXButton mealNav;
  @FXML MFXButton officeNav;

  @FXML
  public void initialize() {
    // navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    signageNav.setOnMouseClicked(event -> System.out.println("it was clicked"));
    // signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
  }
}
