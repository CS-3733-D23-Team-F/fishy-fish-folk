package edu.wpi.fishfolk.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;

public class FurnitureOrderController extends AbsController {
    private static String[] headersArray = {
            "id", "serviceType", "item", "roomNum", "date", "notes", "status", "assignee"
    };

    public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));

    @FXML ChoiceBox requestTypePicker;
    @FXML MFXRadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5, radioButton6, radioButton7;
    @FXML ChoiceBox roomSelector;
    @FXML MFXDatePicker deliveryDate;
    @FXML MFXTextField notesTextField;
    @FXML
    MFXButton cancelButton;
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

}
