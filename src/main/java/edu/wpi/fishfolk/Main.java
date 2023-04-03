package edu.wpi.fishfolk;


import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.pathfinding.*;

public class Main {

  public static void main(String[] args) {

    Fapp.launch(Fapp.class, args); // run ui

    // Fdb fdb = new Fdb(); // Create fdb object
    // fdb.initialize(); // init internal attributes and connect to databases
    // fdb.runTests(); // Test add, remove, update of nodes and edges
  }
}
