package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.FlowerRequest;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PopOver;

public class NewFlowerOrderController extends AbsController {
  @FXML MFXButton springTab, gratitudeTab, sympathyTab; // tab buttons
  @FXML MFXButton clearButton, cancelButton, checkoutButton; // main page buttons
  @FXML MFXButton submitButton, backButton; // cart view buttons
  @FXML MFXFilterComboBox<String> roomSelector;
  @FXML TextField recipientField, timeSelector;
  @FXML TextArea notesField;
  @FXML ScrollPane menuItemsPane, cartItemsPane;
  @FXML AnchorPane cartViewPane, confirmPane;
  @FXML HBox cartWrap, blur, confirmBlur, confirmBox;
  @FXML MFXButton okButton;
  Font oSans26, oSans20, oSans26bold;

  private List<FlowerMenuItem>[] menuTabs; // Apps, Sides, Mains, Drinks, Desserts
  private FlowerCart cart;
  MFXButton[] tabButtons;

  public NewFlowerOrderController() {
    super();
  }

  /** Prepare buttons, cart, items, room selector, and load the appetizers tab */
  @FXML
  private void initialize() {
    loadMenu();
    loadRooms();
    cart = new FlowerCart();
    cartViewPane.setVisible(false);
    cartViewPane.setDisable(true);
    cancelButton.setOnAction(event -> cancel());
    clearButton.setOnAction(event -> clear());
    checkoutButton.setOnAction(event -> openCart());
    submitButton.setOnAction(event -> submit());
    backButton.setOnAction(event -> closeCart());
    okButton.setOnAction(event -> Navigation.navigate(SharedResources.getHome()));
    springTab.setOnAction(event -> tab(0));
    gratitudeTab.setOnAction(event -> tab(1));
    sympathyTab.setOnAction(event -> tab(2));
    tabButtons = new MFXButton[] {springTab, gratitudeTab, sympathyTab};
    oSans20 = new Font("Open Sans Regular", 20);
    oSans26 = new Font("Open Sans Regular", 26);
    oSans26bold = new Font("Open Sans Bold", 26);
    tab(0);
  }

  /** Load food items into Respoective menu tabs */
  private void loadMenu() {
    menuTabs = new List[3];
    for (int i = 0; i < 3; i++) {
      menuTabs[i] = new ArrayList<FlowerMenuItem>();
    }

    List<FlowerMenuItem> allItems = FlowerMenuLoader.loadItems();
    for (FlowerMenuItem item : allItems) {
      switch (item.getCat()) {
        case spring:
          {
            menuTabs[0].add(item);
            break;
          }
        case gratitude:
          {
            menuTabs[1].add(item);
            break;
          }
        case sympathy:
          {
            menuTabs[2].add(item);
            break;
          }
      }
    }
  }

  /** Load room list into Room Selector */
  private void loadRooms() {
    roomSelector.getItems().addAll(dbConnection.getDestLongnames());
    // roomSelector.getItems().add("A room");
  }

  /** remove all items from the cart */
  private void clear() {
    cart = new FlowerCart();
  }

  /** Clear the cart, and Return Home */
  private void cancel() {
    cart = null;
    Navigation.navigate(SharedResources.getHome()); // todo discuss changing this to service request
  }

  /** Load cart items into viewable format, and put the cart on screen */
  /** Load cart items into viewable format, and put the cart on screen */
  private void openCart() {
    loadCart();
    notesField.setWrapText(true);
    cartViewPane.setDisable(false);
    cartViewPane.setVisible(true);
    cartWrap.setDisable(false);
    blur.setDisable(false);
    blur.setVisible(true);
  }

  /** Hide the cart */
  private void closeCart() {
    cartViewPane.setDisable(true);
    cartViewPane.setVisible(false);
    cartWrap.setDisable(true);
    blur.setDisable(true);
    blur.setVisible(false);
  }

