package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.FurnitureRequest;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.FurnitureItem;
import edu.wpi.fishfolk.ui.FurnitureOrder;
import edu.wpi.fishfolk.ui.ServiceType;
import io.github.palexdev.materialfx.controls.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class NewFurnitureOrderController extends AbsController {

  // @FXML ChoiceBox<String> requestTypePicker;
  @FXML
  MFXRectangleToggleNode radioButton1,
      radioButton2,
      radioButton3,
      radioButton4,
      radioButton5,
      radioButton6;
  @FXML
  MFXRectangleToggleNode serviceradioButton1,
      serviceradioButton2,
      serviceradioButton3,
      serviceradioButton4,
      serviceradioButton5;
  @FXML MFXComboBox<String> roomSelector;
  @FXML MFXDatePicker deliveryDate;
  @FXML MFXTextField notesTextField;
  @FXML MFXButton cancelButton, clearButton, furnituresubmitButton;
  @FXML HBox confirmBlur;
  @FXML HBox confirmBox;
  @FXML AnchorPane confirmPane;
  @FXML MFXButton okButton;
  @FXML Label errors;
  @FXML MFXScrollPane serviceScroll, furnitureScroll;

  FurnitureOrder currentFurnitureOrder = new FurnitureOrder();
  ArrayList<FurnitureItem> furnitureOptions = new ArrayList<>();
  private static TranslateTransition thugShaker;

  // initialize() sets the preliminary fields for the page and defines the functionality of the
  // relevant items
  // ex. radioButton functionality, drop-down menus, etc.
  public void initialize() {
    setToBlue();
    clearError();
    loadOptions();
    loadRoomChoice();
    okButton.setOnAction(event -> Navigation.navigate(SharedResources.getHome()));
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
    furnituresubmitButton.setOnMouseClicked(event -> submit());
    clearButton.setOnMouseClicked(event -> clearAllFields());
    radioButton1.setOnMouseClicked(
        event ->
            deselectRadios(radioButton2, radioButton3, radioButton4, radioButton5, radioButton6));
    radioButton2.setOnMouseClicked(
        event ->
            deselectRadios(radioButton1, radioButton3, radioButton4, radioButton5, radioButton6));
    radioButton3.setOnMouseClicked(
        event ->
            deselectRadios(radioButton1, radioButton2, radioButton4, radioButton5, radioButton6));
    radioButton4.setOnMouseClicked(
        event ->
            deselectRadios(radioButton1, radioButton2, radioButton3, radioButton5, radioButton6));
    radioButton5.setOnMouseClicked(
        event ->
            deselectRadios(radioButton1, radioButton2, radioButton3, radioButton4, radioButton6));
    radioButton6.setOnMouseClicked(
        event ->
            deselectRadios(radioButton1, radioButton2, radioButton3, radioButton4, radioButton5));

    serviceradioButton1.setOnMouseClicked(
        event ->
            deselectServiceRadios(
                serviceradioButton2,
                serviceradioButton3,
                serviceradioButton4,
                serviceradioButton5));
    serviceradioButton2.setOnMouseClicked(
        event ->
            deselectServiceRadios(
                serviceradioButton1,
                serviceradioButton3,
                serviceradioButton4,
                serviceradioButton5));
    serviceradioButton3.setOnMouseClicked(
        event ->
            deselectServiceRadios(
                serviceradioButton1,
                serviceradioButton2,
                serviceradioButton4,
                serviceradioButton5));
    serviceradioButton4.setOnMouseClicked(
        event ->
            deselectServiceRadios(
                serviceradioButton1,
                serviceradioButton2,
                serviceradioButton3,
                serviceradioButton5));
    serviceradioButton5.setOnMouseClicked(
        event ->
            deselectServiceRadios(
                serviceradioButton1,
                serviceradioButton2,
                serviceradioButton3,
                serviceradioButton4));
  }

  // loadOptions() fills furnitureOptions list with possible furniture items to select
  public void loadOptions() {
    furnitureOptions.add(FurnitureItem.bed);
    furnitureOptions.add(FurnitureItem.chair);
    furnitureOptions.add(FurnitureItem.desk);
    furnitureOptions.add(FurnitureItem.fileCabinet);
    furnitureOptions.add(FurnitureItem.clock);
    furnitureOptions.add(FurnitureItem.trashCan);
  }

  /** sets all borders back to blue */
  public void setToBlue() {
    deliveryDate.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    roomSelector.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    serviceScroll.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    furnitureScroll.setStyle(
        "-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
  }

  /** Clears the error field */
  private void clearError() {
    errors.setText("");
    errors.setVisible(false);
  }

  public void clearAllFields() {
    deselectRadios(radioButton1, radioButton2, radioButton3, radioButton4, radioButton5);
    radioButton6.setSelected(false);
    deselectServiceRadios(
        serviceradioButton1, serviceradioButton2, serviceradioButton3, serviceradioButton4);
    serviceradioButton5.setSelected(false);
    currentFurnitureOrder.serviceType = null;
    currentFurnitureOrder.furnitureItem = null;
    notesTextField.setText("");
    roomSelector.setValue(null);
    deliveryDate.setValue(null);
    setToBlue();
    clearError();
  }

  // loadRoomChoice() fills the possible options in the Room Numver choicebox
  public void loadRoomChoice() {
    roomSelector.getItems().addAll(dbConnection.getDestLongnames());
    // roomSelector.getItems().add("Placeholder");
  }

  // deselectRadios() will deselect all of the given buttons.
  // This will be used to make sure only one button can be pressed at a time on the form
  public void deselectRadios(
      MFXRectangleToggleNode offButton1,
      MFXRectangleToggleNode offButton2,
      MFXRectangleToggleNode offButton3,
      MFXRectangleToggleNode offButton4,
      MFXRectangleToggleNode offButton5) {
    offButton1.setSelected(false);
    offButton2.setSelected(false);
    offButton3.setSelected(false);
    offButton4.setSelected(false);
    offButton5.setSelected(false);
  }

  public void deselectServiceRadios(
      MFXRectangleToggleNode offButton1,
      MFXRectangleToggleNode offButton2,
      MFXRectangleToggleNode offButton3,
      MFXRectangleToggleNode offButton4) {
    offButton1.setSelected(false);
    offButton2.setSelected(false);
    offButton3.setSelected(false);
    offButton4.setSelected(false);
  }

  // getDate() returns the date. idk what the date function looked like as a string i was curious
  public LocalDateTime getDate() {
    return deliveryDate.getValue().atStartOfDay();
  }

  // setItemToRadios() checks the status of all of the buttons and sets the item in the current
  // order to the equivalent item
  public void setItemToRadios() {
    if (radioButton1.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(0);
    if (radioButton2.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(1);
    if (radioButton3.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(2);
    if (radioButton4.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(3);
    if (radioButton5.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(4);
    if (radioButton6.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(5);
  }

  public void setServiceTypeToRadios() {
    if (serviceradioButton1.isSelected())
      currentFurnitureOrder.serviceType = ServiceType.replacement;
    if (serviceradioButton2.isSelected()) currentFurnitureOrder.serviceType = ServiceType.cleaning;
    if (serviceradioButton3.isSelected()) currentFurnitureOrder.serviceType = ServiceType.delivery;
    if (serviceradioButton4.isSelected())
      currentFurnitureOrder.serviceType = ServiceType.maintenance;
    if (serviceradioButton5.isSelected()) currentFurnitureOrder.serviceType = ServiceType.removal;
  }

  // submit() creates the final currentFurnitureOrder and uses its fields to send data to the
  // furnitureorder table
  void submit() {
    setToBlue();
    clearError();
    setServiceTypeToRadios();
    setItemToRadios();

    if (currentFurnitureOrder.serviceType == null) {
      submissionError("You must choose a type of service.", serviceScroll);
      return;
    }

    if (currentFurnitureOrder.furnitureItem == null) {
      submissionError("You must choose a furniture item.", furnitureScroll);
      return;
    }

    if (roomSelector.getValue() == null) {
      submissionError("You must choose a room.", roomSelector);
      return;
    }

    if (deliveryDate.getValue() == null) {
      submissionError("You must choose a delivery date.", deliveryDate);
      return;
    }

    currentFurnitureOrder.setRoomNum(roomSelector.getValue());
    currentFurnitureOrder.addNotes(notesTextField.getText());
    currentFurnitureOrder.addDate(getDate());

    if (currentFurnitureOrder.deliveryDate.isBefore(LocalDateTime.now().minusDays(1))) {
      submissionError("Cannot set delivery date before current date", deliveryDate);
      return;
    }

    currentFurnitureOrder.setStatus(FormStatus.submitted);
    FurnitureRequest request =
        new FurnitureRequest(
            "",
            FormStatus.submitted,
            currentFurnitureOrder.notes,
            currentFurnitureOrder.furnitureItem,
            currentFurnitureOrder.serviceType,
            currentFurnitureOrder.roomNum,
            currentFurnitureOrder.deliveryDate);
    dbConnection.insertEntry(request);
    confirmBlur.setDisable(false);
    confirmBlur.setVisible(true);
    confirmBox.setDisable(false);
    confirmBox.setVisible(true);
    confirmPane.setVisible(true);
    confirmPane.setDisable(false);
  }

  private void submissionError(String error, Node node) {
    node.setStyle("-fx-border-color: red; -fx-border-radius: 5; -fx-border-width: 1");
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
}
