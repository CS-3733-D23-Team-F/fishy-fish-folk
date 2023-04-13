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
  public boolean insertEntry(ConferenceRequest entry) {
    return false;
  }

  @Override
  public boolean updateEntry(ConferenceRequest entry) {
    return false;
  }

  @Override
  public boolean removeEntry(ConferenceRequest entry) {
    return false;
  }

  @Override
  public ConferenceRequest getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<ConferenceRequest> getAllEntries() {
    return null;
  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