  /** Confirm the order, and add it to the Database */
  private void submit() {
    String notes = notesField.getText();
    LocalTime time = parseTime();
    String room = roomSelector.getValue();
    String recipient = recipientField.getText();
    if (cart.getTotalPrice() == 0) {
      itemsError();
      return;
    }
    if (room == null) {
      roomError();
      return;
    }
    if (time == null) {
      timeError();
      return;
    }
    if (recipient.equals("")) {
      recipientError();
      return;
    }
    List<FlowerItem> items = cart.getSubmittableItems();
    LocalDateTime deliveryTime = LocalDateTime.of(LocalDate.now(), time);
    if (deliveryTime.isBefore(LocalDateTime.now())) {
      deliveryTime.plusDays(1);
    }

    FlowerRequest thisOrder =
        new FlowerRequest(
            "",
            FormStatus.submitted,
            notes,
            recipientField.getText(),
            room,
            deliveryTime,
            cart.getTotalPrice(),
            items);
    dbConnection.insertEntry(thisOrder);
    blur.setDisable(true);
    blur.setVisible(false);
    confirmBlur.setDisable(false);
    confirmBlur.setVisible(true);
    confirmBox.setDisable(false);
    confirmBox.setVisible(true);
    confirmPane.setVisible(true);
    confirmPane.setDisable(false);
  }

  /**
   * gets the time in the time selector
   *
   * @return the time as a LocalTime
   */
  private LocalTime parseTime() {
    String timeSel = timeSelector.getText();
    int pos = timeSel.indexOf(":");
    int h = -1, m = -1;
    if (pos != -1) {
      h = Integer.parseInt(timeSel.substring(0, pos));
      if (timeSel.length() - pos >= 3) {
        m = Integer.parseInt(timeSel.substring(pos + 1, pos + 3));
      }
    }
    if (h == -1 || m == -1) {
      return null;
    }
    if (timeSel.toLowerCase().indexOf("pm") >= 0) {
      if (h != 12) {
        h += 12;
      }
    } else if (timeSel.toLowerCase().indexOf("am") >= 0) {
      if (h == 12) {
        h = 0;
      }
    }
    if (h > 23 || m >= 60) {
      return null;
    }
    return LocalTime.of(h, m);
  }

  /** Informs the user they have not input a valid time */
  private void timeError() {
    submissionError("Please enter a valid time.");
  }

  /** informs the user they have not selected a room */
  private void roomError() {
    submissionError("Please select a room.");
  }

  /** informs the user they have not selected any items */
  private void itemsError() {
    submissionError("Please select at least one item.");
  }

  /** informs the user they have not specified the recipient of the order */
  private void recipientError() {
    submissionError("Please enter a recipient.");
  }

  /**
   * pops up an error if the submission is invalid
   *
   * @param error the error message to display
   */
  private void submissionError(String error) {
    PopOver popup = new PopOver();
    Text popText = new Text(error);
    popText.setFont(oSans26);
    popup.setContentNode(popText);
    popup.show(submitButton);
  }

