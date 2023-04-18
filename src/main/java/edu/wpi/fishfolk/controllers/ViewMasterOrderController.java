package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.View;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

public class ViewMasterOrderController {
  @FXML TableView foodTable, supplyTable, furnitureTable, flowerTable;
  @FXML TableColumn foodid,foodassignee,foodtotalprice,foodstatus,fooddeliveryroom,fooddeliverytime,foodrecipientname,foodnotes,fooditems;
  @FXML TableColumn supplyid,supplyassignee,supplystatus,supplydeliveryroom,supplylink,supplynotes,supplysupplies;
  @FXML TableColumn furnitureid,furnitureassignee,furniturestatus,furnituredeliveryroom,furnituredeliverydate,furniturenotes,furnitureservicetype,furniturefurniture;
  @FXML TableColumn flowerid,flowerassignee,flowertotalprice,flowerstatus,flowerdeliveryroom,flowerdeliverytime,flowerrecipientname,floweritems;

  public void initialize() {
  }
}
