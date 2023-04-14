package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FoodRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodRequestDAO implements IDAO<FoodRequest> {

  private final ArrayList<String> headers;

  private HashMap<Integer, FoodRequest> tableMap;
  private ArrayList<DataEdit<FoodRequest>> dataEdits;

  /** DAO for Food Request table in PostgreSQL database. */
  public FoodRequestDAO() {
    this.headers =
        new ArrayList<>(
            List.of(
                "id", "assignee", "status", "items", "time", "payer", "location", "totalprice"));
  }

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(FoodRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(FoodRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public FoodRequest getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<FoodRequest> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase(boolean updateAll) {
    return false;
  }
}