  /**
   * Loads a tab to the on-screen menu
   *
   * @param target the page to load - 0 is apps, 1 is sides, 2 is mains, 3 is drinks, 4 is desserts
   */
  private void tab(int target) {
    List<FlowerMenuItem> items = menuTabs[target];
    int numRows = (items.size() + 1) / 2;
    System.out.printf("%d items, %d rows\n", items.size(), numRows);
    VBox itemRows = new VBox();
    itemRows.setSpacing(25);
    itemRows.setPrefHeight(Math.max(0, (275 * numRows) - 25));
    itemRows.setPrefWidth(1195);
    for (int i = 0; i < numRows; i++) {
      HBox itemRow = new HBox();
      itemRow.setSpacing(25);
      itemRow.setPrefHeight(250);
      itemRow.setPrefWidth(1195);
      for (int j = 0; j < 2; j++) {
        FlowerMenuItem currentItem = null;
        try {
          currentItem = items.get(i * 2 + j);
          AnchorPane itemPane = new AnchorPane();
          itemPane.setPrefHeight(250);
          itemPane.setPrefWidth(585);
          Rectangle bgRectangle = new Rectangle();
          bgRectangle.setArcHeight(5);
          bgRectangle.setArcWidth(5);
          bgRectangle.setFill(Paint.valueOf("WHITE"));
          bgRectangle.setHeight(230);
          bgRectangle.setWidth(565);
          bgRectangle.setLayoutX(10);
          bgRectangle.setLayoutY(10);
          bgRectangle.setStroke(Paint.valueOf("#012d5a"));
          bgRectangle.setStrokeType(StrokeType.INSIDE);
          itemPane.getChildren().add(bgRectangle);
          ImageView itemImage = new ImageView();
          itemImage.setFitHeight(200);
          itemImage.setFitWidth(200);
          itemImage.setLayoutX(25);
          itemImage.setLayoutY(25);
          itemImage.setPickOnBounds(true);
          itemImage.setPreserveRatio(true);
          final var resource = Fapp.class.getResource(currentItem.getImageLoc());
          itemImage.setImage(new Image(resource.openStream()));
          itemPane.getChildren().add(itemImage);
          Text itemName = new Text();
          itemName.setText(currentItem.getName());
          itemName.setLayoutY(42);
          itemName.setLayoutX(225);
          itemName.setStrokeType(StrokeType.OUTSIDE);
          itemName.setStrokeWidth(0);
          itemName.setWrappingWidth(345);
          itemName.setFont(oSans26bold);
          itemName.setTextAlignment(TextAlignment.LEFT);
          itemPane.getChildren().add(itemName);
          Text itemPrice = new Text();
          itemPrice.setText(String.format("$%.2f", currentItem.getPrice()));
          itemPrice.setLayoutY(42);
          itemPrice.setLayoutX(225);
          itemPrice.setStrokeType(StrokeType.OUTSIDE);
          itemPrice.setStrokeWidth(0);
          itemPrice.setWrappingWidth(345);
          itemPrice.setFont(oSans26);
          itemPrice.setTextAlignment(TextAlignment.RIGHT);
          itemPane.getChildren().add(itemPrice);
          Text itemDescription = new Text();
          itemDescription.setText(currentItem.getDescription());
          itemDescription.setLayoutY(72);
          itemDescription.setLayoutX(225);
          itemDescription.setStrokeType(StrokeType.OUTSIDE);
          itemDescription.setStrokeWidth(0);
          itemDescription.setWrappingWidth(345);
          itemDescription.setFont(oSans20);
          itemDescription.setTextAlignment(TextAlignment.LEFT);
          itemPane.getChildren().add(itemDescription);
          MFXButton addItemButton = new MFXButton();
          addItemButton.setFont(oSans20);
          addItemButton.setText("Add to Order");
          addItemButton.setLayoutX(225);
          addItemButton.setLayoutY(177);
          addItemButton.setPrefHeight(43);
          addItemButton.setPrefWidth(330);
          addItemButton.setStyle("-fx-background-color: #012d5a;");
          addItemButton.setTextFill(Paint.valueOf("WHITE"));
          FlowerMenuItem finalCurrentItem = currentItem;
          addItemButton.setOnAction(event -> cart.add(finalCurrentItem));
          itemPane.getChildren().add(addItemButton);
          itemRow.getChildren().add(itemPane);
        } catch (Exception E) {
          // This row does not have a 2nd item - thrown here by indexOutOfBoundsException
          System.out.println("Should only get here once, here's what happened " + E.toString());
          AnchorPane itemPane = new AnchorPane();
          itemPane.setPrefHeight(250);
          itemPane.setPrefWidth(585);
          itemRow.getChildren().add(itemPane);
        }
      }
      itemRows.getChildren().add(itemRow);
    }
    menuItemsPane.setContent(itemRows);
    for (int i = 0; i < 3; i++) {
      if (i == target) {
        tabButtons[i].setDisable(true);
      } else {
        tabButtons[i].setDisable(false);
      }
    }
  }

