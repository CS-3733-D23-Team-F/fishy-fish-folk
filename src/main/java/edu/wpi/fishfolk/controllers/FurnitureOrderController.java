package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Table;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.FurnitureItem;
import edu.wpi.fishfolk.ui.FurnitureOrder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class FurnitureOrderController extends AbsController {
  // definitions for furnitureorder table's headers
  private static String[] headersArray = {
    "id", "servicetype", "item", "roomnum", "date", "notes", "status", "assignee"
  };

  public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));

  @FXML ChoiceBox<String> requestTypePicker;
  @FXML
  MFXRadioButton radioButton1,
      radioButton2,
      radioButton3,
      radioButton4,
      radioButton5,
      radioButton6,
      radioButton7;
  @FXML ChoiceBox roomSelector;
  @FXML MFXDatePicker deliveryDate;
  @FXML MFXTextField notesTextField;
  @FXML MFXButton cancelButton;
  @FXML MFXButton furnitureSubmitButton;
  @FXML MFXButton clearButton;

  @FXML MFXButton pathfindingNav;
  @FXML MFXButton mapEditorNav;
  @FXML AnchorPane menuWrap;
  @FXML MFXButton signageNav;

  @FXML MFXButton mealNav;

  @FXML MFXButton officeNav;

  @FXML MFXButton sideBar;

  @FXML MFXButton exitButton;

  @FXML MFXButton sideBarClose;
  @FXML AnchorPane slider;
  @FXML MFXButton viewFood;
  @FXML MFXButton viewSupply;
  @FXML MFXButton homeButton;

  FurnitureOrder currentFurnitureOrder = new FurnitureOrder();
  ArrayList<FurnitureItem> furnitureOptions = new ArrayList<>();
  Table furnitureOrderTable;

  // pretty much entirely runs setup for furnitureorder table based on FurnitureOrder fields
  public FurnitureOrderController() {
    super();
    furnitureOrderTable = new Table(dbConnection.conn, "furnitureorder");
    furnitureOrderTable.init(true);
    furnitureOrderTable.addHeaders(
        FurnitureOrderController.headers,
        new ArrayList<>(
            List.of(
                "String", "String", "String", "String", "String", "String", "String", "String")));
  }

  // initialize() sets the preliminary fields for the page and defines the functionality of the
  // relevant items
  // ex. radioButton functionality, drop-down menus, etc.
  public void initialize() {
    loadOptions();
    loadServiceTypeChoice();
    loadRoomChoice();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    furnitureSubmitButton.setOnMouseClicked(event -> submit());
    // clearButton.setOnMouseClicked(event -> clearAllFields());
    signageNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    mealNav.setOnMouseClicked(event -> Navigation.navigate(Screen.FOOD_ORDER_REQUEST));
    officeNav.setOnMouseClicked(event -> Navigation.navigate(Screen.SUPPLIES_REQUEST));
    mapEditorNav.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDITOR));
    pathfindingNav.setOnMouseClicked(event -> Navigation.navigate(Screen.PATHFINDING));
    viewFood.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_FOOD_ORDERS));
    viewSupply.setOnMouseClicked(event -> Navigation.navigate(Screen.VIEW_SUPPLY_ORDERS));
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    radioButton1.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton2,
                radioButton3,
                radioButton4,
                radioButton5,
                radioButton6,
                radioButton7));
    radioButton2.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton3,
                radioButton4,
                radioButton5,
                radioButton6,
                radioButton7));
    radioButton3.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton2,
                radioButton4,
                radioButton5,
                radioButton6,
                radioButton7));
    radioButton4.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton2,
                radioButton3,
                radioButton5,
                radioButton6,
                radioButton7));
    radioButton5.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton2,
                radioButton3,
                radioButton4,
                radioButton6,
                radioButton7));
    radioButton6.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton2,
                radioButton3,
                radioButton4,
                radioButton5,
                radioButton7));
    radioButton7.setOnMouseClicked(
        event ->
            deselectRadios(
                radioButton1,
                radioButton2,
                radioButton3,
                radioButton4,
                radioButton5,
                radioButton6));

    slider.setTranslateX(-400);
    sideBarClose.setVisible(false);
    menuWrap.setVisible(false);
    sideBar.setOnMouseClicked(
        event -> {
          menuWrap.setDisable(false);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);

          slide.setToX(400);
          slide.play();

          slider.setTranslateX(-400);
          menuWrap.setVisible(true);
          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(false);
                sideBarClose.setVisible(true);
              });
        });

    sideBarClose.setOnMouseClicked(
        event -> {
          menuWrap.setVisible(false);
          menuWrap.setDisable(true);
          TranslateTransition slide = new TranslateTransition();
          slide.setDuration(Duration.seconds(0.4));
          slide.setNode(slider);
          slide.setToX(-400);
          slide.play();

          slider.setTranslateX(0);

          slide.setOnFinished(
              (ActionEvent e) -> {
                sideBar.setVisible(true);
                sideBarClose.setVisible(false);
              });
        });
  }

  // loadOptions() fills furnitureOptions list with possible furniture items to select
  public void loadOptions() {
    furnitureOptions.add(FurnitureItem.bed);
    furnitureOptions.add(FurnitureItem.chair);
    furnitureOptions.add(FurnitureItem.desk);
    furnitureOptions.add(FurnitureItem.fileCabinet);
    furnitureOptions.add(FurnitureItem.clock);
    furnitureOptions.add(FurnitureItem.xRay);
    furnitureOptions.add(FurnitureItem.trashCan);
  }

  // loadServiceType() fills the possible options in the Service Type choicebox
  public void loadServiceTypeChoice() {
    requestTypePicker.getItems().add("Furniture Replacement");
    requestTypePicker.getItems().add("Furniture Cleaning");
    requestTypePicker.getItems().add("Furniture Delivery");
    requestTypePicker.getItems().add("Furniture Maintenance");
    requestTypePicker.getItems().add("Furniture Removal");
  }

  // loadRoomChoice() fills the possible options in the Room Numver choicebox
  public void loadRoomChoice() {
    // roomSelector.getItems().addAll(dbConnection.nodeTable.getDestLongNames());
  }

  // deselectRadios() will deselect all of the given buttons.
  // This will be used to make sure only one button can be pressed at a time on the form
  public void deselectRadios(
      MFXRadioButton offButton1,
      MFXRadioButton offButton2,
      MFXRadioButton offButton3,
      MFXRadioButton offButton4,
      MFXRadioButton offButton5,
      MFXRadioButton offButton6) {
    offButton1.setSelected(false);
    offButton2.setSelected(false);
    offButton3.setSelected(false);
    offButton4.setSelected(false);
    offButton5.setSelected(false);
    offButton6.setSelected(false);
  }

  // getDate() returns the date. idk what the date function looked like as a string i was curious
  // lol
  public String getDate() {
    return "" + deliveryDate.getCurrentDate();
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
    if (radioButton7.isSelected()) currentFurnitureOrder.furnitureItem = furnitureOptions.get(6);
  }

  // submit() creates the final currentFurnitureOrder and uses its fields to send data to the
  // furnitureorder table
  void submit() {
    currentFurnitureOrder.setServiceType("" + requestTypePicker.getValue());
    if (radioButton1.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(0));
    if (radioButton2.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(1));
    if (radioButton3.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(2));
    if (radioButton4.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(3));
    if (radioButton5.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(4));
    if (radioButton6.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(5));
    if (radioButton7.isSelected()) currentFurnitureOrder.addFurniture(furnitureOptions.get(6));
    currentFurnitureOrder.setRoomNum("" + roomSelector.getValue());
    currentFurnitureOrder.addNotes(notesTextField.getText());
    currentFurnitureOrder.addDate(getDate());
    currentFurnitureOrder.setStatus(FormStatus.submitted);
    furnitureOrderTable.insert(currentFurnitureOrder);
    Navigation.navigate(Screen.HOME);
  }
}
