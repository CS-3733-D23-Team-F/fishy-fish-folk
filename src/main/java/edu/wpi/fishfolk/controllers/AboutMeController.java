package edu.wpi.fishfolk.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AboutMeController {

  @FXML Label AboutM;
  @FXML Button Jon;
  @FXML Button Meg;
  @FXML Button Sam;
  @FXML Button Christian;
  @FXML Button Max;
  @FXML Button Charlie;
  @FXML Button Louis;
  @FXML Button Tristin;
  @FXML Button Trajan;
  @FXML Button Bernhardt;
  @FXML Button Brendan;

  @FXML
  public void initialize() {

    // none of this works
    FXMLLoader JonPage = new FXMLLoader(getClass().getResource("JonPage.fxml"));
    FXMLLoader MaxPage = new FXMLLoader(getClass().getResource("MaxPage.fxml"));
    FXMLLoader MegPage = new FXMLLoader(getClass().getResource("MegPage.fxml"));
    FXMLLoader TrajanPage = new FXMLLoader(getClass().getResource("TrajanPage.fxml"));
    FXMLLoader TristinPage = new FXMLLoader(getClass().getResource("TristinPage.fxml"));
    FXMLLoader BernhardtPage = new FXMLLoader(getClass().getResource("BernhardtPage.fxml"));
    FXMLLoader LouisPage = new FXMLLoader(getClass().getResource("LouisPage.fxml"));
    FXMLLoader CharliePage = new FXMLLoader(getClass().getResource("CharliePage.fxml"));
    FXMLLoader SamPage = new FXMLLoader(getClass().getResource("SamPage.fxml"));
    FXMLLoader ChristianPage = new FXMLLoader(getClass().getResource("ChristianPage.fxml"));
    FXMLLoader BrendanPage = new FXMLLoader(getClass().getResource("BrendanPage.fxml"));
    Jon.setOnMouseClicked(event -> PopPage(JonPage));
    Meg.setOnMouseClicked(event -> PopPage(MegPage));
    Bernhardt.setOnMouseClicked(event -> PopPage(BernhardtPage));
    Trajan.setOnMouseClicked(event -> PopPage(TrajanPage));
    Tristin.setOnMouseClicked(event -> PopPage(TristinPage));
    Louis.setOnMouseClicked(event -> PopPage(LouisPage));
    Max.setOnMouseClicked(event -> PopPage(MaxPage));
    Charlie.setOnMouseClicked(event -> PopPage(CharliePage));
    Sam.setOnMouseClicked(event -> PopPage(SamPage));
    Christian.setOnMouseClicked(event -> PopPage(ChristianPage));
    Brendan.setOnMouseClicked(event -> PopPage(BrendanPage));
  }

  public void PopPage(FXMLLoader name) {
    try {
      name.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
