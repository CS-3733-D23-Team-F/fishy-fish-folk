package edu.wpi.fishfolk.controllers;

import static edu.wpi.fishfolk.controllers.AbsController.dbConnection;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.DAO.Observables.FoodOrderObservable;
import edu.wpi.fishfolk.database.TableEntry.FoodRequest;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class HomeController {

  // @FXML MFXButton navigateButton;
  @FXML GridPane grid;
  @FXML GridPane alertGrid;
  @FXML MFXPaginatedTableView paginated;

  @FXML
  public void initialize() {
    // navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    int col = 0;
    int row = 1;
    try {
      for (int i = 0; i < 15; i++) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/FutureMoves.fxml"));

        AnchorPane anchorPane = fxmlLoader.load();

        FutureMovesController futureMoves = fxmlLoader.getController();

        futureMoves.setData("Bingus", "01/04/2023");

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
    int colA = 0;
    int rowA = 1;
    try {
      for (int i = 0; i < 15; i++) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fapp.class.getResource("views/Alerts.fxml"));

        AnchorPane anchorPane = fxmlLoader.load();

        AlertsController alerts = fxmlLoader.getController();

        alerts.setData("Bingus", "01/04/2023");

        if (colA == 1) {
          colA = 0;
          rowA++;
        }

        // col++;
        alertGrid.add(anchorPane, colA++, rowA);

        alertGrid.setMinWidth(Region.USE_COMPUTED_SIZE);
        alertGrid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        alertGrid.setMaxWidth(Region.USE_COMPUTED_SIZE);

        alertGrid.setMinHeight(Region.USE_COMPUTED_SIZE);
        alertGrid.setPrefHeight(Region.USE_COMPUTED_SIZE);
        alertGrid.setMaxHeight(Region.USE_COMPUTED_SIZE);

        GridPane.setMargin(anchorPane, new Insets(10));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    setupPaginated();
  }

  private void setupPaginated() {

    MFXTableColumn<FoodOrderObservable> foodId =
        new MFXTableColumn<>(
            "Food ID", false, Comparator.comparing(FoodOrderObservable::getFoodid));
    MFXTableColumn<FoodOrderObservable> foodTotalPrice =
        new MFXTableColumn<>(
            "Food Total Price",
            false,
            Comparator.comparing(FoodOrderObservable::getFoodtotalprice));
    MFXTableColumn<FoodOrderObservable> foodStatus =
        new MFXTableColumn<>(
            "Food Status", false, Comparator.comparing(FoodOrderObservable::getFoodstatus));
    MFXTableColumn<FoodOrderObservable> foodDeliveryRoom =
        new MFXTableColumn<>(
            "Food Delivery Room",
            false,
            Comparator.comparing(FoodOrderObservable::getFooddeliveryroom));
    MFXTableColumn<FoodOrderObservable> foodDeliveryTime =
        new MFXTableColumn<>(
            "Food Delivery Time",
            false,
            Comparator.comparing(FoodOrderObservable::getFooddeliverytime));
    MFXTableColumn<FoodOrderObservable> foodRecipientName =
        new MFXTableColumn<>(
            "Food Recipient Name",
            false,
            Comparator.comparing(FoodOrderObservable::getFoodrecipientname));
    MFXTableColumn<FoodOrderObservable> foodNotes =
        new MFXTableColumn<>(
            "Food Notes", false, Comparator.comparing(FoodOrderObservable::getFoodnotes));
    MFXTableColumn<FoodOrderObservable> foodItems =
        new MFXTableColumn<>(
            "Food Items", false, Comparator.comparing(FoodOrderObservable::getFooditems));

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
            foodItems);
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
            new StringFilter<>("Food Items", FoodOrderObservable::getFooditems));
    ArrayList<FoodRequest> foodList =
        (ArrayList<FoodRequest>) dbConnection.getAllEntries(TableEntryType.FOOD_REQUEST);
    ObservableList<FoodOrderObservable> returnable = FXCollections.observableArrayList();
    for (FoodRequest request : foodList) {
      returnable.add(new FoodOrderObservable(request));
    }
    paginated.setItems(returnable);
  }
}
