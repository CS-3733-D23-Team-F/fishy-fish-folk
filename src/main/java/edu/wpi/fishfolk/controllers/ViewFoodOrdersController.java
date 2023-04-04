package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.ui.FoodOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ViewFoodOrdersController extends AbsController {
  @FXML Text itemsText;
  @FXML AnchorPane itemsTextContainer;
  @FXML Text deliveryRoomText, deliveryTimeText, statusText;
  @FXML MFXTextField assigneeText;
  @FXML MFXButton prevOrderButton, nextOrderButton;
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
    updateDisplay();
  }

  private void updateAssignee() {
    FoodOrder currentOrder = foodOrders.get(currentOrderNumber);
    currentOrder.assignee = assigneeText.getText();
    // updateDisplay();
    foodOrderTable.update(currentOrder.formID, "Assignee", currentOrder.assignee);
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

    // temp generic foodOrders
    /*FoodOrder generic1 = new FoodOrder();
    generic1.assignee = "Blink 182";
    generic1.addItem(FoodItem.generic3);
    generic1.addItem(FoodItem.generic3);
    generic1.deliveryLocation = "Air Force One";
    generic1.formStatus = FormStatus.filled;
    Thread.sleep(100);
    FoodOrder generic2 = new FoodOrder();
    generic2.assignee = "Tristin Youtz";
    generic2.addItem(FoodItem.generic4);
    generic2.addItem(FoodItem.generic6);
    generic1.deliveryLocation = "Home";
    generic2.formStatus = FormStatus.submitted;
    foodOrders.add(generic1);
    foodOrders.add(generic2);*/
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
          }
        case cancelled:
          {
            statusText.setText("Cancelled");
          }
        case submitted:
          {
            statusText.setText("Submitted");
          }
      }
      viewingNumberText.setText(
          "Viewing order #" + (currentOrderNumber + 1) + " out of " + foodOrders.size());
    } else {
      itemsText.setText("");
      itemsTextContainer.setPrefHeight(0);
      deliveryRoomText.setText("");
      deliveryTimeText.setText("");
      assigneeText.setText("");
      viewingNumberText.setText("No orders to view");
    }
  }
}
