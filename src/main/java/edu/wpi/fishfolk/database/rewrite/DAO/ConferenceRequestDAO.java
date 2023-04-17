package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.ConferenceRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConferenceRequestDAO implements IDAO<ConferenceRequest> {

  private final ArrayList<String> headers;

  private HashMap<Integer, ConferenceRequest> tableMap;
  private ArrayList<DataEdit<ConferenceRequest>> dataEdits;

  /** DAO for Conference Request table in PostgreSQL database. */
  public ConferenceRequestDAO() {
    this.headers = new ArrayList<>(List.of("id", "assignee", "status"));
  }

  @Override
  public void init(boolean drop) {}

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(ConferenceRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(ConferenceRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public ConferenceRequest getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<ConferenceRequest> getAllEntries() {
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
