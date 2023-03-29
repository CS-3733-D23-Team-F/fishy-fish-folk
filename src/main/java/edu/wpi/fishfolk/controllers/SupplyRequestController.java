package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SupplyItem;
import edu.wpi.fishfolk.SupplyOrder;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import javafx.fxml.FXML;

public class SupplyRequestController {
  // SupplyOrder currentSupplyOrder;
  SupplyOrder currentSupplyOrder = new SupplyOrder();
  ArrayList<SupplyItem> supplyOptions;
  @FXML MFXButton cancelButton;
  @FXML MFXButton supplySubmitButton;
  @FXML MFXButton clearButton;
  @FXML MFXCheckbox check1, check2, check3, check4, check5, check6, check7;
  @FXML MFXTextField linkTextField, roomNumTextField, notesTextField;

  @FXML
  public void initialize() {
    loadOptions();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    supplySubmitButton.setOnMouseClicked(event -> submit());
    clearButton.setOnMouseClicked(event -> clearAllFields());
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
        || (!(currentSupplyOrder.link == null))) return true;
    else return false;
  }

  private void submit() {
    if (submittable()) {
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
      System.out.println(currentSupplyOrder.toString());
      Navigation.navigate(Screen.HOME);
    }
  }
}
