package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.fxml.FXML;

public class ViewMasterOrderController {
  @FXML MFXTableView foodTable, supplyTable, furnitureTable, flowerTable;

  public void initialize() {
    foodTable.getItems()
  }
}
