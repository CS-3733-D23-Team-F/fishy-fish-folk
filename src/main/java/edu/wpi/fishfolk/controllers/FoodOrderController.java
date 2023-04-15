package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.CreditCardInfo;
import edu.wpi.fishfolk.ui.FoodItem;
import edu.wpi.fishfolk.ui.FoodOrder;
import edu.wpi.fishfolk.ui.Room;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

public class FoodOrderController extends AbsController {
  private static String[] headersArray = {"id", "items", "status", "assignee", "room", "time"};
  public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));

  Table foodOrderTable;
  FoodOrder currentOrder;
  ArrayList<FoodItem> menu;
  ArrayList<String> roomsServiced;
  int hoursAhead;
  int currentPage; // for menus with more than 3 items
  int[] itemQuantities;
  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton supplyNav;
  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML MFXButton exitButton;
  @FXML AnchorPane slider;
  @FXML MFXButton serviceNav;

  @FXML MFXButton closeServiceNav;
  @FXML MFXButton furnitureNav;
  @FXML MFXButton flowerNav;
  @FXML MFXButton conferenceNav;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML AnchorPane serviceBar;
  @FXML MFXButton prevPageButton, nextPageButton;
  @FXML MFXButton addOneButton, addTwoButton, addThreeButton;
  @FXML MFXButton removeOneButton, removeTwoButton, removeThreeButton;
  @FXML MFXButton plusHour, plusDay, minusHour, minusDay, asapButton;
  @FXML MFXButton clearButton, cancelButton, submitButton;
  @FXML Text itemText1, itemText2, itemText3;
  @FXML Text itemPrice1, itemPrice2, itemPrice3;
  @FXML Text itemQuantity1, itemQuantity2, itemQuantity3;
  @FXML Text timeText;
  @FXML ComboBox<String> roomSelector;
  PopOver notiPop;
  Text[] nameBoxes, priceBoxes, quantityBoxes;

  public FoodOrderController() {
    super();
    foodOrderTable = new Table(dbConnection.conn, "foodorder");
    foodOrderTable.init(false);
    foodOrderTable.addHeaders(
        FoodOrderController.headers,
        new ArrayList<>(List.of("String", "String", "String", "String", "String", "String")));
  }

  @FXML
  private void initialize() {
    prevPageButton.setOnAction(event -> prevPage());
    nextPageButton.setOnAction(event -> nextPage());
    addOneButton.setOnAction(event -> addToOrder(0));
    addTwoButton.setOnAction(event -> addToOrder(1));
    addThreeButton.setOnAction(event -> addToOrder(2));
    removeOneButton.setOnAction(event -> removeFromOrder(0));
    removeTwoButton.setOnAction(event -> removeFromOrder(1));
    removeThreeButton.setOnAction(event -> removeFromOrder(2));
    plusDay.setOnAction(event -> delayDay());
    plusHour.setOnAction(event -> delayHour());
    minusDay.setOnAction(event -> undelayDay());
    minusHour.setOnAction(event -> undelayHour());

    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    supplyNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    closeServiceNav.setVisible(false);
    closeServiceNav.setDisable(true);

    serviceNav.setOnMouseClicked(
        event -> {
          // menuWrap.setDisable(false);
          serviceBar.setVisible(true);
          serviceBar.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToY(490);
          slide.play();

          // slider.setTranslateY(-400);
          slider.setTranslateY(490);
          // menuWrap.setVisible(true);
          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(false);
                closeServiceNav.setVisible(true);
                serviceNav.setDisable(true);
                closeServiceNav.setDisable(false);
              });
        });

    closeServiceNav.setOnMouseClicked(
        event -> {
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToY(0);
          slide.play();

          slider.setTranslateY(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                serviceNav.setVisible(true);
                closeServiceNav.setVisible(false);
                serviceNav.setDisable(false);
                closeServiceNav.setDisable(true);
                serviceBar.setVisible(false);
                serviceBar.setDisable(true);
              });
        });

    submitButton.setOnAction(
        event -> {
          if (currentOrder.submit()) {
            foodOrderTable.insert(currentOrder);
            // notify confirmation
            Navigation.navigate(Screen.HOME);
          } else {
            String errors = "Could not submit order because:";
            if (currentOrder.items.isEmpty()) {
              errors += "\nOrder has no items";
            }
            if (currentOrder.deliveryLocation == null) {
              errors += "\nDelivery location not specified";
            }
            if (LocalDateTime.now().isAfter(currentOrder.deliveryTime)) {
              errors += "\nInvalid delivery time";
            }
            if (currentOrder.payer == null) {
              errors += "\nPayer is not specified";
            }
            Text popText = new Text();
            popText.setText(errors);
            notiPop = new PopOver();
            notiPop.setContentNode(popText);
            notiPop.show(submitButton);
            currentOrder.setSubmitted();
            currentOrder.formID = "" + System.currentTimeMillis();
          }
        });
    clearButton.setOnAction(event -> init());
    cancelButton.setOnAction(
        event -> {
          init();
          Navigation.navigate(Screen.HOME);
        });
    roomSelector.setOnAction(event -> currentOrder.deliveryLocation = roomSelector.getValue());
    loadMenu();
    loadRooms();
    currentPage = 0;
    nameBoxes = new Text[] {itemText1, itemText2, itemText3};
    priceBoxes = new Text[] {itemPrice1, itemPrice2, itemPrice3};
    quantityBoxes = new Text[] {itemQuantity1, itemQuantity2, itemQuantity3};
    init();
  }

  void init() {
    currentOrder = new FoodOrder();
    currentOrder.payer = CreditCardInfo.dummy;
    itemQuantities = new int[menu.size()];
    hoursAhead = 0;
    roomSelector.setValue(null);
    updateDisplay();
  }

  void loadRooms() {
    roomsServiced = new ArrayList<String>();
    roomsServiced.add(Room.generic1.toString());
    roomsServiced.add(Room.generic2.toString());
    roomsServiced.add(Room.generic3.toString());
    roomsServiced.add(Room.generic4.toString());
    roomSelector.getItems().addAll(roomsServiced);
  }

  void loadMenu() {
    menu = new ArrayList<FoodItem>();
    menu.add(FoodItem.generic1);
    menu.add(FoodItem.generic2);
    menu.add(FoodItem.generic3);
    menu.add(FoodItem.generic4);
    menu.add(FoodItem.generic5);
    menu.add(FoodItem.generic6);
    while (menu.size() % 3 != 0) {
      menu.add(FoodItem.filler);
    }
  }

  void updateDisplay() {
    int index = 3 * currentPage;
    for (int i = 0; i < 3; i++) {
      nameBoxes[i].setText(menu.get(index + i).itemName);
      priceBoxes[i].setText(String.format("$%.2f", menu.get(index + i).price));
      quantityBoxes[i].setText("" + itemQuantities[index + i]);
    }
    timeText.setText(
        currentOrder.deliveryTime.format(DateTimeFormatter.ofPattern("EE, MM/dd\nh:mma")));
    // System.out.println("\n\nCurrent order: \n" + currentOrder.toString());
  }

  private void nextPage() {
    if ((currentPage + 1) * 3 >= menu.size()) return;
    currentPage++;
    updateDisplay();
  }

  private void prevPage() {
    if (currentPage == 0) return;
    currentPage--;
    updateDisplay();
  }

  void setTimeAsap() {
    currentOrder.deliveryTime = LocalDateTime.now().plusMinutes(15);
    updateDisplay();
  }

  void setTimeHours(int hours) {
    currentOrder.deliveryTime = LocalDateTime.now().plusHours(hours);
    updateDisplay();
  }

  private void delayHour() {
    hoursAhead++;
    setTimeHours(hoursAhead);
  }

  private void undelayHour() {
    if (hoursAhead > 1) {
      hoursAhead--;
      setTimeHours(hoursAhead);
    } else {
      hoursAhead = 0;
      setTimeAsap();
    }
  }

  private void delayDay() {
    hoursAhead += 24;
    setTimeHours(hoursAhead);
  }

  private void undelayDay() {
    if (hoursAhead > 25) {
      hoursAhead -= 24;
      setTimeHours(hoursAhead);
    } else {
      hoursAhead = 0;
      setTimeAsap();
    }
  }

  private void addToOrder(int posOnPage) {
    int itemNum = 3 * currentPage + posOnPage;
    FoodItem item = menu.get(itemNum);
    if (item == FoodItem.filler) {
      return;
    }
    itemQuantities[itemNum]++;
    currentOrder.addItem(item);
    updateDisplay();
  }

  private void removeFromOrder(int posOnPage) {
    int itemNum = 3 * currentPage + posOnPage;
    if (itemQuantities[itemNum] == 0) return;
    itemQuantities[itemNum]--;
    currentOrder.removeItem(menu.get(itemNum));
    updateDisplay();
  }
}
