package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.ui.FlowerItem;
import edu.wpi.fishfolk.ui.NewFoodItem;
import edu.wpi.fishfolk.ui.Sign;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

// PLEASE NOTE: All of these tests make use of hardcoded paths. To use them, replicate or replace
// the specified paths. This method is used because JavaFX sucks :/

public class CSVTests {

  @Test
  public void foodRequestSubtableImport() {
    Fdb fdb = new Fdb();

    HashMap<Integer, ArrayList<NewFoodItem>> items =
        (HashMap<Integer, ArrayList<NewFoodItem>>)
            fdb.importSubtable(
                "D:\\CSVTests\\foodrequestfooditems.csv", TableEntryType.FOOD_REQUEST);

    System.out.println(items.toString());
  }

  @Test
  public void foodRequestTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV(
        "D:\\CSVTests\\foodrequest.csv",
        "D:\\CSVTests\\foodrequestfooditems.csv",
        false,
        TableEntryType.FOOD_REQUEST);
  }

  @Test
  public void foodRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.FOOD_REQUEST);
  }

  @Test
  public void supplyRequestSubtableImport() {
    Fdb fdb = new Fdb();

    HashMap<Integer, ArrayList<SupplyItem>> items =
        (HashMap<Integer, ArrayList<SupplyItem>>)
            fdb.importSubtable(
                "D:\\CSVTests\\supplyrequestsupplyitems.csv", TableEntryType.SUPPLY_REQUEST);

    System.out.println(items.toString());
  }

  @Test
  public void supplyRequestTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV(
        "D:\\CSVTests\\supplyrequest.csv",
        "D:\\CSVTests\\supplyrequestsupplyitems.csv",
        false,
        TableEntryType.SUPPLY_REQUEST);
  }

  @Test
  public void supplyRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.SUPPLY_REQUEST);
  }

  @Test
  public void flowerRequestSubtableImport() {
    Fdb fdb = new Fdb();

    HashMap<Integer, ArrayList<FlowerItem>> items =
        (HashMap<Integer, ArrayList<FlowerItem>>)
            fdb.importSubtable(
                "D:\\CSVTests\\flowerrequestfloweritems.csv", TableEntryType.FLOWER_REQUEST);

    System.out.println(items.toString());
  }

  @Test
  public void flowerRequestTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV(
        "D:\\CSVTests\\flowerrequest.csv",
        "D:\\CSVTests\\flowerrequestfloweritems.csv",
        false,
        TableEntryType.FLOWER_REQUEST);
  }

  @Test
  public void flowerRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.FLOWER_REQUEST);
  }

  @Test
  public void signagePresetSubtableImport() {
    Fdb fdb = new Fdb();

    HashMap<Integer, Sign[]> items =
        (HashMap<Integer, Sign[]>)
            fdb.importSubtable(
                "D:\\CSVTests\\signagepresetsigns.csv", TableEntryType.SIGNAGE_PRESET);

    System.out.println(items.toString());
  }

  @Test
  public void signagePresetTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV(
        "D:\\CSVTests\\signagepreset.csv",
        "D:\\CSVTests\\signagepresetsigns.csv",
        false,
        TableEntryType.SIGNAGE_PRESET);
  }

  @Test
  public void signageRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.SIGNAGE_PRESET);
  }

  @Test
  public void furnitureRequestTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV("D:\\CSVTests\\furniturerequest.csv", false, TableEntryType.FURNITURE_REQUEST);
  }

  @Test
  public void furnitureRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.FURNITURE_REQUEST);
  }

  @Test
  public void conferenceRequestTableImport() {
    Fdb fdb = new Fdb();

    fdb.importCSV("D:\\CSVTests\\conferencerequest.csv", false, TableEntryType.CONFERENCE_REQUEST);
  }

  @Test
  public void conferenceRequestTableExport() {
    Fdb fdb = new Fdb();

    fdb.exportCSV("D:\\CSVTests\\out", TableEntryType.CONFERENCE_REQUEST);
  }
}
