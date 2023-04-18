package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.SupplyItem;
import edu.wpi.fishfolk.ui.SupplyOrder;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

public class NewFurnitureOrderController extends AbsController {

  private static String[] headersArray = {
    "id", "items", "link", "roomNum", "notes", "status", "assignee"
  };
  public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));

  Table supplyRequestTable;
  SupplyOrder currentSupplyOrder = new SupplyOrder();
  @FXML MFXFilterComboBox<String> roomSelector;
  ArrayList<SupplyItem> supplyOptions;
  @FXML Rectangle cancelButton;
  @FXML Rectangle supplySubmitButton;
  @FXML Rectangle clearButton;
  // @FXML MFXCheckbox check1, check2, check3, check4, check5, check6, check7;
  @FXML
  MFXRectangleToggleNode rectangle1,
      rectangle2,
      rectangle3,
      rectangle4,
      rectangle5,
      rectangle6,
      rectangle7;
  @FXML MFXTextField linkTextField, notesTextField;

  /*
  public NewSupplyOrderController() {
    super();
    supplyRequestTable = new Table(dbConnection.conn, "supplyrequest");
    supplyRequestTable.init(false);
    supplyRequestTable.addHeaders(
        SupplyRequestController.headers,
        new ArrayList<>(
            List.of("String", "String", "String", "String", "String", "String", "String")));
  }
   */

  @FXML
  public void initialize() {
    loadOptions();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    supplySubmitButton.setOnMouseClicked(event -> submit());
    clearButton.setOnMouseClicked(event -> clearAllFields());
    loadRooms();
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

  void loadRooms() {
    // roomSelector.getItems().addAll(dbConnection.getDestLongnames());
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
    notesTextField.clear();
    roomSelector.setText(null);
  }

  private void clearChecks() {
    rectangle1.setSelected(false);
    rectangle2.setSelected(false);
    rectangle3.setSelected(false);
    rectangle4.setSelected(false);
    rectangle4.setSelected(false);
    rectangle5.setSelected(false);
    rectangle6.setSelected(false);
  }

  private boolean submittable() {
    if (rectangle1.isSelected()
        || rectangle2.isSelected()
        || rectangle3.isSelected()
        || rectangle4.isSelected()
        || rectangle5.isSelected()
        || rectangle6.isSelected()
        || rectangle7.isSelected()
        || (!(currentSupplyOrder.link == ""))) {
      System.out.println("Sufficient fields filled");
      return true;
    } else {
      System.out.println("Sufficient fields not filled");
      return false;
    }
  }

  private void submit() {
    if (rectangle1.isSelected()) addToOrder(0);
    else addToOrder(7);
    if (rectangle2.isSelected()) addToOrder(1);
    else addToOrder(7);
    if (rectangle3.isSelected()) addToOrder(2);
    else addToOrder(7);
    if (rectangle4.isSelected()) addToOrder(3);
    else addToOrder(7);
    if (rectangle5.isSelected()) addToOrder(4);
    else addToOrder(7);
    if (rectangle6.isSelected()) addToOrder(5);
    else addToOrder(7);
    if (rectangle7.isSelected()) addToOrder(6);
    else addToOrder(7);
    currentSupplyOrder.roomNum = roomSelector.getValue();
    currentSupplyOrder.link = linkTextField.getText();
    currentSupplyOrder.notes = notesTextField.getText();
    if (submittable()) {
      System.out.println(currentSupplyOrder.toString());
      System.out.println(currentSupplyOrder.listItemsToString());
      currentSupplyOrder.setSubmitted();
      // supplyRequestTable.insert(currentSupplyOrder);
      Navigation.navigate(Screen.HOME);
    }
  }
}
