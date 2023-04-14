package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.SupplyRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SupplyRequestDAO implements IDAO<SupplyRequest> {

  private final ArrayList<String> headers;

  private HashMap<Integer, SupplyRequest> tableMap;
  private ArrayList<DataEdit<SupplyRequest>> dataEdits;

  /** DAO for Supply Request table in PostgreSQL database. */
  public SupplyRequestDAO() {
    this.headers =
        new ArrayList<>(
            List.of("id", "assignee", "status", "supplies", "link", "roomnumber", "notes"));
  }

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(SupplyRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(SupplyRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public SupplyRequest getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<SupplyRequest> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase(boolean updateAll) {
    return false;
  }
}
