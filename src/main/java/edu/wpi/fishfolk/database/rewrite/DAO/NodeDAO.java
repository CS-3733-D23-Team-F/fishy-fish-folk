package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEditType;
import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javafx.geometry.Point2D;

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

    populateLocalTable();
  }

  public void populateLocalTable() {
    try {

      String getAll = "SELECT * FROM " + dbConnection.getSchema() + "." + this.tableName + ";";

      PreparedStatement preparedGetAll = dbConnection.prepareStatement(getAll);

      preparedGetAll.execute();
      ResultSet results = preparedGetAll.getResultSet();

      while (results.next()) {
        Node node =
            new Node(
                Integer.parseInt(results.getString(headers.get(0))),
                new Point2D(results.getDouble(headers.get(1)), results.getDouble(headers.get(2))),
                results.getString(headers.get(3)),
                results.getString(headers.get(4)));
        tableMap.put(node.getNodeID(), node);
      }

    } catch (SQLException | NumberFormatException e) {
      System.out.println(e.getMessage());
    }
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

    // Add all Nodes in local table to a list
    for (int nodeID : tableMap.keySet()) {
      allNodes.add(tableMap.get(nodeID));
    }

    return allNodes;
  }

  public void undoChange() {

    // Peek the top item of the data edit stack
    DataEdit<Node> dataEdit = dataEdits.peek();

    // Change behavior based on its data edit type
    switch (dataEdit.getType()) {
      case INSERT:

        // REMOVE the entry if it was an INSERT
        removeEntry(dataEdit.getNewEntry());
        break;

      case UPDATE:

        // UPDATE the entry if it was an UPDATE
        updateEntry(dataEdit.getOldEntry());
        break;

      case REMOVE:

        // REMOVE the entry if it was an INSERT
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

      dataEdits.clear();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }

    return true;
  }
}
