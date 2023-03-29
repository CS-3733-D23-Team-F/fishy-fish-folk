package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.DbIOCommands;
import edu.wpi.fishfolk.database.Fdb;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args);

    Fdb fdb = new Fdb(); //create fdb object and connect to db
    fdb.tests(); //test add, remove, update of nodes and edges

    DbIOCommands cli = new DbIOCommands();
    cli.setNt(fdb.nodeTable);
    cli.setEt(fdb.edgeTable);
    cli.cycleCLI();

    fdb.disconnect();
  }
}
