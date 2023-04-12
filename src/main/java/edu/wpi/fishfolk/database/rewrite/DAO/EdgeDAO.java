package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Edge;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
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
  public boolean insertEntry(Edge entry) {
    return false;
  }

  @Override
  public boolean updateEntry(Edge entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Edge entry) {
    return false;
  }

  @Override
  public Edge getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<Edge> getAllEntries() {
    return null;
  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
