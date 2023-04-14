package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class NodeDAO implements IDAO<Node> {

  private final Connection dbConnection;

  private final String tableName;
  private final ArrayList<String> headers;

  private HashMap<Integer, Node> tableMap;
  private Stack<DataEdit<Node>> dataEdits;

  /** DAO for Node table in PostgreSQL database. */
  public NodeDAO(Connection dbConnection) {
    this.dbConnection = dbConnection;
    this.tableName = "micronode";
    this.headers = new ArrayList<>(List.of("id", "x", "y", "floor", "building"));
    this.tableMap = new HashMap<>();
    this.dataEdits = new Stack<>();
  }

  @Override
  public boolean insertEntry(Node entry) {

    // Mark entry Node status as NEW
    entry.setStatus(EntryStatus.NEW);

    // Push an INSERT to the data edit stack
    dataEdits.push(new DataEdit<>(entry, DataEditType.INSERT));

    // Put entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean updateEntry(Node entry) {

    // Mark entry Node status as MODIFIED
    entry.setStatus(EntryStatus.MODIFIED);

    // Push an UPDATE to the data edit stack
    dataEdits.push(new DataEdit<>(tableMap.get(entry.getNodeID()), entry, DataEditType.UPDATE));

    // Update entry Node in local table
    tableMap.put(entry.getNodeID(), entry);

    return true;
  }

  @Override
  public boolean removeEntry(Node entry) {

    // Mark entry Node status as MODIFIED
    entry.setStatus(EntryStatus.MODIFIED);

    // Push a REMOVE to the data edit stack
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
        removeEntry(dataEdit.getNewEntry());
        break;
      case UPDATE:
        updateEntry(dataEdit.getOldEntry());
        break;
      case REMOVE:
        insertEntry(dataEdit.getNewEntry());
        break;
    }
  }

  @Override
  public boolean updateDatabase() {

    try {

      String insert =
          "INSERT INTO "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " VALUES (?, ?, ?, ?, ?);";

      /*
      UPDATE table_name
      SET column1 = value1, column2 = value2, ...
      WHERE condition;
       */

      String update =
          "UPDATE "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " SET "
              + headers.get(0)
              + " = ?, "
              + headers.get(1)
              + " = ?, "
              + headers.get(2)
              + " = ?, "
              + headers.get(3)
              + " = ?, "
              + headers.get(4)
              + " = ? WHERE "
              + headers.get(0)
              + " = ?;";

      String remove =
          "DELETE FROM "
              + dbConnection.getSchema()
              + "."
              + this.tableName
              + " WHERE "
              + headers.get(0)
              + " = ?;";

      PreparedStatement preparedInsert = dbConnection.prepareStatement(insert);
      PreparedStatement preparedUpdate = dbConnection.prepareStatement(update);
      PreparedStatement preparedRemove = dbConnection.prepareStatement(remove);

      for (DataEdit<Node> dataEdit : dataEdits) {
        switch (dataEdit.getType()) {
          case INSERT:
            preparedInsert.setInt(1, dataEdit.getNewEntry().getNodeID());
            preparedInsert.setDouble(2, dataEdit.getNewEntry().getPoint().getX());
            preparedInsert.setDouble(3, dataEdit.getNewEntry().getPoint().getY());
            preparedInsert.setString(4, dataEdit.getNewEntry().getFloor());
            preparedInsert.setString(5, dataEdit.getNewEntry().getBuilding());

            preparedInsert.executeUpdate();

            break;
          case UPDATE:
            preparedUpdate.setString(1, String.valueOf(dataEdit.getNewEntry().getNodeID()));
            preparedUpdate.setDouble(2, dataEdit.getNewEntry().getPoint().getX());
            preparedUpdate.setDouble(3, dataEdit.getNewEntry().getPoint().getY());
            preparedUpdate.setString(4, dataEdit.getNewEntry().getFloor());
            preparedUpdate.setString(5, dataEdit.getNewEntry().getBuilding());
            preparedUpdate.setString(6, String.valueOf(dataEdit.getNewEntry().getNodeID()));

            preparedUpdate.executeUpdate();

            break;
          case REMOVE:
            preparedRemove.setString(1, String.valueOf(dataEdit.getNewEntry().getNodeID()));

            preparedRemove.executeUpdate();

            break;
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      System.out.println(Arrays.toString(e.getStackTrace()));
    }

    return false;
  }
}
