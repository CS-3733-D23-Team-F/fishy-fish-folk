package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.ui.*;
import edu.wpi.fishfolk.util.NodeType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

public class DAOTests {

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

  @Test
  public void nodeDAOTests() {

    Fdb fdb = new Fdb();

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

  @Test
  public void locationDAOTests() {

    Fdb fdb = new Fdb();

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

    fdb.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/", TableEntryType.LOCATION);
  }

  @Test
  public void foodRequestDAOTests() {

    Fdb fdb = new Fdb();

    ArrayList<NewFoodItem> fList1 = new ArrayList<>(List.of(new NewFoodItem("TEST 1", 3)));
    ArrayList<NewFoodItem> fList2 = new ArrayList<>(List.of(new NewFoodItem("TEST 2", 3)));
    ArrayList<NewFoodItem> fList3 = new ArrayList<>(List.of(new NewFoodItem("TEST 3", 3)));

    fdb.insertEntry(
        new FoodRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Person A",
            FormStatus.submitted,
            "lmao",
            1.11,
            "SL 116",
            LocalDateTime.now(),
            "Bingus",
            fList1));
    fdb.insertEntry(
        new FoodRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Person B",
            FormStatus.submitted,
            "lmao",
            2.22,
            "SL 117",
            LocalDateTime.now(),
            "Bingus",
            fList2));
    fdb.insertEntry(
        new FoodRequest(
            LocalDateTime.of(2022, 2, 2, 2, 2),
            "Person C",
            FormStatus.submitted,
            "lmao",
            3.33,
            "SL 118",
            LocalDateTime.now(),
            "Bingus",
            fList3));

