package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.DAO.Observables.FlowerOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.FoodOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.FurnitureOrderObservable;
import edu.wpi.fishfolk.database.DAO.Observables.SupplyOrderObservable;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.ui.FormStatus;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

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
  @FXML
  MFXButton foodFillButton,
      foodCancelButton,
      foodRemoveButton,
      foodAssignButton,
      foodFilterOrdersButton;
  @FXML
  MFXButton supplyFillButton,
      supplyCancelButton,
      supplyRemoveButton,
      supplyAssignButton,
      supplyFilterOrdersButton;
  @FXML
  MFXButton furnitureFillButton,
      furnitureCancelButton,
      furnitureRemoveButton,
      furnitureAssignButton,
      furnitureFilterOrdersButton;
  @FXML
  MFXButton flowerFillButton,
      flowerCancelButton,
      flowerRemoveButton,
      flowerAssignButton,
      flowerFilterOrdersButton;

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

    foodFillButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.filled));
    foodCancelButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.cancelled));
    foodAssignButton.setOnMouseClicked(event -> foodAssign());
    foodRemoveButton.setOnMouseClicked(event -> foodRemove());
    foodFilterOrdersButton.setOnMouseClicked(event -> filterOrders());

    supplyFillButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.filled));
    supplyCancelButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.cancelled));
    supplyAssignButton.setOnMouseClicked(event -> supplyAssign());
    supplyRemoveButton.setOnMouseClicked(event -> supplyRemove());
    supplyFilterOrdersButton.setOnMouseClicked(event -> filterOrders());

    furnitureFillButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.filled));
    furnitureCancelButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.cancelled));
    furnitureAssignButton.setOnMouseClicked(event -> furnitureAssign());
    furnitureRemoveButton.setOnMouseClicked(event -> furnitureRemove());
    furnitureFilterOrdersButton.setOnMouseClicked(event -> filterOrders());

    flowerFillButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.filled));
    flowerCancelButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.cancelled));
    flowerAssignButton.setOnMouseClicked(event -> flowerAssign());
    flowerRemoveButton.setOnMouseClicked(event -> flowerRemove());
    flowerFilterOrdersButton.setOnMouseClicked(event -> filterOrders());

    ArrayList<UserAccount> users =
        (ArrayList<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);

    for (int user = 0; user < users.size(); user++) {
      UserAccount User = users.get(user);
      foodAssignSelector.getItems().add(User.getUsername());
      supplyAssignSelector.getItems().add(User.getUsername());
      furnitureAssignSelector.getItems().add(User.getUsername());
      flowerAssignSelector.getItems().add(User.getUsername());
      if (User.getUsername().equals(SharedResources.getCurrentUser().getUsername())) {
        foodAssignSelector.getSelectionModel().selectIndex(user);
        flowerAssignSelector.getSelectionModel().selectIndex(user);
        furnitureAssignSelector.getSelectionModel().selectIndex(user);
        supplyAssignSelector.getSelectionModel().selectIndex(user);
        foodAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        flowerAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        furnitureAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        supplyAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
      }
    }
    foodAssignSelector.setOnAction(event -> setAssigns(foodAssignSelector.getValue()));
    flowerAssignSelector.setOnAction(event -> setAssigns(flowerAssignSelector.getValue()));
    supplyAssignSelector.setOnAction(event -> setAssigns(supplyAssignSelector.getValue()));
    furnitureAssignSelector.setOnAction(event -> setAssigns(furnitureAssignSelector.getValue()));
  }

  private void setAssigns(String assignee) {
    foodAssignSelector
        .getSelectionModel()
        .selectIndex(foodAssignSelector.getItems().indexOf(assignee));
    foodAssignSelector.setText(assignee);
    supplyAssignSelector
        .getSelectionModel()
        .selectIndex(supplyAssignSelector.getItems().indexOf(assignee));
    supplyAssignSelector.setText(assignee);
    furnitureAssignSelector
        .getSelectionModel()
        .selectIndex(furnitureAssignSelector.getItems().indexOf(assignee));
    furnitureAssignSelector.setText(assignee);
    flowerAssignSelector
        .getSelectionModel()
        .selectIndex(flowerAssignSelector.getItems().indexOf(assignee));
    flowerAssignSelector.setText(assignee);
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

  private void filterOrders() {
    for (int i = 0; i < foodTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(((FoodOrderObservable) foodTable.getItems().get(i)).getFoodassignee())) {
        foodTable.getItems().remove(i);
        i--;
      }
    }
    for (int i = 0; i < supplyTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(((SupplyOrderObservable) supplyTable.getItems().get(i)).getSupplyassignee())) {
        supplyTable.getItems().remove(i);
        i--;
      }
    }
    for (int i = 0; i < furnitureTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(
              ((FurnitureOrderObservable) furnitureTable.getItems().get(i))
                  .getFurnitureassignee())) {
        furnitureTable.getItems().remove(i);
        i--;
      }
    }
    for (int i = 0; i < flowerTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(((FlowerOrderObservable) flowerTable.getItems().get(i)).getFlowerassignee())) {
        flowerTable.getItems().remove(i);
        i--;
      }
    }

    foodFilterOrdersButton.setStyle("-fx-background-color: #f0Bf4c;");
    foodFilterOrdersButton.setTextFill(Paint.valueOf("#012d5a"));
    foodFilterOrdersButton.setOnMouseClicked(event -> unfilterOrders());
    supplyFilterOrdersButton.setStyle("-fx-background-color: #f0Bf4c;");
    supplyFilterOrdersButton.setTextFill(Paint.valueOf("#012d5a"));
    supplyFilterOrdersButton.setOnMouseClicked(event -> unfilterOrders());
    furnitureFilterOrdersButton.setStyle("-fx-background-color: #f0Bf4c;");
    furnitureFilterOrdersButton.setTextFill(Paint.valueOf("#012d5a"));
    furnitureFilterOrdersButton.setOnMouseClicked(event -> unfilterOrders());
    flowerFilterOrdersButton.setStyle("-fx-background-color: #f0Bf4c;");
    flowerFilterOrdersButton.setTextFill(Paint.valueOf("#012d5a"));
    flowerFilterOrdersButton.setOnMouseClicked(event -> unfilterOrders());

    foodTable.refresh();
    flowerTable.refresh();
    furnitureTable.refresh();
    supplyTable.refresh();
  }

  private void unfilterOrders() {
    foodTable.setItems(getFoodOrderRows());
    supplyTable.setItems(getSupplyOrderRows());
    furnitureTable.setItems(getFurnitureOrderRows());
    flowerTable.setItems(getFlowerOrderRows());

    foodFilterOrdersButton.setStyle("-fx-background-color: #012d5a;");
    foodFilterOrdersButton.setTextFill(Paint.valueOf("WHITE"));
    foodFilterOrdersButton.setOnMouseClicked(event -> filterOrders());
    supplyFilterOrdersButton.setStyle("-fx-background-color: #012d5a;");
    supplyFilterOrdersButton.setTextFill(Paint.valueOf("WHITE"));
    supplyFilterOrdersButton.setOnMouseClicked(event -> filterOrders());
    furnitureFilterOrdersButton.setStyle("-fx-background-color: #012d5a;");
    furnitureFilterOrdersButton.setTextFill(Paint.valueOf("WHITE"));
    furnitureFilterOrdersButton.setOnMouseClicked(event -> filterOrders());
    flowerFilterOrdersButton.setStyle("-fx-background-color: #012d5a;");
    flowerFilterOrdersButton.setTextFill(Paint.valueOf("WHITE"));
    flowerFilterOrdersButton.setOnMouseClicked(event -> filterOrders());

    foodTable.refresh();
    flowerTable.refresh();
    furnitureTable.refresh();
    supplyTable.refresh();
  }
}
