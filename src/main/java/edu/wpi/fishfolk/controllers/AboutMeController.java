package edu.wpi.fishfolk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutMeController {

  @FXML Label AboutM;

  @FXML
  public void initialize() {
    AboutM.setWrapText(true);
    AboutM.setText(
        "WPI Computer Science Department \n CS3733-D23 Software Engineering \n Prof. Wilson Wong \n Team Coach: Luke Deratzou\n Alder, Bernhardt - Assistant Lead \nAloise, Megan - Software Engineer\n Anderson, Charles - Documentation Analyst \n Byrne, Brendan - Software Engineer \n Espelien, Trajan - Lead Software Engineer \n Pantojas, Jonathan - Scrum Master \n Pham, Louis - Software Engineer \n Rooney, Sam - Product Owner \n Rua, Christian - Assistant Lead \n Smith, Max - Software Engineer \n Youtz, Tristin - Product Manager \n Thanks to Brigham and Women's Hospital!\n Thanks to Andrew Shinn for time and input!");
  }
}
