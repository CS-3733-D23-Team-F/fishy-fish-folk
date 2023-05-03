package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.DAO.Observables.*;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.ui.FormStatus;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class ViewMasterOrderController extends AbsController {
  @FXML MFXButton refreshButton;
  @FXML TableView foodTable, supplyTable, furnitureTable, flowerTable, conferenceTable, itTable;
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
      floweritems,
      flowernotes;
  @FXML
  TableColumn conferenceid,
      conferenceroom,
      conferencestart,
      conferenceend,
      conferenceDateReserved,
      conferencebooker,
      conferenceattendees,
      conferencerecurring,
      conferencenotes;
  @FXML
  TableColumn itid, itassignee, itstatus, itissue, itcomponent, itpriority, itroom, itcontactinfo;
  @FXML
  MFXButton foodFillButton,
      foodCancelButton,
      foodRemoveButton,
      foodAssignButton,
      foodImportCSVButton,
      foodExportCSVButton;
  @FXML
  MFXButton supplyFillButton,
      supplyCancelButton,
      supplyRemoveButton,
      supplyAssignButton,
      supplyImportCSVButton,
      supplyExportCSVButton;
  @FXML
  MFXButton furnitureFillButton,
      furnitureCancelButton,
      furnitureRemoveButton,
      furnitureAssignButton,
      furnitureImportCSVButton,
      furnitureExportCSVButton;
  @FXML
  MFXButton flowerFillButton,
      flowerCancelButton,
      flowerRemoveButton,
      flowerAssignButton,
      flowerImportCSVButton,
      flowerExportCSVButton;
  @FXML
  MFXButton itFillButton,
      itCancelButton,
      itRemoveButton,
      itAssignButton,
      itImportCSVButton,
      itExportCSVButton;
  @FXML MFXButton conferenceRemoveButton, conferenceImportCSVButton, conferenceExportCSVButton;
  @FXML Label errorfood, errorsupply, errorfurniture, errorflower, errorit, errorconference;
  @FXML MFXFilterComboBox<String> foodAssignSelector;

  @FXML MFXFilterComboBox<String> supplyAssignSelector;

  @FXML MFXFilterComboBox<String> furnitureAssignSelector;

  @FXML MFXFilterComboBox<String> flowerAssignSelector;
  @FXML MFXFilterComboBox<String> itAssignSelector;
  @FXML MFXButton filterOrdersButton;

  @FXML TabPane tabPane;

  FileChooser fileChooser = new FileChooser();
  DirectoryChooser dirChooser = new DirectoryChooser();
  private static TranslateTransition thugShaker;

  public ViewMasterOrderController() {
    super();
  }

  public void initialize() {
    setToBlue();
    clearError();
    refreshButton.setOnMouseClicked(event -> refreshOrders());
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
    flowernotes.setCellValueFactory(
        new PropertyValueFactory<FlowerOrderObservable, String>("flowernotes"));

    conferenceid.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferenceid"));
    conferenceroom.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferenceroom"));
    conferencestart.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferencestart"));
    conferenceend.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferenceend"));
    conferenceDateReserved.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferenceDateReserved"));
    conferencebooker.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferencebooker"));
    conferenceattendees.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferenceattendees"));
    conferencerecurring.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferencerecurring"));
    conferencenotes.setCellValueFactory(
        new PropertyValueFactory<ConferenceRequestObservable, String>("conferencenotes"));

    itid.setCellValueFactory(new PropertyValueFactory<ITRequestObservable, String>("itid"));
    itassignee.setCellValueFactory(
        new PropertyValueFactory<ITRequestObservable, String>("itassignee"));
    itstatus.setCellValueFactory(new PropertyValueFactory<ITRequestObservable, String>("itstatus"));
    itissue.setCellValueFactory(new PropertyValueFactory<ITRequestObservable, String>("itissue"));
    itcomponent.setCellValueFactory(
        new PropertyValueFactory<ITRequestObservable, String>("itcomponent"));
    itpriority.setCellValueFactory(
        new PropertyValueFactory<ITRequestObservable, String>("itpriority"));
    itroom.setCellValueFactory(new PropertyValueFactory<ITRequestObservable, String>("itroom"));
    itcontactinfo.setCellValueFactory(
        new PropertyValueFactory<ITRequestObservable, String>("itcontactinfo"));

    foodTable.setItems(getFoodOrderRows());
    supplyTable.setItems(getSupplyOrderRows());
    furnitureTable.setItems(getFurnitureOrderRows());
    flowerTable.setItems(getFlowerOrderRows());
    conferenceTable.setItems(getConferenceRows());
    itTable.setItems(getITRequestRows());

    foodFillButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.filled));
    foodCancelButton.setOnMouseClicked(event -> foodSetStatus(FormStatus.cancelled));
    foodAssignButton.setOnMouseClicked(event -> foodAssign());
    foodRemoveButton.setOnMouseClicked(event -> foodRemove());
    foodImportCSVButton.setOnMouseClicked(event -> foodImportCSV());
    foodExportCSVButton.setOnMouseClicked(event -> foodExportCSV());

    supplyFillButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.filled));
    supplyCancelButton.setOnMouseClicked(event -> supplySetStatus(FormStatus.cancelled));
    supplyAssignButton.setOnMouseClicked(event -> supplyAssign());
    supplyRemoveButton.setOnMouseClicked(event -> supplyRemove());
    supplyImportCSVButton.setOnMouseClicked(event -> supplyImportCSV());
    supplyExportCSVButton.setOnMouseClicked(event -> supplyExportCSV());

    furnitureFillButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.filled));
    furnitureCancelButton.setOnMouseClicked(event -> furnitureSetStatus(FormStatus.cancelled));
    furnitureAssignButton.setOnMouseClicked(event -> furnitureAssign());
    furnitureRemoveButton.setOnMouseClicked(event -> furnitureRemove());
    furnitureImportCSVButton.setOnMouseClicked(event -> furnitureImportCSV());
    furnitureExportCSVButton.setOnMouseClicked(event -> furnitureExportCSV());

    flowerFillButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.filled));
    flowerCancelButton.setOnMouseClicked(event -> flowerSetStatus(FormStatus.cancelled));
    flowerAssignButton.setOnMouseClicked(event -> flowerAssign());
    flowerRemoveButton.setOnMouseClicked(event -> flowerRemove());
    flowerImportCSVButton.setOnMouseClicked(event -> flowerImportCSV());
    flowerExportCSVButton.setOnMouseClicked(event -> flowerExportCSV());

    conferenceRemoveButton.setOnMouseClicked(event -> conferenceRemove());
    conferenceImportCSVButton.setOnMouseClicked(event -> conferenceImportCSV());
    conferenceExportCSVButton.setOnMouseClicked(event -> conferenceExportCSV());

    itFillButton.setOnMouseClicked(event -> itSetStatus(FormStatus.filled));
    itCancelButton.setOnMouseClicked(event -> itSetStatus(FormStatus.cancelled));
    itAssignButton.setOnMouseClicked(event -> itAssign());
    itRemoveButton.setOnMouseClicked(event -> itRemove());
    itImportCSVButton.setOnMouseClicked(event -> itImportCSV());
    itExportCSVButton.setOnMouseClicked(event -> itExportCSV());

    filterOrdersButton.setOnMouseClicked(event -> filterOrders());

    ArrayList<UserAccount> users =
        (ArrayList<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);

    for (int user = 0; user < users.size(); user++) {
      UserAccount User = users.get(user);
      foodAssignSelector.getItems().add(User.getUsername());
      supplyAssignSelector.getItems().add(User.getUsername());
      furnitureAssignSelector.getItems().add(User.getUsername());
      flowerAssignSelector.getItems().add(User.getUsername());
      itAssignSelector.getItems().add(User.getUsername());

      if (User.getUsername().equals(SharedResources.getCurrentUser().getUsername())) {

        foodAssignSelector.getSelectionModel().selectIndex(user);
        flowerAssignSelector.getSelectionModel().selectIndex(user);
        furnitureAssignSelector.getSelectionModel().selectIndex(user);
        supplyAssignSelector.getSelectionModel().selectIndex(user);
        itAssignSelector.getSelectionModel().selectIndex(user);

        foodAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        flowerAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        furnitureAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        supplyAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
        itAssignSelector.setText(SharedResources.getCurrentUser().getUsername());
      }
    }
    foodAssignSelector.setOnAction(event -> setAssigns(foodAssignSelector.getValue()));
    flowerAssignSelector.setOnAction(event -> setAssigns(flowerAssignSelector.getValue()));
    supplyAssignSelector.setOnAction(event -> setAssigns(supplyAssignSelector.getValue()));
    furnitureAssignSelector.setOnAction(event -> setAssigns(furnitureAssignSelector.getValue()));
    itAssignSelector.setOnAction(event -> setAssigns(itAssignSelector.getValue()));
  }

  private void setAssigns(String assignee) {
    setToBlue();
    clearError();
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

  public ObservableList<ConferenceRequestObservable> getConferenceRows() {
    ArrayList<ConferenceRequest> conferencelist =
        (ArrayList<ConferenceRequest>)
            dbConnection.getAllEntries(TableEntryType.CONFERENCE_REQUEST);
    ObservableList<ConferenceRequestObservable> returnable = FXCollections.observableArrayList();
    for (ConferenceRequest request : conferencelist) {
      returnable.add(new ConferenceRequestObservable(request));
    }
    return returnable;
  }

  public ObservableList<ITRequestObservable> getITRequestRows() {
    ArrayList<ITRequest> itList =
        (ArrayList<ITRequest>) dbConnection.getAllEntries(TableEntryType.IT_REQUEST);
    ObservableList<ITRequestObservable> returnable = FXCollections.observableArrayList();
    for (ITRequest request : itList) {
      returnable.add(new ITRequestObservable(request));
    }
    return returnable;
  }

  private void foodSetStatus(FormStatus string) {
    setToBlue();
    clearError();
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
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
    setToBlue();
    clearError();
    SupplyOrderObservable food =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
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
    setToBlue();
    clearError();
    FurnitureOrderObservable food =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
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
    setToBlue();
    clearError();
    FlowerOrderObservable food =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
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

  private void itSetStatus(FormStatus string) {
    setToBlue();
    clearError();
    ITRequestObservable it = (ITRequestObservable) itTable.getSelectionModel().getSelectedItem();
    if (it == null) {
      return;
    }
    ITRequest dbRequest = (ITRequest) dbConnection.getEntry(it.id, TableEntryType.IT_REQUEST);
    switch (string) {
      case filled:
        {
          dbRequest.setFormStatus(FormStatus.filled);
          dbConnection.updateEntry(dbRequest);
          it.itstatus = "Filled";
          break;
        }
      case cancelled:
        {
          dbRequest.setFormStatus(FormStatus.cancelled);
          dbConnection.updateEntry(dbRequest);
          it.itstatus = "Cancelled";
          break;
        }
    }
    itTable.refresh();
  }

  private void foodAssign() {
    setToBlue();
    clearError();
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
    FoodRequest dbRequest =
        (FoodRequest) dbConnection.getEntry(food.id, TableEntryType.FOOD_REQUEST);
    String assignee = foodAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    food.foodassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    foodTable.refresh();
  }

  private void supplyAssign() {
    setToBlue();
    clearError();
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    if (supply == null) {
      return;
    }
    SupplyRequest dbRequest =
        (SupplyRequest) dbConnection.getEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    String assignee = supplyAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    supply.supplyassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    supplyTable.refresh();
  }

  private void furnitureAssign() {
    setToBlue();
    clearError();
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    if (furniture == null) {
      return;
    }
    FurnitureRequest dbRequest =
        (FurnitureRequest) dbConnection.getEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    String assignee = furnitureAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    furniture.furnitureassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    furnitureTable.refresh();
  }

  private void flowerAssign() {
    setToBlue();
    clearError();
    FlowerOrderObservable flower =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    if (flower == null) {
      return;
    }
    FlowerRequest dbRequest =
        (FlowerRequest) dbConnection.getEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    String assignee = flowerAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    flower.flowerassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    flowerTable.refresh();
  }

  private void itAssign() {
    setToBlue();
    clearError();
    ITRequestObservable it = (ITRequestObservable) itTable.getSelectionModel().getSelectedItem();
    if (it == null) {
      return;
    }
    ITRequest dbRequest = (ITRequest) dbConnection.getEntry(it.id, TableEntryType.IT_REQUEST);
    String assignee = itAssignSelector.getValue();
    dbRequest.setAssignee(assignee);
    it.itassignee = assignee;
    dbConnection.updateEntry(dbRequest);
    itTable.refresh();
  }

  private void foodRemove() {
    setToBlue();
    clearError();
    FoodOrderObservable food =
        (FoodOrderObservable) foodTable.getSelectionModel().getSelectedItem();
    if (food == null) {
      return;
    }
    dbConnection.removeEntry(food.id, TableEntryType.FOOD_REQUEST);
    foodTable.getItems().remove(food);
    foodTable.refresh();
  }

  private void supplyRemove() {
    setToBlue();
    clearError();
    SupplyOrderObservable supply =
        (SupplyOrderObservable) supplyTable.getSelectionModel().getSelectedItem();
    if (supply == null) {
      return;
    }
    dbConnection.removeEntry(supply.id, TableEntryType.SUPPLY_REQUEST);
    supplyTable.getItems().remove(supply);
    supplyTable.refresh();
  }

  private void furnitureRemove() {
    setToBlue();
    clearError();
    FurnitureOrderObservable furniture =
        (FurnitureOrderObservable) furnitureTable.getSelectionModel().getSelectedItem();
    if (furniture == null) {
      return;
    }
    dbConnection.removeEntry(furniture.id, TableEntryType.FURNITURE_REQUEST);
    furnitureTable.getItems().remove(furniture);
    furnitureTable.refresh();
  }

  private void flowerRemove() {
    setToBlue();
    clearError();
    FlowerOrderObservable flower =
        (FlowerOrderObservable) flowerTable.getSelectionModel().getSelectedItem();
    if (flower == null) {
      return;
    }
    dbConnection.removeEntry(flower.id, TableEntryType.FLOWER_REQUEST);
    flowerTable.getItems().remove(flower);
    flowerTable.refresh();
  }

  private void conferenceRemove() {
    setToBlue();
    clearError();
    ConferenceRequestObservable conf =
        (ConferenceRequestObservable) conferenceTable.getSelectionModel().getSelectedItem();
    if (conf == null) {
      return;
    }
    dbConnection.removeEntry(conf.id, TableEntryType.CONFERENCE_REQUEST);
    conferenceTable.getItems().remove(conf);
    conferenceTable.refresh();
  }

  private void itRemove() {
    setToBlue();
    clearError();
    ITRequestObservable it = (ITRequestObservable) itTable.getSelectionModel().getSelectedItem();
    if (it == null) {
      return;
    }
    dbConnection.removeEntry(it.id, TableEntryType.IT_REQUEST);
    itTable.getItems().remove(it);
    itTable.refresh();
  }

  private void foodImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the Food Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();
    fileChooser.setTitle("Select the Food Request Subtable CSV file");
    String subtablePath = fileChooserPrompt();

    if (!dbConnection.importCSV(mainTablePath, subtablePath, false, TableEntryType.FOOD_REQUEST)) {
      submissionError("Error importing CSV, please try again", foodImportCSVButton, errorfood);
    }

    refreshOrders();
  }

  private void supplyImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the Supply Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();
    fileChooser.setTitle("Select the Supply Request Subtable CSV file");
    String subtablePath = fileChooserPrompt();

    String message;
    if (!dbConnection.importCSV(
        mainTablePath, subtablePath, false, TableEntryType.SUPPLY_REQUEST)) {
      submissionError("Error importing CSV, please try again", supplyImportCSVButton, errorsupply);
    }

    refreshOrders();
  }

  private void flowerImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the Flower Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();
    fileChooser.setTitle("Select the Flower Request Subtable CSV file");
    String subtablePath = fileChooserPrompt();

    if (!dbConnection.importCSV(
        mainTablePath, subtablePath, false, TableEntryType.FLOWER_REQUEST)) {
      submissionError("Error importing CSV, please try again", flowerImportCSVButton, errorflower);
    }

    refreshOrders();
  }

  private void furnitureImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the Furniture Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();

    if (!dbConnection.importCSV(mainTablePath, false, TableEntryType.FURNITURE_REQUEST)) {
      submissionError(
          "Error importing CSV, please try again", furnitureImportCSVButton, errorfurniture);
    }

    refreshOrders();
  }

  private void conferenceImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the Conference Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();

    if (!dbConnection.importCSV(mainTablePath, false, TableEntryType.CONFERENCE_REQUEST)) {
      submissionError(
          "Error importing CSV, please try again", conferenceImportCSVButton, errorconference);
    }

    refreshOrders();
  }

  private void itImportCSV() {
    setToBlue();
    clearError();
    fileChooser.setTitle("Select the IT Request Main Table CSV file");
    String mainTablePath = fileChooserPrompt();

    if (!dbConnection.importCSV(mainTablePath, false, TableEntryType.IT_REQUEST)) {
      submissionError("Error importing CSV, please try again", itImportCSVButton, errorit);
    }
    refreshOrders();
  }

  /** Sets all borders back to blue */
  public void setToBlue() {
    itImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    conferenceImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    foodImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    flowerImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    supplyImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    furnitureImportCSVButton.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
  }

  /** Clears the error field */
  private void clearError() {
    errorit.setText("");
    errorit.setVisible(false);
    errorfurniture.setText("");
    errorfurniture.setVisible(false);
    errorsupply.setText("");
    errorsupply.setVisible(false);
    errorfood.setText("");
    errorfood.setVisible(false);
    errorflower.setText("");
    errorflower.setVisible(false);
    errorconference.setText("");
    errorconference.setVisible(false);
  }

  /**
   * Creates an error popup for the given values.
   *
   * @param error the error message you want to present.
   * @param node the area it will pop up next to.
   */
  private void submissionError(String error, MFXButton node, Label errors) {
    setToBlue();
    clearError();
    node.setStyle(
        "-fx-border-color: red; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-color: #012d5a; -fx-background-radius: 5");
    if (thugShaker == null || thugShaker.getNode() != node) {
      thugShaker = new TranslateTransition(Duration.millis(100), node);
    }
    thugShaker.setFromX(0f);
    thugShaker.setCycleCount(4);
    thugShaker.setAutoReverse(true);
    thugShaker.setByX(15f);
    thugShaker.playFromStart();
    errors.setText(error);
    errors.setVisible(true);
    errors.setStyle("-fx-text-fill:  red;");
    errors.setFont(Font.font("Open Sans", 15.0));
  }

  private void foodExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.FOOD_REQUEST);
  }

  private void supplyExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.SUPPLY_REQUEST);
  }

  private void flowerExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.FLOWER_REQUEST);
  }

  private void furnitureExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.FURNITURE_REQUEST);
  }

  private void conferenceExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.CONFERENCE_REQUEST);
  }

  private void itExportCSV() {
    setToBlue();
    clearError();
    dirChooser.setTitle("Select export directory");
    String exportPath = dirChooserPrompt();
    dbConnection.exportCSV(exportPath, TableEntryType.IT_REQUEST);
  }

  private void filterOrders() {
    setToBlue();
    clearError();
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
    for (int i = 0; i < conferenceTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(
              ((ConferenceRequestObservable) conferenceTable.getItems().get(i))
                  .getConferencebooker())) {
        conferenceTable.getItems().remove(i);
        i--;
      }
    }
    for (int i = 0; i < itTable.getItems().size(); i++) {
      if (!SharedResources.getCurrentUser()
          .getUsername()
          .equals(((ITRequestObservable) itTable.getItems().get(i)).getItassignee())) {
        itTable.getItems().remove(i);
        i--;
      }
    }

    filterOrdersButton.setStyle("-fx-background-color: #f0Bf4c;");
    filterOrdersButton.setTextFill(Paint.valueOf("#012d5a"));
    filterOrdersButton.setOnMouseClicked(event -> unfilterOrders());

    foodTable.refresh();
    flowerTable.refresh();
    furnitureTable.refresh();
    supplyTable.refresh();
    itTable.refresh();
    conferenceTable.refresh();
  }

  private void unfilterOrders() {
    refreshOrders();

    filterOrdersButton.setStyle("-fx-background-color: #012d5a;");
    filterOrdersButton.setTextFill(Paint.valueOf("WHITE"));
    filterOrdersButton.setOnMouseClicked(event -> filterOrders());
  }

  private void refreshOrders() {
    foodTable.setItems(getFoodOrderRows());
    supplyTable.setItems(getSupplyOrderRows());
    flowerTable.setItems(getFlowerOrderRows());
    furnitureTable.setItems(getFurnitureOrderRows());
    itTable.setItems(getITRequestRows());
    conferenceTable.setItems(getConferenceRows());
    foodTable.refresh();
    supplyTable.refresh();
    flowerTable.refresh();
    furnitureTable.refresh();
    itTable.refresh();
    conferenceTable.refresh();
  }
}