  /** prepares the visual cart with the items that have been added */
  private void loadCart() {
    List<FlowerCart.quantityItem> items = cart.getItems();
    VBox itemsBox = new VBox();
    itemsBox.setPrefHeight(130 * items.size());
    itemsBox.setPrefWidth(1190);
    for (FlowerCart.quantityItem item : items) {
      AnchorPane itemPane = new AnchorPane();
      itemPane.setPrefHeight(130);
      itemPane.setPrefWidth(1190);
      Rectangle bgRectangle = new Rectangle();
      bgRectangle.setArcHeight(5);
      bgRectangle.setArcWidth(5);
      bgRectangle.setFill(Paint.valueOf("WHITE"));
      bgRectangle.setHeight(90);
      bgRectangle.setWidth(1150);
      bgRectangle.setLayoutX(20);
      bgRectangle.setLayoutY(20);
      bgRectangle.setStroke(Paint.valueOf("#012d5a"));
      bgRectangle.setStrokeType(StrokeType.INSIDE);
      itemPane.getChildren().add(bgRectangle);
      Text itemName = new Text();
      itemName.setText(item.getItem().getName());
      itemName.setLayoutY(75);
      itemName.setLayoutX(42);
      itemName.setStrokeType(StrokeType.OUTSIDE);
      itemName.setStrokeWidth(0);
      itemName.setWrappingWidth(300);
      itemName.setFont(oSans26bold);
      itemName.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(itemName);
      Text unitPrice = new Text();
      unitPrice.setText(String.format("$%.2f", item.getItem().getPrice()));
      unitPrice.setLayoutY(75);
      unitPrice.setLayoutX(302);
      unitPrice.setStrokeType(StrokeType.OUTSIDE);
      unitPrice.setStrokeWidth(0);
      unitPrice.setWrappingWidth(100);
      unitPrice.setFont(oSans26);
      unitPrice.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(unitPrice);
      Text quantityText = new Text();
      quantityText.setText(String.format("x%d", item.getQuantity()));
      quantityText.setLayoutY(75);
      quantityText.setLayoutX(497);
      quantityText.setStrokeType(StrokeType.OUTSIDE);
      quantityText.setStrokeWidth(0);
      quantityText.setWrappingWidth(100);
      quantityText.setFont(oSans26);
      quantityText.setTextAlignment(TextAlignment.CENTER);
      itemPane.getChildren().add(quantityText);
      Text totalPrice = new Text();
      totalPrice.setText(String.format("$%.2f", item.getItem().getPrice() * item.getQuantity()));
      totalPrice.setLayoutY(75);
      totalPrice.setLayoutX(700);
      totalPrice.setStrokeType(StrokeType.OUTSIDE);
      totalPrice.setStrokeWidth(0);
      totalPrice.setWrappingWidth(100);
      totalPrice.setFont(oSans26);
      totalPrice.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(totalPrice);
      MFXButton minusButton = new MFXButton();
      minusButton.setLayoutX(450);
      minusButton.setLayoutY(51);
      minusButton.setText("-");
      minusButton.setMinHeight(28);
      minusButton.setMinWidth(28);
      minusButton.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 14;");
      final FlowerMenuItem currentItem = item.getItem();
      minusButton.setOnAction(
          event -> {
            cart.remove(currentItem);
            loadCart();
          });
      itemPane.getChildren().add(minusButton);
      MFXButton plusButton = new MFXButton();
      plusButton.setLayoutX(616);
      plusButton.setLayoutY(51);
      plusButton.setText("+");
      plusButton.setMinHeight(28);
      plusButton.setMinWidth(28);
      plusButton.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 14;");
      plusButton.setOnAction(
          event -> {
            cart.add(currentItem);
            loadCart();
          });
      itemPane.getChildren().add(plusButton);
      MFXButton removeButton = new MFXButton();
      removeButton.setText("Remove from Order");
      removeButton.setLayoutX(844);
      removeButton.setLayoutY(38);
      removeButton.setPrefHeight(55);
      removeButton.setPrefWidth(309);
      removeButton.setFont(oSans26);
      removeButton.setStyle("-fx-background-color: #900000;");
      removeButton.setTextFill(Paint.valueOf("WHITE"));
      removeButton.setOnAction(
          event -> {
            cart.removeAll(currentItem);
            loadCart();
          });
      itemPane.getChildren().add(removeButton);
      itemsBox.getChildren().add(itemPane);
    }
    cartItemsPane.setContent(itemsBox);
  }
}
