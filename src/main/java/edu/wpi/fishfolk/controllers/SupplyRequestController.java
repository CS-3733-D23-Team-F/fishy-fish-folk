package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.SupplyItem;
import edu.wpi.fishfolk.ui.SupplyOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class SupplyRequestController {
  // SupplyOrder currentSupplyOrder;
  SupplyOrder currentSupplyOrder = new SupplyOrder();
  ArrayList<SupplyItem> supplyOptions;
  @FXML MFXButton cancelButton;
  @FXML MFXButton supplySubmitButton;
  @FXML MFXButton clearButton;
  @FXML MFXCheckbox check1, check2, check3, check4, check5, check6, check7;
  @FXML MFXTextField linkTextField, roomNumTextField, notesTextField;

  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;

  @FXML MFXButton sideBar;

  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;

  @FXML
  public void initialize() {
    loadOptions();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    supplySubmitButton.setOnMouseClicked(event -> submit());
    clearButton.setOnMouseClicked(event -> clearAllFields());

    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    slider.setTranslateX(-400);
    sideBarClose.setVisible(false);

    sideBar.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToX(0);
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

  void loadOptions() {
    supplyOptions = new ArrayList<SupplyItem>();
    supplyOptions.add(SupplyItem.supply1);
    supplyOptions.add(SupplyItem.supply2);
    supplyOptions.add(SupplyItem.supply3);
    supplyOptions.add(SupplyItem.supply4);
    supplyOptions.add(SupplyItem.supply5);
    supplyOptions.add(SupplyItem.supply6);
    supplyOptions.add(SupplyItem.supply7);
    supplyOptions.add(SupplyItem.supply8);
  }

  private void addToOrder(int supplyNum) {
    SupplyItem supply = supplyOptions.get(supplyNum);
    currentSupplyOrder.addSupply(supply);
  }

  // private void addTextFields() {}

  private void clearAllFields() {
    clearChecks();
    clearTextFields();
  }

  private void clearTextFields() {
    linkTextField.clear();
    roomNumTextField.clear();
    notesTextField.clear();
  }

  private void clearChecks() {
    check1.setSelected(false);
    check2.setSelected(false);
    check3.setSelected(false);
    check4.setSelected(false);
    check5.setSelected(false);
    check6.setSelected(false);
    check7.setSelected(false);
  }

  private boolean submittable() {
    if (check1.isSelected()
        || check2.isSelected()
        || check3.isSelected()
        || check4.isSelected()
        || check5.isSelected()
        || check6.isSelected()
        || check7.isSelected()
        || (!(currentSupplyOrder.link == ""))) {
      System.out.println("Sufficient fields filled");
      return true;
    } else {
      System.out.println("Sufficient fields not filled");
      return false;
    }
  }

  private void submit() {
    if (check1.isSelected()) addToOrder(0);
    else addToOrder(7);
    if (check2.isSelected()) addToOrder(1);
    else addToOrder(7);
    if (check3.isSelected()) addToOrder(2);
    else addToOrder(7);
    if (check4.isSelected()) addToOrder(3);
    else addToOrder(7);
    if (check5.isSelected()) addToOrder(4);
    else addToOrder(7);
    if (check6.isSelected()) addToOrder(5);
    else addToOrder(7);
    if (check7.isSelected()) addToOrder(6);
    else addToOrder(7);
    currentSupplyOrder.roomNum = roomNumTextField.getText();
    currentSupplyOrder.link = linkTextField.getText();
    currentSupplyOrder.notes = notesTextField.getText();
    if (submittable()) {
      System.out.println(currentSupplyOrder.toString());
      Navigation.navigate(Screen.HOME);
    }
  }
}
