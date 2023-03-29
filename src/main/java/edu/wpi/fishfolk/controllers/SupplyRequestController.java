package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SupplyItem;
import edu.wpi.fishfolk.SupplyOrder;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import java.util.ArrayList;
import javafx.fxml.FXML;

public class SupplyRequestController {
  // SupplyOrder currentSupplyOrder;
  SupplyOrder currentSupplyOrder = new SupplyOrder();
  ArrayList<SupplyItem> supplyOptions;
  @FXML MFXButton cancelButton;
  @FXML MFXButton supplySubmitButton;
  @FXML MFXCheckbox check1, check2, check3, check4, check5, check6, check7;

  @FXML
  public void initialize() {
    loadOptions();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    supplySubmitButton.setOnMouseClicked(event -> submit());
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
    System.out.println(currentSupplyOrder.toString());
    Navigation.navigate(Screen.HOME);
  }
}