    fdb.updateEntry(
        new FoodRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Person B",
            FormStatus.submitted,
            "lmao",
            99.99,
            "SL 117",
            LocalDateTime.now(),
            "Bingus",
            fList2));

    fdb.undoChange(TableEntryType.FOOD_REQUEST);
    fdb.undoChange(TableEntryType.FOOD_REQUEST);

    fdb.updateEntry(
        new FoodRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Person A",
            FormStatus.submitted,
            "LOLLLLLLLL",
            1.11,
            "SL 116",
            LocalDateTime.now(),
            "Bingus",
            fList1));

    fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.FOOD_REQUEST);

    fdb.exportCSV("D:\\", TableEntryType.FOOD_REQUEST);
  }

  @Test
  public void supplyRequestDAOTests() {

    Fdb fdb = new Fdb();

    ArrayList<SupplyItem> s1 = new ArrayList<>(List.of(new SupplyItem("bingus 1", 1)));
    ArrayList<SupplyItem> s2 = new ArrayList<>(List.of(new SupplyItem("bingus 2", 2)));
    ArrayList<SupplyItem> s3 = new ArrayList<>(List.of(new SupplyItem("bingus 3", 3)));

    fdb.insertEntry(
        new SupplyRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Me",
            FormStatus.submitted,
            "I need sleep",
            "www.pizza.com",
            "20000",
            s1));
    fdb.insertEntry(
        new SupplyRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Me",
            FormStatus.submitted,
            "I need more sleep",
            "wpi.edu",
            "20000",
            s2));
    fdb.insertEntry(
        new SupplyRequest(
            LocalDateTime.of(2022, 2, 2, 2, 2),
            "Me",
            FormStatus.submitted,
            "Yup",
            "the internet",
            "20000",
            s3));

    fdb.updateEntry(
        new SupplyRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Me",
            FormStatus.submitted,
            "I need EVEN more sleep",
            "wpi.edu",
            "200009",
            s2));

    fdb.undoChange(TableEntryType.SUPPLY_REQUEST);
    fdb.undoChange(TableEntryType.SUPPLY_REQUEST);

    fdb.updateEntry(
        new SupplyRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Me",
            FormStatus.submitted,
            "I need food i guess (and sleep)",
            "www.pizza.com",
            "20000",
            s1));

    fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.SUPPLY_REQUEST);

    fdb.exportCSV("D:\\", TableEntryType.SUPPLY_REQUEST);
  }

  @Test
  public void furnitureRequestDAOTests() {

    Fdb fdb = new Fdb();

    fdb.insertEntry(
        new FurnitureRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Wilson Wong",
            FormStatus.submitted,
            "These sure are notes",
            new FurnitureItem("couch"),
            ServiceType.cleaning,
            "OH 106",
            LocalDateTime.of(1500, 2, 2, 2, 2)));
    fdb.insertEntry(
        new FurnitureRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Wilson Wong",
            FormStatus.submitted,
            "LETTERS",
            new FurnitureItem("couch"),
            ServiceType.cleaning,
            "OH 106",
            LocalDateTime.of(1500, 2, 2, 2, 2)));
    fdb.insertEntry(
        new FurnitureRequest(
            LocalDateTime.of(2022, 2, 2, 2, 2),
            "Wilson Wong",
            FormStatus.submitted,
            "numbers??",
            new FurnitureItem("couch"),
            ServiceType.cleaning,
            "OH 106",
            LocalDateTime.of(1500, 2, 2, 2, 2)));

    fdb.updateEntry(
        new FurnitureRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "ME LMAO",
            FormStatus.submitted,
            "LETTERS",
            new FurnitureItem("couch"),
            ServiceType.cleaning,
            "OH 106",
            LocalDateTime.of(1500, 2, 2, 2, 2)));

    fdb.undoChange(TableEntryType.FURNITURE_REQUEST);
    fdb.undoChange(TableEntryType.FURNITURE_REQUEST);

    fdb.updateEntry(
        new FurnitureRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Wilson Wong",
            FormStatus.submitted,
            "These AREN'T notes",
            new FurnitureItem("couch"),
            ServiceType.cleaning,
            "OH 106",
            LocalDateTime.of(1500, 2, 2, 2, 2)));

    fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.FURNITURE_REQUEST);

    fdb.exportCSV("D:\\", TableEntryType.FURNITURE_REQUEST);
  }

  @Test
  public void flowerRequestDAOTests() {

    Fdb fdb = new Fdb();

    ArrayList<FlowerItem> items1 = new ArrayList<>(List.of(new FlowerItem("red", 1, 5)));
    ArrayList<FlowerItem> items2 = new ArrayList<>(List.of(new FlowerItem("red", 2, 5)));
    ArrayList<FlowerItem> items3 = new ArrayList<>(List.of(new FlowerItem("red", 3, 5)));

    fdb.insertEntry(
        new FlowerRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Trajan",
            FormStatus.submitted,
            "No changes made",
            "null",
            "null",
            LocalDateTime.of(1500, 2, 2, 2, 2),
            2.00,
            items1));
    fdb.insertEntry(
        new FlowerRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Jon",
            FormStatus.submitted,
            "No changes made",
            "null",
            "null",
            LocalDateTime.of(1500, 2, 2, 2, 2),
            2.00,
            items2));
    fdb.insertEntry(
        new FlowerRequest(
            LocalDateTime.of(2022, 2, 2, 2, 2),
            "Charlie",
            FormStatus.submitted,
            "No changes made",
            "null",
            "null",
            LocalDateTime.of(1500, 2, 2, 2, 2),
            2.00,
            items3));

    fdb.updateEntry(
        new FlowerRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Jon",
            FormStatus.submitted,
            "Changed one word lol",
            "null",
            "null",
            LocalDateTime.of(1500, 2, 2, 2, 2),
            2.00,
            items2));

    fdb.undoChange(TableEntryType.FLOWER_REQUEST);
    fdb.undoChange(TableEntryType.FLOWER_REQUEST);

    fdb.updateEntry(
        new FlowerRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Trajan",
            FormStatus.submitted,
            "Uhhhhhhhh notes",
            "null",
            "null",
            LocalDateTime.of(1500, 2, 2, 2, 2),
            2.00,
            items1));

    fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.FLOWER_REQUEST);

    fdb.exportCSV("D:\\", TableEntryType.FLOWER_REQUEST);
  }

  @Test
  public void conferenceRequestDAOTests() {

    Fdb fdb = new Fdb();

    fdb.insertEntry(
        new ConferenceRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "Nobody",
            "start",
            "start",
            "end",
            Recurring.DAILY,
            5,
            "roomname"));
    fdb.insertEntry(
        new ConferenceRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "Jon",
            "start",
            "start",
            "end",
            Recurring.DAILY,
            5,
            "roomname"));
    fdb.insertEntry(
        new ConferenceRequest(
            LocalDateTime.of(2022, 2, 2, 2, 2),
            "Qui",
            "start",
            "start",
            "end",
            Recurring.DAILY,
            5,
            "roomname"));

    fdb.updateEntry(
        new ConferenceRequest(
            LocalDateTime.of(2021, 2, 2, 2, 2),
            "NEW PERSON",
            "start",
            "start",
            "end",
            Recurring.DAILY,
            5,
            "roomname"));

    fdb.undoChange(TableEntryType.CONFERENCE_REQUEST);
    fdb.undoChange(TableEntryType.CONFERENCE_REQUEST);

    fdb.updateEntry(
        new ConferenceRequest(
            LocalDateTime.of(2020, 2, 2, 2, 2),
            "ANOTHER NEW PERSON",
            "start",
            "start",
            "end",
            Recurring.DAILY,
            5,
            "roomname"));

    fdb.removeEntry(LocalDateTime.of(2021, 2, 2, 2, 2), TableEntryType.CONFERENCE_REQUEST);

    fdb.exportCSV("D:\\", TableEntryType.CONFERENCE_REQUEST);
  }
}
