package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.navigation.Navigation;
import edu.wpi.fishfolk.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class NewConferenceController extends AbsController {
    @FXML MFXButton confClearButton;
    @FXML MFXButton confCancelButton;
    @FXML MFXButton confSubmitButton;

    public NewConferenceController(){
        super();
    }

    @FXML public void init(){
        confCancelButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    }

}
