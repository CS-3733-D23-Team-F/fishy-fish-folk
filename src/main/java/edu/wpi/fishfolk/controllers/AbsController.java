package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Fdb;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsController {

  static Fdb dbConnection;

  protected final ArrayList<String> floors = new ArrayList<>(List.of("L2", "L1", "1", "2", "3"));
  // BTM - Building for Transformative Medicine
  protected final ArrayList<String> buildings =
      new ArrayList<>(List.of("15 Francis", "45 Francis", "BTM", "Shapiro", "Tower"));

  public AbsController() {

    dbConnection = new Fdb();
  }
}
