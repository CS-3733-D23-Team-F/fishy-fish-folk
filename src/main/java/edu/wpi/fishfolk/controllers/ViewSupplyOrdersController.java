package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.ui.FormStatus.cancelled;
import static edu.wpi.fishfolk.ui.FormStatus.filled;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.ui.SupplyOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ViewSupplyOrdersController extends AbsController {
  @FXML Text itemsText;
  @FXML AnchorPane itemsTextContainer;
  @FXML Text deliveryRoomText, linkText, statusText;
  @FXML TextField assigneeText;
  @FXML MFXButton prevOrderButton, nextOrderButton;
  @FXML MFXButton cancelButton, filledButton, removeButton;
  @FXML Text viewingNumberText;

  int currentOrderNumber;
  List<SupplyOrder> supplyOrders;

  Table supplyOrderTable;

  public ViewSupplyOrdersController() {
    super();
    supplyOrderTable = new Table(dbConnection.conn, "supplyrequest");
    supplyOrderTable.addHeaders(
        SupplyRequestController.headers,
        new ArrayList<>(
            List.of("String", "String", "String", "String", "String", "String", "String")));
    supplyOrderTable.init(false);
  }

  @FXML
  private void initialize() throws InterruptedException {
    currentOrderNumber = 0;
    prevOrderButton.setOnAction(event -> prevOrder());
    nextOrderButton.setOnAction(event -> nextOrder());
    prevOrderButton.setDisable(true);
    loadOrders();
    if (supplyOrders.size() < 2) nextOrderButton.setDisable(true);
    assigneeText.setOnKeyReleased(event -> updateAssignee());
    cancelButton.setOnAction(event -> cancelOrder());
    filledButton.setOnAction(event -> fillOrder());
    removeButton.setOnAction(event -> removeOrder());
    updateDisplay();
  }

  private void updateAssignee() {
    SupplyOrder currentOrder = supplyOrders.get(currentOrderNumber);
    currentOrder.assignee = assigneeText.getText();
    // updateDisplay();
    supplyOrderTable.update(currentOrder.formID, "assignee", currentOrder.assignee);
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
    if (currentOrderNumber == supplyOrders.size() - 1) nextOrderButton.setDisable(true);
    updateDisplay();
  }

  private void loadOrders() throws InterruptedException {
    supplyOrders = new ArrayList<SupplyOrder>();

    ArrayList<String>[] tableOrders = supplyOrderTable.getAll();
    boolean headersHandled = false;
    for (ArrayList<String> tableEntry : tableOrders) {
      if (headersHandled) {
        SupplyOrder order = new SupplyOrder();
        order.construct(tableEntry);
        supplyOrders.add(order);
      } else {
        headersHandled = true;
      }
    }
  }

  private void cancelOrder() {
    SupplyOrder currentOrder = supplyOrders.get(currentOrderNumber);
    currentOrder.formStatus = cancelled;
    supplyOrderTable.update(currentOrder.formID, "status", "Cancelled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void fillOrder() {
    SupplyOrder currentOrder = supplyOrders.get(currentOrderNumber);
    currentOrder.formStatus = filled;
    supplyOrderTable.update(currentOrder.formID, "status", "Filled");
    cancelButton.setDisable(true);
    filledButton.setDisable(true);
    updateDisplay();
  }

  private void removeOrder() {
    SupplyOrder currentOrder = supplyOrders.get(currentOrderNumber);
    supplyOrderTable.remove("id", currentOrder.formID);
    supplyOrders.remove(currentOrderNumber);
    if (currentOrderNumber != 0) {
      currentOrderNumber--;
      if (currentOrderNumber == 0) {
        prevOrderButton.setDisable(true);
      }
    } else {
      if (supplyOrders.size() == 0) {
        nextOrderButton.setDisable(true);
      }
    }
    updateDisplay();
  }

  private void updateDisplay() {
    if (supplyOrders.size() > 0) {
      deliveryRoomText.setText(supplyOrders.get(currentOrderNumber).roomNum);
      linkText.setText(supplyOrders.get(currentOrderNumber).link);
      String itemsTextContent =
          supplyOrders.get(currentOrderNumber).listItemsToString().replace("-_-", "\n");
      itemsTextContent += "\n" + supplyOrders.get(currentOrderNumber).notes;
      itemsText.setText(itemsTextContent);
      int numLines = itemsTextContent.split("\n").length + 1;
      itemsTextContainer.setPrefHeight(64 * numLines);
      assigneeText.setText(supplyOrders.get(currentOrderNumber).assignee);
      switch (supplyOrders.get(currentOrderNumber).formStatus) {
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
          "Viewing order #" + (currentOrderNumber + 1) + " out of " + supplyOrders.size());
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
      linkText.setText("");
      assigneeText.setText("");
      viewingNumberText.setText("No orders to view");
      cancelButton.setDisable(true);
      filledButton.setDisable(true);
      removeButton.setDisable(true);
    }
  }
}
