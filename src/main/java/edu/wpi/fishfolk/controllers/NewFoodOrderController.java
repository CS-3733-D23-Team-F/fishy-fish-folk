package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.FoodCategory;
import edu.wpi.fishfolk.ui.NewFoodCart;
import edu.wpi.fishfolk.ui.NewFoodMenuItem;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
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

public class NewFoodOrderController extends AbsController {
  @FXML MFXButton appsTab, sidesTab, mainsTab, drinksTab, dessertsTab; // tab buttons
  @FXML MFXButton clearButton, cancelButton, checkoutButton; // main page buttons
  @FXML MFXButton submitButton, backButton; // cart view buttons
  @FXML MFXFilterComboBox<String> roomSelector;
  @FXML TextField recipientField, timeSelector;
  @FXML TextArea notesField;
  @FXML ScrollPane menuItemsPane, cartItemsPane;
  @FXML AnchorPane cartViewPane;
  List<NewFoodMenuItem>[] menuTabs; // Apps, Sides, Mains, Drinks, Desserts
  NewFoodCart cart;

  public NewFoodOrderController() {
    super();
  }

  @FXML
  private void initialize() {
    loadMenu();
    loadRooms();
    cart = new NewFoodCart();
    cartViewPane.setVisible(false);
    cartViewPane.setDisable(true);
    cancelButton.setOnAction(event -> cancel());
    clearButton.setOnAction(event -> clear());
    checkoutButton.setOnAction(event -> openCart());
    submitButton.setOnAction(event -> submit());
    backButton.setOnAction(event -> closeCart());
    tab(4);
  }

  private void loadMenu() {
    menuTabs = new List[5];
    for (int i = 0; i < 5; i++) {
      menuTabs[i] = new ArrayList<NewFoodMenuItem>();
    }

    // temp
    NewFoodMenuItem chocoCake =
        new NewFoodMenuItem(
            "Chocolate Cake",
            "A slice of Delicious, Scrumptious, Overly well described Chocolate Cake. Optionally served with whipped cream.",
            "images/chocoCake.jpg", // jesus if i can
            // just get this
            // to work
            // this'll look
            // amazing
            3.50f,
            FoodCategory.dessert);

    for (int i = 0; i < 7; i++) {
      menuTabs[4].add(chocoCake);
    }
    // todo this might be a large pain with how much menu there is to do (categorization etc)
  }

  private void loadRooms() {
    // todo write function once exists on DB side
  }

  private void clear() {
    cart = new NewFoodCart();
  }

  private void cancel() {
    cart = null;
    Navigation.navigate(Screen.HOME); // todo discuss changing this to service request
  }

  private void openCart() {
    loadCart();
    notesField.setWrapText(true);
    cartViewPane.setDisable(false);
    cartViewPane.setVisible(true);
  }

  private void closeCart() {
    cartViewPane.setDisable(true);
    cartViewPane.setVisible(false);
  }

  private void submit() {
    // todo send orders to DB on submission
  }

