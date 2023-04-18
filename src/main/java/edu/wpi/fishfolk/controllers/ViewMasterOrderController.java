package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FlowerOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FoodOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FurnitureOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.SupplyOrderObservable;
import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FoodRequest;
import edu.wpi.fishfolk.database.rewrite.TableEntry.TableEntryType;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewMasterOrderController {
  @FXML TableView foodTable, supplyTable, furnitureTable, flowerTable;
  @FXML
  TableColumn foodid,
      foodassignee,
      foodtotalprice,
      foodstatus,
      fooddeliveryroom,
      fooddeliverytime,
      foodrecipientname,
      foodnotes,
      fooditems;
  @FXML
  TableColumn supplyid,
      supplyassignee,
      supplystatus,
      supplydeliveryroom,
      supplylink,
      supplynotes,
      supplysupplies;
  @FXML
  TableColumn furnitureid,
      furnitureassignee,
      furniturestatus,
      furnituredeliveryroom,
      furnituredeliverydate,
      furniturenotes,
      furnitureservicetype,
      furniturefurniture;
  @FXML
  TableColumn flowerid,
      flowerassignee,
      flowertotalprice,
      flowerstatus,
      flowerdeliveryroom,
      flowerdeliverytime,
      flowerrecipientname,
      floweritems;

  Fdb dibdab;

  public ViewMasterOrderController() {
    super();
    dibdab = new Fdb();
  }

  public void initialize() {
    foodid.setCellValueFactory(new PropertyValueFactory<FoodOrderObservable, String>("foodid"));
    foodassignee.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("foodassignee"));
    foodtotalprice.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("foodtotalprice"));
    foodstatus.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("foodstatus"));
    fooddeliveryroom.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("fooddeliveryroom"));
    fooddeliverytime.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("fooddeliverytime"));
    foodrecipientname.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("foodrecipientname"));
    foodnotes.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("foodnotes"));
    fooditems.setCellValueFactory(
        new PropertyValueFactory<FoodOrderObservable, String>("fooditems"));

    supplyid.setCellValueFactory(new PropertyValueFactory<SupplyOrderObservable, String>("ID"));
    supplyassignee.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("Assignee"));
    supplystatus.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("Form Status"));
    supplydeliveryroom.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("Delivery Room"));
    supplylink.setCellValueFactory(new PropertyValueFactory<SupplyOrderObservable, String>("Link"));
    supplynotes.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("Notes"));
    supplysupplies.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("Supplies"));

    furnitureid.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("ID"));
    furnitureassignee.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Assignee"));
    furniturestatus.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Form Status"));
    furnituredeliveryroom.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Delivery Room"));
    furnituredeliverydate.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Delivery Date"));
    furniturenotes.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Notes"));
    furnitureservicetype.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Service Type"));
    furniturefurniture.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("Furniture"));

    flowerid.setCellValueFactory(new PropertyValueFactory<FlowerOrderObservable, String>("ID"));
    flowerassignee.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Assignee"));
    flowertotalprice.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Total Price"));
    flowerstatus.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Form Status"));
    flowerdeliveryroom.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Delivery Room"));
    flowerdeliverytime.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Delivery Time"));
    flowerrecipientname.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Recipient Name"));
    floweritems.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("Items"));

    foodTable.setItems(getFoodOrderRows());
  }

  public ObservableList<FoodOrderObservable> getFoodOrderRows() {
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dibdab.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();
    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    return returnable;
  }
}
