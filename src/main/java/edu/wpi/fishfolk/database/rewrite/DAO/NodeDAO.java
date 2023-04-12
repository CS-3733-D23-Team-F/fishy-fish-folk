package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeDAO implements IDAO<Node> {

  private final ArrayList<String> headers;

  private HashMap<Integer, Node> tableMap;
  private ArrayList<DataEdit<Node>> dataEdits;

  /** DAO for Node table in PostgreSQL database. */
  public NodeDAO() {
    this.headers = new ArrayList<>(List.of("id", "x", "y", "floor", "building"));
  }

  @Override
  public boolean insertEntry(Node entry) {
    return false;
  }

  @Override
  public boolean updateEntry(Node entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Node entry) {
    return false;
  }

  @Override
  public Node getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<Node> getAllEntries() {
    return null;
  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
