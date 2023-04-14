package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FlowerRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlowerRequestDAO implements IDAO<FlowerRequest> {

  private final ArrayList<String> headers;

  private HashMap<Integer, FlowerRequest> tableMap;
  private ArrayList<DataEdit<FlowerRequest>> dataEdits;

  /** DAO for Flower Request table in PostgreSQL database. */
  public FlowerRequestDAO() {
    this.headers =
        new ArrayList<>(
            List.of(
                "id", "assignee", "status", "items", "payer", "deliveryLocation", "totalPrice"));
  }

  @Override
  public void populateLocalTable() {

  }

  @Override
  public boolean insertEntry(FlowerRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(FlowerRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(FlowerRequest entry) {
    return false;
  }

  @Override
  public FlowerRequest getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<FlowerRequest> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {

  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
