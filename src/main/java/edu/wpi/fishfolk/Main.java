package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.Fdb;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) {
    // Fapp.launch(Fapp.class, args);
    Fdb fdb = new Fdb();
    try {
      Connection db = fdb.connect("teamfdb", "teamf", "teamf60");
      db.setSchema("test");
      System.out.println("Current schema: " + db.getSchema());
      fdb.disconnect(db);
      db.setSchema("test");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Welcome to FFF Database CLI, type 'help' for more information on how to use this program\n");
  }


  // shortcut: psvm

}
