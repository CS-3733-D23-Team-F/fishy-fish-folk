package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.Fdb;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.image.Image;

public abstract class AbsController {

  protected static Fdb dbConnection;

  protected static final ArrayList<String> allFloors =
      new ArrayList<>(List.of("L2", "L1", "1", "2", "3"));

  // BTM - Building for Transformative Medicine
  protected final ArrayList<String> allBuildings =
      new ArrayList<>(List.of("15 Francis", "45 Francis", "BTM", "Shapiro", "Tower"));

  HashMap<String, String> mapImgURLs;
  HashMap<String, Image> images;

  public static LocalDate today = LocalDate.of(2023, Month.MAY, 1);

  public AbsController() {

    if (dbConnection == null) {
      dbConnection = new Fdb();
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
}
