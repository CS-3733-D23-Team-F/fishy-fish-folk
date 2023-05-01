package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.DAO.Observables.*;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.database.TableEntry.Alert;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminDashboardController {

  // @FXML MFXButton navigateButton;
  @FXML GridPane grid;
  @FXML GridPane alertGrid;
  @FXML Label tableHeader;
  @FXML MFXButton outstandingFilter, unassignedTasks;
  // @FXML MFXPaginatedTableView paginated;
  @FXML TableView<FoodOrderObservable> foodTable;
  @FXML TableView<FurnitureOrderObservable> furnitureTable;
  @FXML TableView<FlowerOrderObservable> flowerTable;
  @FXML TableView<SupplyOrderObservable> supplyTable;
  @FXML MFXTextField addAlert;
  @FXML MFXButton toMapEditor, toSignageEditor, toMoveEditor;
  @FXML
  TableColumn<FoodOrderObservable, String> foodid,
      foodassignee,
      foodtotalprice,
      foodstatus,
      fooddeliveryroom,
      fooddeliverytime,
      foodrecipientname,
      foodnotes,
      fooditems;
  @FXML
  TableColumn<SupplyOrderObservable, String> supplyid,
      supplyassignee,
      supplystatus,
      supplydeliveryroom,
      supplylink,
      supplynotes,
      supplysupplies;
  @FXML
  TableColumn<FurnitureOrderObservable, String> furnitureid,
      furnitureassignee,
      furniturestatus,
      furnituredeliveryroom,
      furnituredeliverydate,
      furniturenotes,
      furnitureservicetype,
      furniturefurniture;
  @FXML
  TableColumn<FlowerOrderObservable, String> flowerid,
      flowerassignee,
      flowertotalprice,
      flowerstatus,
      flowerdeliveryroom,
      flowerdeliverytime,
      flowerrecipientname,
      floweritems;

  private int rowA = 0;

  @FXML
  public void initialize() {
    ArrayList<Move> moves = (ArrayList<Move>) dbConnection.getAllEntries(TableEntryType.MOVE);
    setTable();

    outstandingFilter.setOnMouseClicked(
        event -> {
          setOutstandingTable();
          unassignedTasks.setVisible(true);
          unassignedTasks.setDisable(false);
          outstandingFilter.setDisable(true);
          outstandingFilter.setVisible(false);
          tableHeader.setText("Outstanding Services");
        });

    unassignedTasks.setOnMouseClicked(
        event -> {
          setTable();
          unassignedTasks.setVisible(false);
          unassignedTasks.setDisable(true);
          outstandingFilter.setDisable(false);
          outstandingFilter.setVisible(true);
          tableHeader.setText("Unassigned Tasks");
        });

    int col = 0;
    int row = 1;
    try {
      for (Move move : moves) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/FutureMoves.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        FutureMovesController futureMoves = fxmlLoader.getController();

        futureMoves.setData(move.getLongName(), "" + move.getDate());

        futureMoves.notify.setOnMouseClicked(
            event -> {
              String longname = futureMoves.longname;
              LocalDate date = LocalDate.parse(futureMoves.sDate);
              // truncate example:
              // https://stackoverflow.com/questions/31726418/localdatetime-remove-the-milliseconds
              Alert alert =
                  new Alert(
                      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), longname, date, "");

              addAlert(alert);
            });

        if (col == 1) {
          col = 0;
          row++;
        }

        // col++;
        grid.add(anchorPane, col++, row);

        grid.setMinWidth(Region.USE_COMPUTED_SIZE);
        grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        grid.setMaxWidth(Region.USE_COMPUTED_SIZE);

        grid.setMinHeight(Region.USE_COMPUTED_SIZE);
        grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        grid.setMaxHeight(Region.USE_COMPUTED_SIZE);

        GridPane.setMargin(anchorPane, new Insets(10));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    dbConnection.getAllEntries(TableEntryType.ALERT).forEach(obj -> addAlert((Alert) obj));

    addAlert.setOnAction(
        event -> {
          Alert alert =
              new Alert(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), addAlert.getText());
          addAlert(alert);
        });

    supplyassignee.setOnEditCommit(this::onSetSupplyEdit);
    foodassignee.setOnEditCommit(this::onSetFoodEdit);
    flowerassignee.setOnEditCommit(this::onSetFlowerEdit);
    furnitureassignee.setOnEditCommit(this::onSetFurnitureEdit);

    toMapEditor.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    toMoveEditor.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_EDITOR));
    toSignageEditor.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE_EDITOR));
  }

  public void addAlert(Alert alert) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Fapp.class.getResource("views/Alerts.fxml"));
      HBox alertPane = fxmlLoader.load();

      AlertsController alertsController = fxmlLoader.getController();
      alertsController.setData(alert);

      alertsController.closeAlert.setOnMouseClicked(
          event -> {
            alertGrid.getChildren().remove(alertPane);
            dbConnection.removeEntry(alert.getTimestamp(), TableEntryType.ALERT);
          });

      dbConnection.insertEntry(alert);

      GridPane.setHgrow(alertPane, Priority.ALWAYS);
      GridPane.setMargin(alertPane, new Insets(10));
      alertPane.setAlignment(Pos.TOP_CENTER);

      alertGrid.add(alertPane, 0, rowA);

      GridPane.setValignment(alertPane, VPos.TOP);
      rowA += 1;
      addAlert.clear();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onSetSupplyEdit(TableColumn.CellEditEvent<SupplyOrderObservable, String> t) {

    SupplyOrderObservable row = t.getRowValue();
    SupplyRequest entry =
        (SupplyRequest) dbConnection.getEntry(row.id, TableEntryType.SUPPLY_REQUEST);
    String value = t.getNewValue();
    entry.setAssignee(value);
    dbConnection.updateEntry(entry);
  }

  public void onSetFoodEdit(TableColumn.CellEditEvent<FoodOrderObservable, String> t) {

    FoodOrderObservable row = t.getRowValue();
    FoodRequest entry = (FoodRequest) dbConnection.getEntry(row.id, TableEntryType.FOOD_REQUEST);
    String value = t.getNewValue();
    entry.setAssignee(value);
    dbConnection.updateEntry(entry);
  }

  public void onSetFurnitureEdit(TableColumn.CellEditEvent<FurnitureOrderObservable, String> t) {

    FurnitureOrderObservable row = t.getRowValue();
    FurnitureRequest entry =
        (FurnitureRequest) dbConnection.getEntry(row.id, TableEntryType.FURNITURE_REQUEST);
    String value = t.getNewValue();
    entry.setAssignee(value);
    dbConnection.updateEntry(entry);
  }

  public void onSetFlowerEdit(TableColumn.CellEditEvent<FlowerOrderObservable, String> t) {
    FlowerOrderObservable row = t.getRowValue();
    FlowerRequest entry =
        (FlowerRequest) dbConnection.getEntry(row.id, TableEntryType.FLOWER_REQUEST);
    String value = t.getNewValue();
    entry.setAssignee(value);
    dbConnection.updateEntry(entry);
  }

  public List<String> getAssignees() {
    return dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT).stream()
        .map(obj -> ((UserAccount) obj).getUsername())
        .toList();
  }

  public void setOutstandingTable() {
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

    ObservableList<String> ol = FXCollections.observableList(getAssignees());
    supplyassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    foodassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    furnitureassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    flowerassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    foodTable.setEditable(true);
    furnitureTable.setEditable(true);
    supplyTable.setEditable(true);
    flowerTable.setEditable(true);

    foodTable.setItems(getOutstandingFoodOrderRows());
    supplyTable.setItems(getOutstandingSupplyOrderRows());
    furnitureTable.setItems(getOutstandingFurnitureOrderRows());
    flowerTable.setItems(getOutstandingFlowerOrderRows());
  }

  public void setTable() {
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

    ObservableList<String> ol = FXCollections.observableList(getAssignees());
    supplyassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    foodassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    furnitureassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    flowerassignee.setCellFactory(ChoiceBoxTableCell.forTableColumn(ol));
    foodTable.setEditable(true);
    furnitureTable.setEditable(true);
    supplyTable.setEditable(true);
    flowerTable.setEditable(true);

    // foodTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

    // foodTable.autosize();

    foodTable.setItems(getFoodOrderRows());

    supplyTable.setItems(getSupplyOrderRows());
    furnitureTable.setItems(getFurnitureOrderRows());
     flowerTable.setItems(getFlowerOrderRows());
  }

  public ObservableList<FoodOrderObservable> getFoodOrderRows() {
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dbConnection.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();

    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    Predicate<FoodOrderObservable> filter = p -> p.getFoodassignee().isEmpty();
    FilteredList<FoodOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<SupplyOrderObservable> getSupplyOrderRows() {
    ArrayList<SupplyRequest> supplyList =
        (ArrayList<SupplyRequest>) dbConnection.getAllEntries(TableEntryType.SUPPLY_REQUEST);
    ObservableList<SupplyOrderObservable> returnable = FXCollections.observableArrayList();
    for (SupplyRequest request : supplyList) {
      returnable.add(new SupplyOrderObservable(request));
    }
    Predicate<SupplyOrderObservable> filter = p -> p.getSupplyassignee().isEmpty();
    FilteredList<SupplyOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<FurnitureOrderObservable> getFurnitureOrderRows() {
    ArrayList<FurnitureRequest> furnitureList =
        (ArrayList<FurnitureRequest>) dbConnection.getAllEntries(TableEntryType.FURNITURE_REQUEST);
    ObservableList<FurnitureOrderObservable> returnable = FXCollections.observableArrayList();
    for (FurnitureRequest request : furnitureList) {
      returnable.add(new FurnitureOrderObservable(request));
    }
    Predicate<FurnitureOrderObservable> filter = p -> p.getFurnitureassignee().isEmpty();
    FilteredList<FurnitureOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<FlowerOrderObservable> getFlowerOrderRows() {
    ArrayList<FlowerRequest> flowerList =
        (ArrayList<FlowerRequest>) dbConnection.getAllEntries(TableEntryType.FLOWER_REQUEST);
    ObservableList<FlowerOrderObservable> returnable = FXCollections.observableArrayList();
    for (FlowerRequest request : flowerList) {
      returnable.add(new FlowerOrderObservable(request));
    }
    Predicate<FlowerOrderObservable> filter = p -> p.getFlowerassignee().isEmpty();
    FilteredList<FlowerOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<FoodOrderObservable> getOutstandingFoodOrderRows() {
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dbConnection.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();

    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    Predicate<FoodOrderObservable> filter = p -> p.getFoodstatus().equals("Submitted");
    FilteredList<FoodOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<SupplyOrderObservable> getOutstandingSupplyOrderRows() {
    ArrayList<SupplyRequest> supplyList =
        (ArrayList<SupplyRequest>) dbConnection.getAllEntries(TableEntryType.SUPPLY_REQUEST);
    ObservableList<SupplyOrderObservable> returnable = FXCollections.observableArrayList();
    for (SupplyRequest request : supplyList) {
      returnable.add(new SupplyOrderObservable(request));
    }
    Predicate<SupplyOrderObservable> filter = p -> p.getSupplystatus().equals("Submitted");
    FilteredList<SupplyOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<FurnitureOrderObservable> getOutstandingFurnitureOrderRows() {
    ArrayList<FurnitureRequest> furnitureList =
        (ArrayList<FurnitureRequest>) dbConnection.getAllEntries(TableEntryType.FURNITURE_REQUEST);
    ObservableList<FurnitureOrderObservable> returnable = FXCollections.observableArrayList();
    for (FurnitureRequest request : furnitureList) {
      returnable.add(new FurnitureOrderObservable(request));
    }
    Predicate<FurnitureOrderObservable> filter = p -> p.getFurniturestatus().equals("Submitted");
    FilteredList<FurnitureOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  public ObservableList<FlowerOrderObservable> getOutstandingFlowerOrderRows() {
    ArrayList<FlowerRequest> flowerList =
        (ArrayList<FlowerRequest>) dbConnection.getAllEntries(TableEntryType.FLOWER_REQUEST);
    ObservableList<FlowerOrderObservable> returnable = FXCollections.observableArrayList();
    for (FlowerRequest request : flowerList) {
      returnable.add(new FlowerOrderObservable(request));
    }
    Predicate<FlowerOrderObservable> filter = p -> p.getFlowerstatus().equals("Submitted");
    FilteredList<FlowerOrderObservable> filteredList = new FilteredList<>(returnable, filter);
    return filteredList;
  }

  /*
  private void setupPaginated() {

    MFXTableColumn<FoodOrderObservable> foodId =
        new MFXTableColumn<>("Food ID", true, Comparator.comparing(FoodOrderObservable::getFoodid));
    MFXTableColumn<FoodOrderObservable> foodTotalPrice =
        new MFXTableColumn<>(
            "Food Total Price", true, Comparator.comparing(FoodOrderObservable::getFoodtotalprice));
    MFXTableColumn<FoodOrderObservable> foodStatus =
        new MFXTableColumn<>(
            "Food Status", true, Comparator.comparing(FoodOrderObservable::getFoodstatus));
    MFXTableColumn<FoodOrderObservable> foodDeliveryRoom =
        new MFXTableColumn<>(
            "Food Delivery Room",
            true,
            Comparator.comparing(FoodOrderObservable::getFooddeliveryroom));
    MFXTableColumn<FoodOrderObservable> foodDeliveryTime =
        new MFXTableColumn<>(
            "Food Delivery Time",
            true,
            Comparator.comparing(FoodOrderObservable::getFooddeliverytime));
    MFXTableColumn<FoodOrderObservable> foodRecipientName =
        new MFXTableColumn<>(
            "Food Recipient Name",
            true,
            Comparator.comparing(FoodOrderObservable::getFoodrecipientname));
    MFXTableColumn<FoodOrderObservable> foodNotes =
        new MFXTableColumn<>(
            "Food Notes", true, Comparator.comparing(FoodOrderObservable::getFoodnotes));
    MFXTableColumn<FoodOrderObservable> foodItems =
        new MFXTableColumn<>(
            "Food Items", true, Comparator.comparing(FoodOrderObservable::getFooditems));
    TableColumn<FoodOrderObservable, String> foodAssignee;

    foodId.setRowCellFactory(device -> new MFXTableRowCell<>(FoodOrderObservable::getFoodid));
    foodTotalPrice.setRowCellFactory(
        device -> new MFXTableRowCell<>(FoodOrderObservable::getFoodtotalprice));
    foodStatus.setRowCellFactory(
        device -> new MFXTableRowCell<>(FoodOrderObservable::getFoodstatus));
    foodDeliveryRoom.setRowCellFactory(
        device -> new MFXTableRowCell<>(FoodOrderObservable::getFooddeliveryroom));
    foodDeliveryTime.setRowCellFactory(
        device -> new MFXTableRowCell<>(FoodOrderObservable::getFooddeliverytime));
    foodRecipientName.setRowCellFactory(
        device -> new MFXTableRowCell<>(FoodOrderObservable::getFoodrecipientname));
    foodNotes.setRowCellFactory(device -> new MFXTableRowCell<>(FoodOrderObservable::getFoodnotes));
    foodItems.setRowCellFactory(device -> new MFXTableRowCell<>(FoodOrderObservable::getFooditems));
    foodAssignee.setCellValueFactory(
            ChoiceBoxTableCell.forTableColumn(FoodOrderObservable::getFoodassignee));


    paginated
        .getTableColumns()
        .addAll(
            foodId,
            foodTotalPrice,
            foodStatus,
            foodDeliveryRoom,
            foodDeliveryTime,
            foodRecipientName,
            foodNotes,
            foodItems,
            foodAssignee);
    paginated
        .getFilters()
        .addAll(
            new StringFilter<>("Food ID", FoodOrderObservable::getFoodid),
            new StringFilter<>("Food Total Price", FoodOrderObservable::getFoodtotalprice),
            new StringFilter<>("Food Status", FoodOrderObservable::getFoodstatus),
            new StringFilter<>("Food Delivery Room", FoodOrderObservable::getFooddeliveryroom),
            new StringFilter<>("Food Delivery Time", FoodOrderObservable::getFooddeliverytime),
            new StringFilter<>("Food Recipient Name", FoodOrderObservable::getFoodrecipientname),
            new StringFilter<>("Food Notes", FoodOrderObservable::getFoodnotes),
            new StringFilter<>("Food Items", FoodOrderObservable::getFooditems),
            new StringFilter<>("Food Items", FoodOrderObservable::getFoodassignee));
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dbConnection.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();
    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    paginated.setItems(returnable);
  }

   */
}