  private void tab(int target) {
    List<NewFoodMenuItem> items = menuTabs[target];
    int numRows = (items.size() + 1) / 2;
    System.out.printf("%d items, %d rows\n", items.size(), numRows);
    VBox itemRows = new VBox();
    itemRows.setSpacing(25);
    itemRows.setPrefHeight(Math.max(0, (275 * numRows) - 35));
    itemRows.setPrefWidth(1195);
    for (int i = 0; i < numRows; i++) {
      HBox itemRow = new HBox();
      itemRow.setSpacing(25);
      itemRow.setPrefHeight(250);
      itemRow.setPrefWidth(1195);
      for (int j = 0; j < 2; j++) {
        NewFoodMenuItem currentItem = null;
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
          itemName.setFont(new Font("Open Sans Regular", 26));
          itemName.setTextAlignment(TextAlignment.LEFT);
          itemPane.getChildren().add(itemName);
          Text itemPrice = new Text();
          itemPrice.setText(String.format("$%.2f", currentItem.getPrice()));
          itemPrice.setLayoutY(42);
          itemPrice.setLayoutX(225);
          itemPrice.setStrokeType(StrokeType.OUTSIDE);
          itemPrice.setStrokeWidth(0);
          itemPrice.setWrappingWidth(345);
          itemPrice.setFont(new Font("Open Sans Regular", 26));
          itemPrice.setTextAlignment(TextAlignment.RIGHT);
          itemPane.getChildren().add(itemPrice);
          Text itemDescription = new Text();
          itemDescription.setText(currentItem.getDescription());
          itemDescription.setLayoutY(72);
          itemDescription.setLayoutX(225);
          itemDescription.setStrokeType(StrokeType.OUTSIDE);
          itemDescription.setStrokeWidth(0);
          itemDescription.setWrappingWidth(345);
          itemDescription.setFont(new Font("Open Sans Regular", 20));
          itemDescription.setTextAlignment(TextAlignment.LEFT);
          itemPane.getChildren().add(itemDescription);
          MFXButton addItemButton = new MFXButton();
          addItemButton.setFont(new Font("Open Sans Regular", 20));
          addItemButton.setText("Add to Order");
          addItemButton.setLayoutX(225);
          addItemButton.setLayoutY(177);
          addItemButton.setPrefHeight(43);
          addItemButton.setPrefWidth(330);
          addItemButton.setStyle("-fx-background-color: #012d5a;");
          addItemButton.setTextFill(Paint.valueOf("WHITE"));
          NewFoodMenuItem finalCurrentItem = currentItem;
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
  }

  private void loadCart() {
    List<NewFoodCart.quantityItem> items = cart.getItems();
    VBox itemsBox = new VBox();
    itemsBox.setPrefHeight(140 * items.size());
    itemsBox.setPrefWidth(1190);
    for (NewFoodCart.quantityItem item : items) {
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
      itemName.setFont(new Font("Open Sans Regular", 26));
      itemName.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(itemName);
      Text unitPrice = new Text();
      unitPrice.setText(String.format("$%.2f", item.getItem().getPrice()));
      unitPrice.setLayoutY(75);
      unitPrice.setLayoutX(302);
      unitPrice.setStrokeType(StrokeType.OUTSIDE);
      unitPrice.setStrokeWidth(0);
      unitPrice.setWrappingWidth(100);
      unitPrice.setFont(new Font("Open Sans Regular", 26));
      unitPrice.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(unitPrice);
      Text quantityText = new Text();
      quantityText.setText(String.format("x%d", item.getQuantity()));
      quantityText.setLayoutY(75);
      quantityText.setLayoutX(497);
      quantityText.setStrokeType(StrokeType.OUTSIDE);
      quantityText.setStrokeWidth(0);
      quantityText.setWrappingWidth(100);
      quantityText.setFont(new Font("Open Sans Regular", 26));
      quantityText.setTextAlignment(TextAlignment.CENTER);
      itemPane.getChildren().add(quantityText);
      Text totalPrice = new Text();
      totalPrice.setText(String.format("$%.2f", item.getItem().getPrice() * item.getQuantity()));
      totalPrice.setLayoutY(75);
      totalPrice.setLayoutX(700);
      totalPrice.setStrokeType(StrokeType.OUTSIDE);
      totalPrice.setStrokeWidth(0);
      totalPrice.setWrappingWidth(100);
      totalPrice.setFont(new Font("Open Sans Regular", 26));
      totalPrice.setTextAlignment(TextAlignment.LEFT);
      itemPane.getChildren().add(totalPrice);
      MFXButton minusButton = new MFXButton();
      minusButton.setLayoutX(450);
      minusButton.setLayoutY(51);
      minusButton.setText("-");
      minusButton.setMinHeight(28);
      minusButton.setMinWidth(28);
      minusButton.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 14;");
      final NewFoodMenuItem currentItem = item.getItem();
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
      removeButton.setFont(new Font("Open Sans Regular", 26));
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
