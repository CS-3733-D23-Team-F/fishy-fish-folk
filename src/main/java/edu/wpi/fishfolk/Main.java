package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.*;
import edu.wpi.fishfolk.ui.FoodItem;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {

    Fapp.launch(Fapp.class, args); // run ui

    // Fdb fdb = new Fdb();

    // Test NodeDAO
    //        if (false) {
    //          fdb.insertEntry(new Node(9997, new Point2D(100, 200), "carpet", "bingus"));
    //          fdb.insertEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));
    //          fdb.insertEntry(new Node(9999, new Point2D(500, 600), "uhh, dirt?", "bingus"));
    //
    //          fdb.updateEntry(new Node(9998, new Point2D(777, 777), "tile", "bingus"));
    //
    //          System.out.println("[Main]: " + fdb.getEntry(9998, TableEntryType.NODE));
    //          System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.NODE));
    //
    //          fdb.undoChange(TableEntryType.NODE);
    //          fdb.undoChange(TableEntryType.NODE);
    //
    //          fdb.updateEntry(new Node(9997, new Point2D(1999, 1999), "carpet", "bingus"));
    //
    //          fdb.removeEntry(9998, TableEntryType.NODE);
    //        }
    //
    //        // Test LocationDAO
    //        if (false) {
    //          fdb.insertEntry(new Location(".Loooong name", "Short name", NodeType.INFO));
    //          fdb.insertEntry(new Location(".Bingus Tower III", "Bingus", NodeType.DEPT));
    //          fdb.insertEntry(new Location(".Joseph Biden", "Joe", NodeType.INFO));
    //
    //          fdb.updateEntry(new Location(".Bingus Tower III", "BINGUSSSSSSSS", NodeType.DEPT));
    //
    //          System.out.println("[Main]: " + fdb.getEntry(".Bingus Tower III",
    //     TableEntryType.LOCATION));
    //          System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.LOCATION));
    //
    //          fdb.undoChange(TableEntryType.LOCATION);
    //          fdb.undoChange(TableEntryType.LOCATION);
    //
    //          fdb.updateEntry(new Location(".Loooong name", "This name is SHORT", NodeType.INFO));
    //
    //          fdb.removeEntry(".Bingus Tower III", TableEntryType.LOCATION);
    //
    //          fdb.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/", TableEntryType.NODE);
    //        }
    //
    // Test FoodRequestDAO
//    if (false) {
//
//      ArrayList<FoodItem> fList1 = new ArrayList<>();
//      fList1.add(new FoodItem("TEST 1", 3.0F, null));
//
//      ArrayList<FoodItem> fList2 = new ArrayList<>();
//      fList2.add(new FoodItem("TEST 2", 3.0F, null));
//
//      ArrayList<FoodItem> fList3 = new ArrayList<>();
//      fList3.add(new FoodItem("TEST 3", 3.0F, null));
//
//      fdb.insertEntry(
//          new FoodRequest(
//              LocalDateTime.of(2020, 2, 2, 2, 2),
//              "Person A",
//              FormStatus.submitted,
//              "lmao",
//              1.11,
//              "SL 116",
//              LocalDateTime.now(),
//              "Bingus",
//              fList1));
//      fdb.insertEntry(
//          new FoodRequest(
//              LocalDateTime.of(2021, 2, 2, 2, 2),
//              "Person B",
//              FormStatus.submitted,
//              "lmao",
//              2.22,
//              "SL 117",
//              LocalDateTime.now(),
//              "Bingus",
//              fList2));
//      fdb.insertEntry(
//          new FoodRequest(
//              LocalDateTime.of(2022, 2, 2, 2, 2),
//              "Person C",
//              FormStatus.submitted,
//              "lmao",
//              3.33,
//              "SL 118",
//              LocalDateTime.now(),
//              "Bingus",
//              fList3));
//
//      fdb.updateEntry(
//          new FoodRequest(
//              LocalDateTime.of(2021, 2, 2, 2, 2),
//              "Person B",
//              FormStatus.submitted,
//              "lmao",
//              99.99,
//              "SL 117",
//              LocalDateTime.now(),
//              "Bingus",
//              fList2));
//
//      fdb.undoChange(TableEntryType.FOOD_REQUEST);
//      fdb.undoChange(TableEntryType.FOOD_REQUEST);
//
//      fdb.updateEntry(
//          new FoodRequest(
//              LocalDateTime.of(2020, 2, 2, 2, 2),
//              "Person A",
//              FormStatus.submitted,
//              "LOLLLLLLLL",
//              1.11,
//              "SL 116",
//              LocalDateTime.now(),
//              "Bingus",
//              fList1));
//
//      fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.FOOD_REQUEST);
//
//      fdb.exportCSV("D:\\", TableEntryType.FOOD_REQUEST);
//    }
    //
    //        // Test SupplyRequestDAO
    //        if (false) {
    //          fdb.insertEntry(
    //              new SupplyRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Me",
    //                  FormStatus.submitted,
    //                  "I need sleep",
    //                  null,
    //                  "www.pizza.com",
    //                  "20000"));
    //          fdb.insertEntry(
    //              new SupplyRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "Me",
    //                  FormStatus.submitted,
    //                  "I need more sleep",
    //                  null,
    //                  "wpi.edu",
    //                  "20000"));
    //          fdb.insertEntry(
    //              new SupplyRequest(
    //                  LocalDateTime.of(2022, 2, 2, 2, 2),
    //                  "Me",
    //                  FormStatus.submitted,
    //                  "Yup",
    //                  null,
    //                  "the internet",
    //                  "20000"));
    //
    //          fdb.updateEntry(
    //              new SupplyRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "Me",
    //                  FormStatus.submitted,
    //                  "I need EVEN more sleep",
    //                  null,
    //                  "wpi.edu",
    //                  "200009"));
    //
    //          fdb.undoChange(TableEntryType.SUPPLY_REQUEST);
    //          fdb.undoChange(TableEntryType.SUPPLY_REQUEST);
    //
    //          fdb.updateEntry(
    //              new SupplyRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Me",
    //                  FormStatus.submitted,
    //                  "I need food i guess (and sleep)",
    //                  null,
    //                  "www.pizza.com",
    //                  "20000"));
    //
    //          fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.SUPPLY_REQUEST);
    //
    //          fdb.exportCSV("D:\\", TableEntryType.SUPPLY_REQUEST);
    //        }
    //
    //        // Test FurnitureRequestDAO
    //        if (false) {
    //          fdb.insertEntry(
    //              new FurnitureRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Wilson Wong",
    //                  FormStatus.submitted,
    //                  "These sure are notes",
    //                  "Couch",
    //                  "Clean",
    //                  "OH 106",
    //                  LocalDateTime.of(1500, 2, 2, 2, 2)));
    //          fdb.insertEntry(
    //              new FurnitureRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "Wilson Wong",
    //                  FormStatus.submitted,
    //                  "LETTERS",
    //                  "Couch",
    //                  "Clean",
    //                  "OH 106",
    //                  LocalDateTime.of(1500, 2, 2, 2, 2)));
    //          fdb.insertEntry(
    //              new FurnitureRequest(
    //                  LocalDateTime.of(2022, 2, 2, 2, 2),
    //                  "Wilson Wong",
    //                  FormStatus.submitted,
    //                  "numbers??",
    //                  "Couch",
    //                  "Clean",
    //                  "OH 106",
    //                  LocalDateTime.of(1500, 2, 2, 2, 2)));
    //
    //          fdb.updateEntry(
    //              new FurnitureRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "ME LMAO",
    //                  FormStatus.submitted,
    //                  "LETTERS",
    //                  "Couch",
    //                  "Clean",
    //                  "OH 106",
    //                  LocalDateTime.of(1500, 2, 2, 2, 2)));
    //
    //          fdb.undoChange(TableEntryType.FURNITURE_REQUEST);
    //          fdb.undoChange(TableEntryType.FURNITURE_REQUEST);
    //
    //          fdb.updateEntry(
    //              new FurnitureRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Wilson Wong",
    //                  FormStatus.submitted,
    //                  "These AREN'T notes",
    //                  "Couch",
    //                  "Clean",
    //                  "OH 106",
    //                  LocalDateTime.of(1500, 2, 2, 2, 2)));
    //
    //          fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2),
    // TableEntryType.FURNITURE_REQUEST);
    //
    //          fdb.exportCSV("D:\\", TableEntryType.FURNITURE_REQUEST);
    //        }
    //
    //        // Test FlowerRequestDAO
    //        if (false) {
    //          fdb.insertEntry(
    //              new FlowerRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Trajan",
    //                  FormStatus.submitted,
    //                  "No changes made",
    //                  null,
    //                  null,
    //                  "ur mom",
    //                  2.00));
    //          fdb.insertEntry(
    //              new FlowerRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "Jon",
    //                  FormStatus.submitted,
    //                  "No changes made",
    //                  null,
    //                  null,
    //                  "ur mom",
    //                  2.00));
    //          fdb.insertEntry(
    //              new FlowerRequest(
    //                  LocalDateTime.of(2022, 2, 2, 2, 2),
    //                  "Charlie",
    //                  FormStatus.submitted,
    //                  "No changes made",
    //                  null,
    //                  null,
    //                  "ur mom",
    //                  2.00));
    //
    //          fdb.updateEntry(
    //              new FlowerRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2),
    //                  "Jon",
    //                  FormStatus.submitted,
    //                  "Changed one word lol",
    //                  null,
    //                  null,
    //                  "ur mom",
    //                  2.00));
    //
    //          fdb.undoChange(TableEntryType.FLOWER_REQUEST);
    //          fdb.undoChange(TableEntryType.FLOWER_REQUEST);
    //
    //          fdb.updateEntry(
    //              new FlowerRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Trajan",
    //                  FormStatus.submitted,
    //                  "Uhhhhhhhh notes",
    //                  null,
    //                  null,
    //                  "ur mom",
    //                  2.00));
    //
    //          fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.FLOWER_REQUEST);
    //
    //          fdb.exportCSV("D:\\", TableEntryType.FLOWER_REQUEST);
    //        }
    //
    //        // Test ConferenceRequestDAO
    //        if (false) {
    //          fdb.insertEntry(
    //              new ConferenceRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2), "Nobody", FormStatus.submitted, "Yep"));
    //          fdb.insertEntry(
    //              new ConferenceRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2), "Somebody?", FormStatus.submitted,
    // "Yep"));
    //          fdb.insertEntry(
    //              new ConferenceRequest(
    //                  LocalDateTime.of(2022, 2, 2, 2, 2), "Secret person", FormStatus.submitted,
    //     "Yep"));
    //
    //          fdb.updateEntry(
    //              new ConferenceRequest(
    //                  LocalDateTime.of(2021, 2, 2, 2, 2), "Somebody?", FormStatus.submitted,
    //     "CHANGE"));
    //
    //          fdb.undoChange(TableEntryType.CONFERENCE_REQUEST);
    //          fdb.undoChange(TableEntryType.CONFERENCE_REQUEST);
    //
    //          fdb.updateEntry(
    //              new ConferenceRequest(
    //                  LocalDateTime.of(2020, 2, 2, 2, 2),
    //                  "Nobody",
    //                  FormStatus.submitted,
    //                  "ANOTHER CHANGE"));
    //
    //          fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2),
    // TableEntryType.CONFERENCE_REQUEST);
    //
    //          fdb.exportCSV("D:\\", TableEntryType.CONFERENCE_REQUEST);
    //        }

    /*
     * Test format:
     * INSERT A
     * INSERT B
     * INSERT C
     * UPDATE B
     * UNDO
     * UNDO
     * UPDATE A
     * REMOVE B
     * EXPORT CSV
     */
  }
}
