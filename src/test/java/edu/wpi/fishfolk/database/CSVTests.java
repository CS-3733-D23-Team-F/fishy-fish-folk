package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.ui.NewFoodItem;
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
}
