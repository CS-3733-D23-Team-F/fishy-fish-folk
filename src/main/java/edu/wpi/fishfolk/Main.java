package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.*;
import edu.wpi.fishfolk.pathfinding.NodeType;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import javafx.geometry.Point2D;

public class Main {

  public static void main(String[] args) {

    Fapp.launch(Fapp.class, args); // run ui

    Fdb fdb = new Fdb();

    // Test NodeDAO
    if (false) {
      fdb.insertEntry(new Node(9997, new Point2D(100, 200), "carpet", "bingus"));
      fdb.insertEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));
      fdb.insertEntry(new Node(9999, new Point2D(500, 600), "uhh, dirt?", "bingus"));

      fdb.updateEntry(new Node(9998, new Point2D(777, 777), "tile", "bingus"));

      System.out.println("[Main]: " + fdb.getEntry(9998, TableEntryType.NODE));
      System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.NODE));

      fdb.undoChange(TableEntryType.NODE);
      fdb.undoChange(TableEntryType.NODE);

      fdb.updateEntry(new Node(9997, new Point2D(1999, 1999), "carpet", "bingus"));

      fdb.removeEntry(9998, TableEntryType.NODE);
    }

    // Test LocationDAO
    if (false) {
      fdb.insertEntry(new Location(".Loooong name", "Short name", NodeType.INFO));
      fdb.insertEntry(new Location(".Bingus Tower III", "Bingus", NodeType.DEPT));
      fdb.insertEntry(new Location(".Joseph Biden", "Joe", NodeType.INFO));

      fdb.updateEntry(new Location(".Bingus Tower III", "BINGUSSSSSSSS", NodeType.DEPT));

      System.out.println("[Main]: " + fdb.getEntry(".Bingus Tower III", TableEntryType.LOCATION));
      System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.LOCATION));

      fdb.undoChange(TableEntryType.LOCATION);
      fdb.undoChange(TableEntryType.LOCATION);

      fdb.updateEntry(new Location(".Loooong name", "This name is SHORT", NodeType.INFO));

      fdb.removeEntry(".Bingus Tower III", TableEntryType.LOCATION);

      fdb.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/", TableEntryType.NODE);
    }

    // Test FoodRequestDAO
    if (false) {
      fdb.insertEntry(
          new FoodRequest(
              1,
              "Person A",
              FormStatus.submitted,
              "lmao",
              1.11,
              "SL 116",
              LocalDateTime.now(),
              "Bingus"));
      fdb.insertEntry(
          new FoodRequest(
              2,
              "Person B",
              FormStatus.submitted,
              "lmao",
              2.22,
              "SL 117",
              LocalDateTime.now(),
              "Bingus"));
      fdb.insertEntry(
          new FoodRequest(
              3,
              "Person C",
              FormStatus.submitted,
              "lmao",
              3.33,
              "SL 118",
              LocalDateTime.now(),
              "Bingus"));

      fdb.updateEntry(
          new FoodRequest(
              2,
              "Person B",
              FormStatus.submitted,
              "lmao",
              99.99,
              "SL 117",
              LocalDateTime.now(),
              "Bingus"));

      fdb.undoChange(TableEntryType.FOOD_REQUEST);
      fdb.undoChange(TableEntryType.FOOD_REQUEST);

      fdb.updateEntry(
          new FoodRequest(
              1,
              "Person A",
              FormStatus.submitted,
              "LOLLLLLLLL",
              1.11,
              "SL 116",
              LocalDateTime.now(),
              "Bingus"));

      fdb.removeEntry(2, TableEntryType.FOOD_REQUEST);

      fdb.exportCSV("D:\\", TableEntryType.FOOD_REQUEST);
    }

    // Test SupplyRequestDAO
    if (false) {
      fdb.insertEntry(
          new SupplyRequest(
              LocalDateTime.of(2020, 2, 2, 2, 2),
              "Me",
              FormStatus.submitted,
              "I need sleep",
              null,
              "www.pizza.com",
              "20000"));
      fdb.insertEntry(
          new SupplyRequest(
              LocalDateTime.of(2021, 2, 2, 2, 2),
              "Me",
              FormStatus.submitted,
              "I need more sleep",
              null,
              "wpi.edu",
              "20000"));
      fdb.insertEntry(
          new SupplyRequest(
              LocalDateTime.of(2022, 2, 2, 2, 2),
              "Me",
              FormStatus.submitted,
              "Yup",
              null,
              "the internet",
              "20000"));

      fdb.updateEntry(
          new SupplyRequest(
              LocalDateTime.of(2021, 2, 2, 2, 2),
              "Me",
              FormStatus.submitted,
              "I need EVEN more sleep",
              null,
              "wpi.edu",
              "200009"));

      fdb.undoChange(TableEntryType.SUPPLY_REQUEST);
      fdb.undoChange(TableEntryType.SUPPLY_REQUEST);

      fdb.updateEntry(
          new SupplyRequest(
              LocalDateTime.of(2020, 2, 2, 2, 2),
              "Me",
              FormStatus.submitted,
              "I need food i guess (and sleep)",
              null,
              "www.pizza.com",
              "20000"));

      fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.SUPPLY_REQUEST);

      fdb.exportCSV("D:\\", TableEntryType.SUPPLY_REQUEST);
    }
  }
}
