package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.ui.FoodOrder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ViewFoodOrdersController {
  @FXML Text itemsText;
  @FXML AnchorPane itemsTextContainer;
  @FXML Text deliveryRoomText, deliveryTimeText, statusText;
  @FXML MFXTextField assigneeText;

  int currentOrderNumber;
  List<FoodOrder> foodOrders;

  @FXML
  private void initialize() {
    currentOrderNumber = 0;
    loadOrders();

  }

  private void loadOrders() {
    foodOrders = new ArrayList<FoodOrder>();
    // TODO load food orders from database
  }

  private void updateDisplay() {
    if (foodOrders.size() > 0) {
      deliveryRoomText.setText(foodOrders.get(currentOrderNumber).deliveryLocation.toString());
      deliveryTimeText.setText(foodOrders.get(currentOrderNumber).deliveryTime.format(DateTimeFormatter.ofPattern("h:ma, EE, MM/dd")));
      String itemsTextContent = foodOrders.get(currentOrderNumber).toString();
      itemsTextContent = itemsTextContent.substring(0, itemsTextContent.indexOf("Total Cost: ") - 1);
      itemsText.setText(itemsTextContent);
      int numLines = itemsTextContent.split("\n").length + 1;
      itemsTextContainer.setPrefHeight(64 * numLines);
    } else {

    }
  }
}
