package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.Fdb;

public abstract class AbsController {

  static Fdb dbConnection;

  public AbsController() {

    dbConnection = new Fdb();
  }
}
