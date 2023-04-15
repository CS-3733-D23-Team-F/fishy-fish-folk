package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FurnitureRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FurnitureRequestDAO implements IDAO<FurnitureRequest> {

  private final ArrayList<String> headers;

  private HashMap<Integer, FurnitureRequest> tableMap;
  private ArrayList<DataEdit<FurnitureRequest>> dataEdits;

  /** DAO for Furniture Request table in PostgreSQL database. */
  public FurnitureRequestDAO() {
    this.headers =
        new ArrayList<>(
            List.of(
                "id", "assignee", "status", "item", "servicetype", "roomnumber", "deliverydate"));
  }

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(FurnitureRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(FurnitureRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public FurnitureRequest getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<FurnitureRequest> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase(boolean updateAll) {
    return false;
  }

  @Override
  public boolean importCSV(String filepath, boolean backup) {
    return false;
  }

  @Override
  public boolean exportCSV(String directory) {
    return false;
  }
}
