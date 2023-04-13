package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class NodeDAO implements IDAO<Node> {

  private final String tableName;
  private final ArrayList<String> headers;

  private HashMap<Integer, Node> tableMap;
  private Stack<DataEdit<Node>> dataEdits;

  /** DAO for Node table in PostgreSQL database. */
  public NodeDAO() {
    this.tableName = "micronode";
    this.headers = new ArrayList<>(List.of("id", "x", "y", "floor", "building"));
    this.tableMap = new HashMap<>();
    this.dataEdits = new Stack<>();
  }

  @Override
  public boolean insertEntry(Node entry) {

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Add entry Node to data edit stack as an INSERT
    dataEdits.push(new DataEdit<>(entry, DataEditType.INSERT));

    // Put entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(Node entry) {

    // Mark entry Node status as MODIFIED
    entry.setStatus(EntryStatus.MODIFIED);

    // Add old Node to data edit stack as an UPDATE
    dataEdits.push(new DataEdit<>(tableMap.get(entry.getNodeID()), DataEditType.UPDATE));

    // Update entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Node entry) {

    // Mark entry Node status as MODIFIED
    entry.setStatus(EntryStatus.MODIFIED);

    // Add entry Node to data edit stack as a REMOVE
    dataEdits.add(new DataEdit<>(entry, DataEditType.REMOVE));

    // Remove entry Node from local table
    tableMap.remove(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public Node getEntry(String identifier) {
    try {

      // Parse indentifier to integer
      int nodeID = Integer.parseInt(identifier);

      // Return Node object from local table
      return tableMap.get(nodeID);
    } catch (NumberFormatException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  @Override
  public ArrayList<Node> getAllEntries() {
    ArrayList<Node> allNodes = new ArrayList<>();

    for (int nodeID : tableMap.keySet()) {
      allNodes.add(tableMap.get(nodeID));
    }

    return allNodes;
  }

  public void undoChange() {

    DataEdit<Node> dataEdit = dataEdits.pop();

    switch (dataEdit.getType()) {
      case INSERT:
        removeEntry(dataEdit.getEntry());
        break;
      case UPDATE:
        updateEntry(dataEdit.getEntry());
        break;
      case REMOVE:
        insertEntry(dataEdit.getEntry());
        break;
    }
  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
