package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.ui.FormStatus.*;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.ui.FoodOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ViewFoodOrdersController extends AbsController {
  @FXML Text itemsText;
  @FXML AnchorPane itemsTextContainer;
  @FXML Text deliveryRoomText, deliveryTimeText, statusText;
  @FXML TextField assigneeText;
  @FXML MFXButton prevOrderButton, nextOrderButton;
  @FXML MFXButton cancelButton, filledButton, removeButton;
  @FXML Text viewingNumberText;

  int currentOrderNumber;
  List<FoodOrder> foodOrders;

  Table foodOrderTable;

  public ViewFoodOrdersController() {
    super();
    foodOrderTable = new Table(dbConnection.conn, "foodorder");
    foodOrderTable.addHeaders(
        FoodOrderController.headers,
        new ArrayList<>(List.of("String", "String", "String", "String", "String", "String")));
    foodOrderTable.init(false);
  }

  @FXML
  private void initialize() throws InterruptedException {
    currentOrderNumber = 0;
    prevOrderButton.setOnAction(event -> prevOrder());
    nextOrderButton.setOnAction(event -> nextOrder());
    prevOrderButton.setDisable(true);
    loadOrders();
    if (foodOrders.size() < 2) nextOrderButton.setDisable(true);
    assigneeText.setOnKeyReleased(event -> updateAssignee());
    cancelButton.setOnAction(event -> cancelOrder());
    filledButton.setOnAction(event -> fillOrder());
    removeButton.setOnAction(event -> removeOrder());
    updateDisplay();
  }

  private void updateAssignee() {
    FoodOrder currentOrder = foodOrders.get(currentOrderNumber);
    currentOrder.assignee = assigneeText.getText();
    // updateDisplay();
    foodOrderTable.update(currentOrder.formID, "assignee", currentOrder.assignee);
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
    if (currentOrderNumber == foodOrders.size() - 1) nextOrderButton.setDisable(true);
    updateDisplay();
  }

  private void loadOrders() throws InterruptedException {
    foodOrders = new ArrayList<FoodOrder>();
    // TODO load food orders from database

    ArrayList<String>[] tableOrders = foodOrderTable.getAll();
    boolean headersHandled = false;
    for (ArrayList<String> tableEntry : tableOrders) {
      if (headersHandled) {
        FoodOrder order = new FoodOrder();
        order.construct(tableEntry);
        foodOrders.add(order);
      } else {
        headersHandled = true;
      }
    }
  }

  private void cancelOrder() {
    FoodOrder currentOrder = foodOrders.get(currentOrderNumber);
    currentOrder.formStatus = cancelled;
    foodOrderTable.update(currentOrder.formID, "status", "Cancelled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void fillOrder() {
    FoodOrder currentOrder = foodOrders.get(currentOrderNumber);
    currentOrder.formStatus = filled;
    foodOrderTable.update(currentOrder.formID, "status", "Filled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void removeOrder() {
    FoodOrder currentOrder = foodOrders.get(currentOrderNumber);
    foodOrderTable.remove("id", currentOrder.formID);
    foodOrders.remove(currentOrderNumber);
    if (currentOrderNumber != 0) {
      currentOrderNumber--;
      if (currentOrderNumber == 0) {
        prevOrderButton.setDisable(true);
      }
    } else {
      if (foodOrders.size() == 0) {
        nextOrderButton.setDisable(true);
      }
    }
    updateDisplay();
  }

  private void updateDisplay() {
    if (foodOrders.size() > 0) {
      deliveryRoomText.setText(foodOrders.get(currentOrderNumber).deliveryLocation);
      deliveryTimeText.setText(
          foodOrders
              .get(currentOrderNumber)
              .deliveryTime
              .format(DateTimeFormatter.ofPattern("h:ma, EE, MM/dd")));
      String itemsTextContent = foodOrders.get(currentOrderNumber).toString();
      itemsTextContent =
          itemsTextContent.substring(0, itemsTextContent.indexOf("Total cost: ") - 1);
      itemsText.setText(itemsTextContent);
      int numLines = itemsTextContent.split("\n").length + 1;
      itemsTextContainer.setPrefHeight(64 * numLines);
      assigneeText.setText(foodOrders.get(currentOrderNumber).assignee);
      switch (foodOrders.get(currentOrderNumber).formStatus) {
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
          "Viewing order #" + (currentOrderNumber + 1) + " out of " + foodOrders.size());
      if (statusText.getText().equals("Submitted")) {
        cancelButton.setDisable(false);
        filledButton.setDisable(false);
        System.out.println("This is fine");
      } else {
        cancelButton.setDisable(true);
        filledButton.setDisable(true);
        System.out.println("Why am i here, just to suffer?");
      }
      removeButton.setDisable(false);
    } else {
      itemsText.setText("");
      itemsTextContainer.setPrefHeight(0);
      deliveryRoomText.setText("");
      deliveryTimeText.setText("");
      assigneeText.setText("");
      viewingNumberText.setText("No orders to view");
      cancelButton.setDisable(true);
      filledButton.setDisable(true);
      removeButton.setDisable(true);
    }
  }
}
