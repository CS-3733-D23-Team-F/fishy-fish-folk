package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EdgeDAO implements IDAO<Edge> {

  private final ArrayList<String> headers;

  private HashMap<Integer, Edge> tableMap;
  private ArrayList<DataEdit<Edge>> dataEdits;

  /** DAO for Edge table in PostgreSQL database. */
  public EdgeDAO() {
    this.headers = new ArrayList<>(List.of("startnode", "endnode"));
  }

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(Edge entry) {
    return false;
  }

  @Override
  public boolean updateEntry(Edge entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public Edge getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<Edge> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase(boolean updateAll) {
    return false;
  }
}
