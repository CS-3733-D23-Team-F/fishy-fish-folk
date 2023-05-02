package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.DBSource;
import edu.wpi.fishfolk.database.Fdb;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public abstract class AbsController {

  protected static Fdb dbConnection;

  protected static final ArrayList<String> allFloors =
      new ArrayList<>(List.of("L2", "L1", "1", "2", "3"));

  // BTM - Building for Transformative Medicine
  protected final ArrayList<String> allBuildings =
      new ArrayList<>(List.of("15 Francis", "45 Francis", "BTM", "Shapiro", "Tower"));

  HashMap<String, String> mapImgURLs;
  HashMap<String, Image> images;

  public static final LocalDate today = LocalDate.of(2023, Month.JUNE, 1);

  private FileChooser fileChooser = new FileChooser();
  private DirectoryChooser dirChooser = new DirectoryChooser();

  public AbsController() {

    if (dbConnection == null) {
      dbConnection = new Fdb(DBSource.DB_AWS);
    }

    mapImgURLs = new HashMap<>();

    mapImgURLs.put("L1", "map/clean/00_thelowerlevel1.png");
    mapImgURLs.put("L2", "map/clean/00_thelowerlevel2.png");
    // mapImgURLs.put("GG", "map/clean/00_thegroundfloor.png");
    mapImgURLs.put("1", "map/clean/01_thefirstfloor.png");
    mapImgURLs.put("2", "map/clean/02_thesecondfloor.png");
    mapImgURLs.put("3", "map/clean/03_thethirdfloor.png");

    images = new HashMap<>();

    for (String floor : mapImgURLs.keySet()) {
      images.put(floor, new Image(Fapp.class.getResourceAsStream(mapImgURLs.get(floor))));
    }

    // dbConnection = new Fdb();
  }

  /**
   * Opens dialog to choose a file from the explorer. Handles any Exceptions.
   *
   * @return The absolute path of the chosen file, empty string on error
   */
  public String fileChooserPrompt() {
    try {
      return fileChooser.showOpenDialog(Fapp.getPrimaryStage()).getAbsolutePath();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return "";
    }
  }

  /**
   * Opens dialog to choose a directory from the explorer. Handles any Exceptions.
   *
   * @return The absolute path of the chosen directory, empty string on error
   */
  public String dirChooserPrompt() {
    try {
      return dirChooser.showDialog(Fapp.getPrimaryStage()).getAbsolutePath();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return "";
    }
  }
}
