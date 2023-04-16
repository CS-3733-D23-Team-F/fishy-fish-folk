package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Move;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoveDAO implements IDAO<Move> {

  private final ArrayList<String> headers;

  private HashMap<Integer, Move> tableMap;
  private ArrayList<DataEdit<Move>> dataEdits;

  /** DAO for Move table in PostgreSQL database. */
  public MoveDAO() {
    this.headers = new ArrayList<>(List.of("id", "longname", "date"));
  }

  @Override
  public void init(boolean drop) {}

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(Move entry) {
    return false;
  }

  @Override
  public boolean updateEntry(Move entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public Move getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<Move> getAllEntries() {
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
