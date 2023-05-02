package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.SharedResources;
import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.ITComponent;
import edu.wpi.fishfolk.ui.ITPriority;
import edu.wpi.fishfolk.ui.ITRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

public class ITRequestController extends AbsController {
  @FXML
  MFXRectangleToggleNode comp0,
      comp1,
      comp2,
      comp3,
      comp4,
      comp5,
      comp6,
      comp7,
      comp8; // type of component IT needs to be done on
  @FXML TextArea issueText; // describing the IT issue in question
  @FXML
  MFXRectangleToggleNode priority0,
      priority1,
      priority2; // selector between LOW, MEDIUM, HIGH priority for request
  @FXML MFXFilterComboBox<String> roomPicker; // which room IT issue is in
  @FXML MFXTextField contactInfoText;
  @FXML
  MFXButton cancelButton,
      clearButton,
      submitButton; // cancel order, clear form fields, submit order
  @FXML HBox confirmBlur;
  @FXML HBox confirmBox;
  @FXML AnchorPane confirmPane;
  @FXML MFXButton okButton;

  ITRequest currentITRequest =
      new ITRequest(); // object used to store data from fields to put into database

  /**
   * initialize() runs preliminary loading and setting functions for relevant objects on the page
   */
  public void initialize() {
    loadRoomChoice();
    cancelButton.setOnMouseClicked(event -> Navigation.navigate(SharedResources.getHome()));
    clearButton.setOnMouseClicked(event -> clearAll());
    submitButton.setOnMouseClicked(event -> submit());
    okButton.setOnAction(event -> Navigation.navigate(SharedResources.getHome()));
  }

  /**
   * * loadRoomChoice() puts all of the long names of the hospital rooms into the room selector
   * MFXFilterComboBox.
   */
  private void loadRoomChoice() {
    roomPicker.getItems().addAll(dbConnection.getDestLongnames());
  }

  /**
   * clearAll() clears all of the fields on the current form. Since currentITRequest is filled in
   * submit(), this just sets the fields to default values.
   */
  private void clearAll() {
    comp0.setSelected(false);
    comp1.setSelected(false);
    comp2.setSelected(false);
    comp3.setSelected(false);
    comp4.setSelected(false);
    comp5.setSelected(false);
    comp6.setSelected(false);
    comp7.setSelected(false);
    comp8.setSelected(false);

    issueText.setText("");

    priority0.setSelected(false);
    priority1.setSelected(false);
    priority2.setSelected(false);

    roomPicker.setValue(null);

    contactInfoText.setText("");
  }

  /**
   * selectComp() checks the "IT Component having issues" MFXRectangleToggleNodes to see what was
   * selected. This is then filled into currentITRequest's component field to identify what IT needs
   * to be done on.
   */
  private void selectComp() {
    if (comp0.isSelected()) currentITRequest.setComponent(ITComponent.COMPUTER);
    if (comp1.isSelected()) currentITRequest.setComponent(ITComponent.KEYBOARD);
    if (comp2.isSelected()) currentITRequest.setComponent(ITComponent.MOUSE);
    if (comp3.isSelected()) currentITRequest.setComponent(ITComponent.XRAY);
    if (comp4.isSelected()) currentITRequest.setComponent(ITComponent.PRINTER);
    if (comp5.isSelected()) currentITRequest.setComponent(ITComponent.PHONE);
    if (comp6.isSelected()) currentITRequest.setComponent(ITComponent.HEADSET);
    if (comp7.isSelected()) currentITRequest.setComponent(ITComponent.APPLE);
    if (comp8.isSelected()) currentITRequest.setComponent(ITComponent.OUTOFIDEAS);
  }

  /**
   * selectPriority() checks the issue priority MFXRectangleToggleNodes to find the issue priority
   * This is then filled into currentITRequest's priority field to identify priority.
   */
  private void selectPriority() {
    if (priority0.isSelected()) currentITRequest.setPriority(ITPriority.LOW);
    if (priority1.isSelected()) currentITRequest.setPriority(ITPriority.MEDIUM);
    if (priority2.isSelected()) currentITRequest.setPriority(ITPriority.HIGH);
  }

  private void submit() {
    selectComp();
    selectPriority();
    currentITRequest.setRoomNum(roomPicker.getValue());
    currentITRequest.setIssueNotes(issueText.getText());
    currentITRequest.setContactInfo(contactInfoText.getText());

    if (currentITRequest.getComponent() == null) System.out.println("comp");
    if (currentITRequest.getPriority() == null) System.out.println("priority");
    if (roomPicker.getValue() == null) System.out.println("room");
    if (issueText.getText().equals("")) System.out.println("issues");

    if (currentITRequest.getComponent() == null
        || currentITRequest.getPriority() == null
        || roomPicker.getValue() == null
        || issueText.getText().equals("")) {
      PopOver error = new PopOver();
      Text errorText = new Text("Insufficient number of fields filled");
      errorText.setFont(new Font("Open Sans", 26));
      error.setContentNode(errorText);
      error.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
      error.show(submitButton);
      return;
    }

    currentITRequest.setFormStatus(FormStatus.submitted);

    edu.wpi.fishfolk.database.TableEntry.ITRequest tableRequest =
        new edu.wpi.fishfolk.database.TableEntry.ITRequest(
            "",
            FormStatus.submitted,
            currentITRequest.getIssueNotes(),
            currentITRequest.getComponent(),
            currentITRequest.getPriority(),
            currentITRequest.getRoomNum(),
            currentITRequest.getContactInfo());

    dbConnection.insertEntry(tableRequest);

    confirmBlur.setDisable(false);
    confirmBlur.setVisible(true);
    confirmBox.setDisable(false);
    confirmBox.setVisible(true);
    confirmPane.setVisible(true);
    confirmPane.setDisable(false);
  }
}
