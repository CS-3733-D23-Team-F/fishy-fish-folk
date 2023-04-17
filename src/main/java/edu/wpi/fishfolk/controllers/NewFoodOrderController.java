package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.ui.NewFoodCart;
import edu.wpi.fishfolk.ui.NewFoodMenuItem;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class NewFoodOrderController extends AbsController {
  @FXML MFXButton appsTab, sidesTab, mainsTab, drinksTab, dessertsTab; // tab buttons
  @FXML MFXButton clearButton, cancelButton, checkoutButton; // main page buttons
  @FXML MFXButton submitButton, backButton; // cart view buttons
  @FXML ComboBox<String> roomSelector;
  @FXML MFXTextField recipientField, timeSelector;
  @FXML TextArea notesField;
  @FXML ScrollPane menuItemsPane, cartItemsPane;
  @FXML AnchorPane cartViewPane;
  List<NewFoodMenuItem> apps, sides, mains, drinks, desserts;
  NewFoodCart cart;

  public NewFoodOrderController() {
    super();
  }

  @FXML
  private void initialize() {
    loadMenu();
    cartViewPane.setVisible(false);
    cartViewPane.setDisable(true);
    cancelButton.setOnAction(event -> cancel());
    clearButton.setOnAction(event -> clear());
    checkoutButton.setOnAction(event -> openCart());
    submitButton.setOnAction(event -> submit());
    backButton.setOnAction(event -> closeCart());
  }

  private void loadMenu() {
    // todo this might be a large pain with how much menu there is to do (categorization etc)
  }

  private void clear() {}

  private void cancel() {}

  private void openCart() {
    cartViewPane.setDisable(false);
    cartViewPane.setVisible(true);
  }

  private void closeCart() {
    cartViewPane.setDisable(true);
    cartViewPane.setVisible(false);
  }

  private void submit() {}
}
