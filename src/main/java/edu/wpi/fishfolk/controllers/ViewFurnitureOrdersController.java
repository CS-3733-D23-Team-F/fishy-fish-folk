package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.ui.FormStatus.cancelled;
import static edu.wpi.fishfolk.ui.FormStatus.filled;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.FurnitureOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ViewFurnitureOrdersController extends AbsController {
  @FXML Text itemsText;
  @FXML AnchorPane itemsTextContainer;
  @FXML Text deliveryRoomText, deliveryTimeText, statusText;
  @FXML TextField assigneeText;
  @FXML MFXButton prevOrderButton, nextOrderButton;
  @FXML MFXButton cancelButton, filledButton, removeButton;
  @FXML Text viewingNumberText;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;

  @FXML MFXButton sideBar, viewFurniture, furnitureNav;

  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML MFXButton homeButton;

  int currentOrderNumber;
  List<FurnitureOrder> furnitureOrders;

  Table furnitureOrderTable;

  public ViewFurnitureOrdersController() {
    super();
    furnitureOrderTable = new Table(dbConnection.conn, "furnitureorder");
    furnitureOrderTable.init(false);
    furnitureOrderTable.addHeaders(
        FurnitureOrderController.headers,
        new ArrayList<>(
            List.of(
                "String", "String", "String", "String", "String", "String", "String", "String")));
  }

  @FXML
  private void initialize() throws InterruptedException {
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
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

    currentOrderNumber = 0;
    prevOrderButton.setOnAction(event -> prevOrder());
    nextOrderButton.setOnAction(event -> nextOrder());
    prevOrderButton.setDisable(true);
    loadOrders();
    if (furnitureOrders.size() < 2) nextOrderButton.setDisable(true);
    assigneeText.setOnKeyReleased(event -> updateAssignee());
    cancelButton.setOnAction(event -> cancelOrder());
    filledButton.setOnAction(event -> fillOrder());
    removeButton.setOnAction(event -> removeOrder());
    updateDisplay();
  }

  private void updateAssignee() {
    FurnitureOrder currentOrder = furnitureOrders.get(currentOrderNumber);
    currentOrder.assignee = assigneeText.getText();
    // updateDisplay();
    furnitureOrderTable.update("id", currentOrder.formID, "assignee", currentOrder.assignee);
  }

  private void prevOrder() {
    currentOrderNumber--;
    nextOrderButton.setDisable(false);
    if (currentOrderNumber == 0) prevOrderButton.setDisable(true);
    updateDisplay();
  }

  private void nextOrder() {
    currentOrderNumber++;
    prevOrderButton.setDisable(false);
    if (currentOrderNumber == furnitureOrders.size() - 1) nextOrderButton.setDisable(true);
    updateDisplay();
  }

  private void loadOrders() throws InterruptedException {
    furnitureOrders = new ArrayList<FurnitureOrder>();

    ArrayList<String[]> tableOrders = furnitureOrderTable.getAll();
    for (String[] tableEntry : tableOrders) {
      FurnitureOrder order = new FurnitureOrder();
      ArrayList<String> entry = new ArrayList<String>();
      entry.addAll(List.of(tableEntry));
      order.construct(entry);
      furnitureOrders.add(order);
      System.out.println(order.deconstruct());
    }
  }

  private void cancelOrder() {
    FurnitureOrder currentOrder = furnitureOrders.get(currentOrderNumber);
    currentOrder.formStatus = cancelled;
    furnitureOrderTable.update("id", currentOrder.formID, "status", "Cancelled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void fillOrder() {
    FurnitureOrder currentOrder = furnitureOrders.get(currentOrderNumber);
    currentOrder.formStatus = filled;
    furnitureOrderTable.update("id", currentOrder.formID, "status", "Filled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void removeOrder() {
    FurnitureOrder currentOrder = furnitureOrders.get(currentOrderNumber);
    furnitureOrderTable.remove("id", currentOrder.formID);
    furnitureOrders.remove(currentOrderNumber);
    if (currentOrderNumber != 0) {
      currentOrderNumber--;
      if (currentOrderNumber == 0) {
        prevOrderButton.setDisable(true);
      }
    } else {
      if (furnitureOrders.size() == 0 || furnitureOrders.size() == 1) {
        nextOrderButton.setDisable(true);
      }
    }
    updateDisplay();
  }

  String addLineBreaks(String input) {
    if (input == null) return "";
    if (input.equals("")) return "";
    String[] words = input.split(" ");
    String output = words[0];
    for (int i = 1; i < words.length; i++) {
      if (words[i].length() + (output.length() - output.lastIndexOf('\n')) > 18) {
        output += "\n" + words[i];
      } else {
        output += " " + words[i];
      }
    }
    return output;
  }

  private void updateDisplay() {
    if (furnitureOrders.size() > 0) {
      deliveryRoomText.setText(furnitureOrders.get(currentOrderNumber).roomNum);
      // linkText.setText(furnitureOrders.get(currentOrderNumber).link);
      // String itemsTextContent =
      // supplyOrders.get(currentOrderNumber).listItemsToString().replace("-_-", "\n");
      String itemsTextContent =
          furnitureOrders.get(currentOrderNumber).furnitureItem.furnitureName
              + "\n\nService Type: \n"
              + furnitureOrders.get(currentOrderNumber).serviceType
              + "\n\nNotes:\n"
              + addLineBreaks(furnitureOrders.get(currentOrderNumber).notes);
      itemsText.setText(itemsTextContent);
      deliveryTimeText.setText(furnitureOrders.get(currentOrderNumber).deliveryDate);
      int numLines = itemsTextContent.split("\n").length + 1;
      itemsTextContainer.setPrefHeight(60 * numLines);
      assigneeText.setText(furnitureOrders.get(currentOrderNumber).assignee);
      switch (furnitureOrders.get(currentOrderNumber).formStatus) {
        case filled:
          {
            statusText.setText("Filled");
            break;
          }
        case cancelled:
          {
            statusText.setText("Cancelled");
            break;
          }
        case submitted:
          {
            statusText.setText("Submitted");
            break;
          }
      }
      viewingNumberText.setText(
          "Viewing order #" + (currentOrderNumber + 1) + " out of " + furnitureOrders.size());
      if (statusText.getText().equals("Submitted")) {
        cancelButton.setDisable(false);
        filledButton.setDisable(false);
        System.out.println("This is not fine");
      } else {
        cancelButton.setDisable(true);
        filledButton.setDisable(true);
        System.out.println("Why am i not here, just to enjoy?");
      }
      removeButton.setDisable(false);
    } else {
      itemsText.setText("");
      itemsTextContainer.setPrefHeight(0);
      deliveryRoomText.setText("");
      assigneeText.setText("");
      deliveryTimeText.setText("");
      statusText.setText("");
      viewingNumberText.setText("No orders to view");
      cancelButton.setDisable(true);
      filledButton.setDisable(true);
      removeButton.setDisable(true);
    }
  }
}
