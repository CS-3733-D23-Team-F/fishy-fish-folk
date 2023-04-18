package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FlowerOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FoodOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.FurnitureOrderObservable;
import edu.wpi.fishfolk.database.rewrite.DAO.Observables.SupplyOrderObservable;
import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.*;
import edu.wpi.fishfolk.ui.FormStatus;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;

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
  @FXML Rectangle foodfillButton, foodcancelButton, foodremoveButton, foodassignButton;
  @FXML Rectangle supplyfillButton, supplycancelButton, supplyremoveButton, supplyassignButton;
  @FXML
  Rectangle furniturefillButton,
      furniturecancelButton,
      furnitureremoveButton,
      furnitureassignButton;
  @FXML Rectangle flowerfillButton, flowercancelButton, flowerremoveButton, flowerassignButton;

  @FXML MFXTextField foodassigneeTextField;
  @FXML MFXTextField supplyassigneeTextField;
  @FXML MFXTextField furnitureassigneeTextField;
  @FXML MFXTextField flowerassigneeTextField;

  @FXML TabPane tabPane;

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
    supplyTable.setItems(getSupplyOrderRows());
    furnitureTable.setItems(getFurnitureOrderRows());
    flowerTable.setItems(getFlowerOrderRows());

    foodfillButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.filled));
    foodcancelButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.cancelled));
    foodassignButton.setOnMouseClicked(event -> foodAssign());
    foodremoveButton.setOnMouseClicked(event -> foodRemove());

    supplyfillButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.filled));
    supplycancelButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.cancelled));
    supplyassignButton.setOnMouseClicked(event -> supplyAssign());
    supplyremoveButton.setOnMouseClicked(event -> supplyRemove());

    furniturefillButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.filled));
    furniturecancelButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.cancelled));
    furnitureassignButton.setOnMouseClicked(event -> furnitureAssign());
    furnitureremoveButton.setOnMouseClicked(event -> furnitureRemove());

    flowerfillButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.filled));
    flowercancelButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.cancelled));
    flowerassignButton.setOnMouseClicked(event -> flowerAssign());
    flowerremoveButton.setOnMouseClicked(event -> flowerRemove());
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

  public ObservableList<SupplyOrderObservable> getSupplyOrderRows() {
    ArrayList<SupplyRequest> supplyList =
        (ArrayList<SupplyRequest>) dibdab.getAllEntries(TableEntryType.SUPPLY_REQUEST);
    ObservableList<SupplyOrderObservable> returnable = FXCollections.observableArrayList();
    for (SupplyRequest request : supplyList) {
      returnable.add(new SupplyOrderObservable(request));
    }
    return returnable;
  }

  public ObservableList<FurnitureOrderObservable> getFurnitureOrderRows() {
    ArrayList<FurnitureRequest> furnitureList =
        (ArrayList<FurnitureRequest>) dibdab.getAllEntries(TableEntryType.FURNITURE_REQUEST);
    ObservableList<FurnitureOrderObservable> returnable = FXCollections.observableArrayList();
    for (FurnitureRequest request : furnitureList) {
      returnable.add(new FurnitureOrderObservable(request));
    }
    return returnable;
  }

  public ObservableList<FlowerOrderObservable> getFlowerOrderRows() {
    ArrayList<FlowerRequest> flowerList =
        (ArrayList<FlowerRequest>) dibdab.getAllEntries(TableEntryType.FLOWER_REQUEST);
    ObservableList<FlowerOrderObservable> returnable = FXCollections.observableArrayList();
    for (FlowerRequest request : flowerList) {
      returnable.add(new FlowerOrderObservable(request));
    }
    return returnable;
  }

  private void foodSetStatus(FormStatus string) {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    FoodRequest dbRequest = (FoodRequest) dibdab.getEntry(food.id, TableEntryType.FOOD_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dibdab.updateEntry(dbRequest);
          food.foodstatus = "Filled";
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dibdab.updateEntry(dbRequest);
          food.foodstatus = "Cancelled";
        }
    }
    foodTable.refresh();
  }

  private void supplySetStatus(FormStatus string) {
    SupplyOrderObservable food =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    SupplyRequest dbRequest =
        (SupplyRequest) dibdab.getEntry(food.id, TableEntryType.SUPPLY_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dibdab.updateEntry(dbRequest);
          food.supplystatus = "Filled";
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dibdab.updateEntry(dbRequest);
          food.supplystatus = "Cancelled";
        }
    }
    foodTable.refresh();
  }

  private void furnitureSetStatus(FormStatus string) {
    FurnitureOrderObservable food =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    FurnitureRequest dbRequest =
        (FurnitureRequest) dibdab.getEntry(food.id, TableEntryType.FURNITURE_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dibdab.updateEntry(dbRequest);
          food.furniturestatus = "Filled";
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dibdab.updateEntry(dbRequest);
          food.furniturestatus = "Cancelled";
        }
    }
    foodTable.refresh();
  }

  private void flowerSetStatus(FormStatus string) {
    FlowerOrderObservable food =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    FlowerRequest dbRequest =
        (FlowerRequest) dibdab.getEntry(food.id, TableEntryType.FLOWER_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dibdab.updateEntry(dbRequest);
          food.flowerstatus = "Filled";
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dibdab.updateEntry(dbRequest);
          food.flowerstatus = "Cancelled";
        }
    }
    foodTable.refresh();
  }

  private void foodAssign() {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    FoodRequest dbRequest = (FoodRequest) dibdab.getEntry(food.id, TableEntryType.FOOD_REQUEST);
    String assignee = foodassigneeTextField.getText();
    dbRequest.setAssignee(assignee);
    food.foodassignee = assignee;
    dibdab.updateEntry(dbRequest);
    foodTable.refresh();
    foodassigneeTextField.setText("");
  }

  private void supplyAssign() {
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    SupplyRequest dbRequest =
        (SupplyRequest) dibdab.getEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    String assignee = supplyassigneeTextField.getText();
    dbRequest.setAssignee(assignee);
    supply.supplyassignee = assignee;
    dibdab.updateEntry(dbRequest);
    supplyTable.refresh();
    supplyassigneeTextField.setText("");
  }

  private void furnitureAssign() {
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    FurnitureRequest dbRequest =
        (FurnitureRequest) dibdab.getEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    String assignee = furnitureassigneeTextField.getText();
    dbRequest.setAssignee(assignee);
    furniture.furnitureassignee = assignee;
    dibdab.updateEntry(dbRequest);
    furnitureTable.refresh();
    furnitureassigneeTextField.setText("");
  }

  private void flowerAssign() {
    FlowerOrderObservable flower =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    FlowerRequest dbRequest =
        (FlowerRequest) dibdab.getEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    String assignee = flowerassigneeTextField.getText();
    dbRequest.setAssignee(assignee);
    flower.flowerassignee = assignee;
    dibdab.updateEntry(dbRequest);
    flowerTable.refresh();
    flowerassigneeTextField.setText("");
  }

  private void foodRemove() {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    dibdab.removeEntry(food.id, TableEntryType.FOOD_REQUEST);
    foodTable.getItems().remove(food);
    foodTable.refresh();
  }

  private void supplyRemove() {
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    dibdab.removeEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    supplyTable.getItems().remove(supply);
    supplyTable.refresh();
  }

  private void furnitureRemove() {
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    dibdab.removeEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    furnitureTable.getItems().remove(furniture);
    furnitureTable.refresh();
  }

  private void flowerRemove() {
    FurnitureOrderObservable flower =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    dibdab.removeEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    flowerTable.getItems().remove(flower);
    flowerTable.refresh();
  }
}
