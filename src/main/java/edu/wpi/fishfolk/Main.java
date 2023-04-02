package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.Fdb;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args);

    Fdb fdb = new Fdb(); // Create fdb object
    fdb.initialize(); // init internal attributes and connect to databases
    fdb.runTests(); // Test add, remove, update of nodes and edges
    fdb.startCLI(); // Start the command line interface for database interaction
  }
}
