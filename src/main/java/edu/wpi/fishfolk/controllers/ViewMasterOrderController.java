package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DAO.Observables.FlowerOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.FoodOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.FurnitureOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.SupplyOrderObservable;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.ui.FormStatus;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
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

public class ViewMasterOrderController extends AbsController {
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

  @FXML MFXFilterComboBox<String> foodAssignSelector;

  @FXML MFXFilterComboBox<String> supplyAssignSelector;

  @FXML MFXFilterComboBox<String> furnitureAssignSelector;

  @FXML MFXFilterComboBox<String> flowerAssignSelector;

  @FXML TabPane tabPane;

  public ViewMasterOrderController() {
    super();
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

    supplyid.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplyid"));
    supplyassignee.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplyassignee"));
    supplystatus.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplystatus"));
    supplydeliveryroom.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplydeliveryroom"));
    supplylink.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplylink"));
    supplynotes.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplynotes"));
    supplysupplies.setCellValueFactory(
        new PropertyValueFactory<SupplyOrderObservable, String>("supplysupplies"));

    furnitureid.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furnitureid"));
    furnitureassignee.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furnitureassignee"));
    furniturestatus.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furniturestatus"));
    furnituredeliveryroom.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furnituredeliveryroom"));
    furnituredeliverydate.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furnituredeliverydate"));
    furniturenotes.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furniturenotes"));
    furnitureservicetype.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furnitureservicetype"));
    furniturefurniture.setCellValueFactory(
        new PropertyValueFactory<FurnitureOrderObservable, String>("furniturefurniture"));

    flowerid.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerid"));
    flowerassignee.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerassignee"));
    flowertotalprice.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowertotalprice"));
    flowerstatus.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerstatus"));
    flowerdeliveryroom.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerdeliveryroom"));
    flowerdeliverytime.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerdeliverytime"));
    flowerrecipientname.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowerrecipientname"));
    floweritems.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("floweritems"));

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

    ArrayList<UserAccount> users =
        (ArrayList<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);

    for (int user = 0; user < users.size(); user++) {
      foodAssignSelector.getItems().add(users.get(user).getUsername());
      supplyAssignSelector.getItems().add(users.get(user).getUsername());
      furnitureAssignSelector.getItems().add(users.get(user).getUsername());
      flowerAssignSelector.getItems().add(users.get(user).getUsername());
    }
  }

  public ObservableList<FoodOrderObservable> getFoodOrderRows() {
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dbConnection.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();
    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    return returnable;
  }

  public ObservableList<SupplyOrderObservable> getSupplyOrderRows() {
    ArrayList<SupplyRequest> supplyList =
        (ArrayList<SupplyRequest>) dbConnection.getAllEntries(TableEntryType.SUPPLY_REQUEST);
    ObservableList<SupplyOrderObservable> returnable = FXCollections.observableArrayList();
    for (SupplyRequest request : supplyList) {
      returnable.add(new SupplyOrderObservable(request));
    }
    return returnable;
  }

  public ObservableList<FurnitureOrderObservable> getFurnitureOrderRows() {
    ArrayList<FurnitureRequest> furnitureList =
        (ArrayList<FurnitureRequest>) dbConnection.getAllEntries(TableEntryType.FURNITURE_REQUEST);
    ObservableList<FurnitureOrderObservable> returnable = FXCollections.observableArrayList();
    for (FurnitureRequest request : furnitureList) {
      returnable.add(new FurnitureOrderObservable(request));
    }
    return returnable;
  }

  public ObservableList<FlowerOrderObservable> getFlowerOrderRows() {
    ArrayList<FlowerRequest> flowerList =
        (ArrayList<FlowerRequest>) dbConnection.getAllEntries(TableEntryType.FLOWER_REQUEST);
    ObservableList<FlowerOrderObservable> returnable = FXCollections.observableArrayList();
    for (FlowerRequest request : flowerList) {
      returnable.add(new FlowerOrderObservable(request));
    }
    return returnable;
  }

  private void foodSetStatus(FormStatus string) {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    FoodRequest dbRequest =
        (FoodRequest) dbConnection.getEntry(food.id, TableEntryType.FOOD_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dbConnection.updateEntry(dbRequest);
          food.foodstatus = "Filled";
          break;
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dbConnection.updateEntry(dbRequest);
          food.foodstatus = "Cancelled";
          break;
        }
    }
    foodTable.refresh();
  }

  private void supplySetStatus(FormStatus string) {
    SupplyOrderObservable food =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    SupplyRequest dbRequest =
        (SupplyRequest) dbConnection.getEntry(food.id, TableEntryType.SUPPLY_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dbConnection.updateEntry(dbRequest);
          food.supplystatus = "Filled";
          break;
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dbConnection.updateEntry(dbRequest);
          food.supplystatus = "Cancelled";
          break;
        }
    }
    supplyTable.refresh();
  }

  private void furnitureSetStatus(FormStatus string) {
    FurnitureOrderObservable food =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    FurnitureRequest dbRequest =
        (FurnitureRequest) dbConnection.getEntry(food.id, TableEntryType.FURNITURE_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dbConnection.updateEntry(dbRequest);
          food.furniturestatus = "Filled";
          break;
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dbConnection.updateEntry(dbRequest);
          food.furniturestatus = "Cancelled";
          break;
        }
    }
    furnitureTable.refresh();
  }

  private void flowerSetStatus(FormStatus string) {
    FlowerOrderObservable food =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    FlowerRequest dbRequest =
        (FlowerRequest) dbConnection.getEntry(food.id, TableEntryType.FLOWER_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dbConnection.updateEntry(dbRequest);
          food.flowerstatus = "Filled";
          break;
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dbConnection.updateEntry(dbRequest);
          food.flowerstatus = "Cancelled";
          break;
        }
    }
    flowerTable.refresh();
  }

  private void foodAssign() {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    FoodRequest dbRequest =
        (FoodRequest) dbConnection.getEntry(food.id, TableEntryType.FOOD_REQUEST);
    String assignee = foodAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    food.foodassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    foodTable.refresh();
  }

  private void supplyAssign() {
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    SupplyRequest dbRequest =
        (SupplyRequest) dbConnection.getEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    String assignee = supplyAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    supply.supplyassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    supplyTable.refresh();
  }

  private void furnitureAssign() {
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    FurnitureRequest dbRequest =
        (FurnitureRequest) dbConnection.getEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    String assignee = furnitureAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    furniture.furnitureassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    furnitureTable.refresh();
  }

  private void flowerAssign() {
    FlowerOrderObservable flower =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    FlowerRequest dbRequest =
        (FlowerRequest) dbConnection.getEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    String assignee = flowerAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    flower.flowerassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    flowerTable.refresh();
    flowerassigneeTextField.setText("");
  }

  private void foodRemove() {
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    dbConnection.removeEntry(food.id, TableEntryType.FOOD_REQUEST);
    foodTable.getItems().remove(food);
    foodTable.refresh();
  }

  private void supplyRemove() {
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    dbConnection.removeEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    supplyTable.getItems().remove(supply);
    supplyTable.refresh();
  }

  private void furnitureRemove() {
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    dbConnection.removeEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    furnitureTable.getItems().remove(furniture);
    furnitureTable.refresh();
  }

  private void flowerRemove() {
    FurnitureOrderObservable flower =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    dbConnection.removeEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    flowerTable.getItems().remove(flower);
    flowerTable.refresh();
  }
}
