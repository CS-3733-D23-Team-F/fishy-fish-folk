package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.ui.NewFoodItem;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

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
}
