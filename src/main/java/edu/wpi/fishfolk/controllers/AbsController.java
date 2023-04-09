package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.Fdb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.image.Image;

public abstract class AbsController {

  static Fdb dbConnection;

  protected final ArrayList<String> allFloors = new ArrayList<>(List.of("L2", "L1", "1", "2", "3"));

  // BTM - Building for Transformative Medicine
  protected final ArrayList<String> allBuildings =
      new ArrayList<>(List.of("15 Francis", "45 Francis", "BTM", "Shapiro", "Tower"));

  HashMap<String, String> mapImgURLs;
  HashMap<String, Image> images;

  public AbsController() {

    dbConnection = new Fdb();

    mapImgURLs = new HashMap<>();

    mapImgURLs.put("L1", "map/00_thelowerlevel1.png");
    mapImgURLs.put("L2", "map/00_thelowerlevel2.png");
    mapImgURLs.put("GG", "map/00_thegroundfloor.png");
    mapImgURLs.put("1", "map/01_thefirstfloor.png");
    mapImgURLs.put("2", "map/02_thesecondfloor.png");
    mapImgURLs.put("3", "map/03_thethirdfloor.png");

    images = new HashMap<>();

    for (String floor : mapImgURLs.keySet()) {
      images.put(floor, new Image(Fapp.class.getResourceAsStream(mapImgURLs.get(floor))));
    }
  }
}
