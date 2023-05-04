package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.database.TableEntry.SupplyRequest;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.SupplyItem;
import edu.wpi.fishfolk.ui.SupplyOrder;
import io.github.palexdev.materialfx.controls.*;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class NewSupplyOrderController extends AbsController {

  private static String[] headersArray = {
    "id", "items", "link", "roomNum", "notes", "status", "assignee"
  };
  public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));

  SupplyOrder currentSupplyOrder = new SupplyOrder();
  @FXML MFXFilterComboBox<String> roomSelector;
  ArrayList<SupplyItem> supplyOptions;
  @FXML MFXButton cancelButton;
  @FXML MFXButton supplySubmitButton;
  @FXML MFXButton clearButton;
  // @FXML MFXCheckbox check1, check2, check3, check4, check5, check6, check7;
  @FXML
  MFXRectangleToggleNode rectangle1,
      rectangle2,
      rectangle3,
      rectangle4,
      rectangle5,
      rectangle6,
      rectangle7;
  @FXML MFXTextField linkTextField, notesTextField;
  @FXML HBox confirmBlur;
  @FXML HBox confirmBox;
  @FXML AnchorPane confirmPane;
  @FXML MFXButton okButton;
  @FXML Label errors;
  @FXML MFXScrollPane itemScroll;

  private static TranslateTransition thugShaker;

  public NewSupplyOrderController() {
    super();
  }

  @FXML
  public void initialize() {
    loadOptions();
    setToBlue();
    clearError();
    okButton.setOnAction(event -> Navigation.navigate(SharedResources.getHome()));
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
    supplySubmitButton.setOnMouseClicked(event -> submit());
    clearButton.setOnMouseClicked(event -> clearAllFields());
    loadRooms();
  }

  void loadOptions() {
    supplyOptions = new ArrayList<SupplyItem>();
    supplyOptions.add(SupplyItem.supply1);
    supplyOptions.add(SupplyItem.supply2);
    supplyOptions.add(SupplyItem.supply3);
    supplyOptions.add(SupplyItem.supply4);
    supplyOptions.add(SupplyItem.supply5);
    supplyOptions.add(SupplyItem.supply6);
    supplyOptions.add(SupplyItem.supply7);
    supplyOptions.add(SupplyItem.supply8);
  }

  void loadRooms() {
    roomSelector.getItems().addAll(dbConnection.getDestLongnames());
    // roomSelector.getItems().add("A room");
  }

  private void addToOrder(int supplyNum) {
    SupplyItem supply = supplyOptions.get(supplyNum);
    currentSupplyOrder.addSupply(supply);
  }

  // private void addTextFields() {}

  private void clearAllFields() {
    clearChecks();
    clearTextFields();
    setToBlue();
    clearError();
  }

  /** Sets all borders back to blue */
  public void setToBlue() {
    itemScroll.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    roomSelector.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    notesTextField.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
    linkTextField.setStyle("-fx-border-color: #012d5a; -fx-border-radius: 5; -fx-border-width: 1");
  }

  /** Clears the error field */
  private void clearError() {
    errors.setText("");
    errors.setVisible(false);
  }

  private void clearTextFields() {
    linkTextField.clear();
    notesTextField.clear();
    roomSelector.setText("");
  }

  private void clearChecks() {
    rectangle1.setSelected(false);
    rectangle2.setSelected(false);
    rectangle3.setSelected(false);
    rectangle4.setSelected(false);
    rectangle4.setSelected(false);
    rectangle5.setSelected(false);
    rectangle6.setSelected(false);
  }

  /**
   * Creates an error popup for the given values.
   *
   * @param error the error message you want to present.
   * @param node the area it will pop up next to.
   */
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

  private boolean submittable() {
    setToBlue();
    clearError();
    if ((rectangle1.isSelected()
            || rectangle2.isSelected()
            || rectangle3.isSelected()
            || rectangle4.isSelected()
            || rectangle5.isSelected()
            || rectangle6.isSelected()
            || rectangle7.isSelected())
        || !(currentSupplyOrder.link == "")) {
      if (roomSelector.getValue() != null) {
        return true;
      } else {
        submissionError("You must include a room.", roomSelector);
      }
    } else {
      submissionError("", itemScroll);
      submissionError("You must either choose at least one item or include a link.", linkTextField);
    }
    return false;
  }

  private void submit() {
    if (rectangle1.isSelected()) addToOrder(0);
    if (rectangle2.isSelected()) addToOrder(1);
    if (rectangle3.isSelected()) addToOrder(2);
    if (rectangle4.isSelected()) addToOrder(3);
    if (rectangle5.isSelected()) addToOrder(4);
    if (rectangle6.isSelected()) addToOrder(5);
    if (rectangle7.isSelected()) addToOrder(6);
    currentSupplyOrder.roomNum = roomSelector.getValue();
    currentSupplyOrder.link = linkTextField.getText();
    currentSupplyOrder.notes = notesTextField.getText();
    if (submittable()) {
      System.out.println(currentSupplyOrder.listItemsToString());
      currentSupplyOrder.setSubmitted();
      SupplyRequest request =
          new SupplyRequest(
              "",
              FormStatus.submitted,
              notesTextField.getText(),
              linkTextField.getText(),
              roomSelector.getValue(),
              currentSupplyOrder.supplies);
      dbConnection.insertEntry(request);
      confirmBlur.setDisable(false);
      confirmBlur.setVisible(true);
      confirmBox.setDisable(false);
      confirmBox.setVisible(true);
      confirmPane.setVisible(true);
      confirmPane.setDisable(false);
    }
  }
}
