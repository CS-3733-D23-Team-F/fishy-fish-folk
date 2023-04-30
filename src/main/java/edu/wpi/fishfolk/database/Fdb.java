package edu.wpi.fishfolk.database;

import static edu.wpi.fishfolk.util.NodeType.*;

import edu.wpi.fishfolk.database.DAO.*;
import edu.wpi.fishfolk.database.TableEntry.*;
import edu.wpi.fishfolk.mapeditor.NodeText;
import edu.wpi.fishfolk.util.NodeType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import lombok.Getter;

public class Fdb {

  private final Connection dbConnection;

  @Getter
  private final List<NodeType> nodeTypes =
      List.of(BATH, CONF, DEPT, ELEV, EXIT, HALL, INFO, LABS, REST, RETL, SERV, STAI);

  // Hospital Map Tables
  private final NodeDAO nodeTable;
  private final LocationDAO locationTable;
  private final MoveDAO moveTable;
  private final EdgeDAO edgeTable;

  // Service Request Tables
  private final FoodRequestDAO foodRequestTable;
  private final SupplyRequestDAO supplyRequestTable;
  private final FurnitureRequestDAO furnitureRequestTable;
  private final FlowerRequestDAO flowerRequestTable;
  private final ConferenceRequestDAO conferenceRequestTable;
  private final ITRequestDAO itRequestTable;

  // Login & User Accounts Tables
  private final UserAccountDAO userAccountTable;

  // Signage Tables
  private final SignagePresetDAO signagePresetTable;

  private final AlertDAO alertTable;

  // TODO refactor: use map from tabletype -> dao table object to simplify delegation

  /** Singleton facade for managing all PostgreSQL database communication. */
  public Fdb() {

    this.dbConnection = connect();

    // Hospital Map Tables
    this.nodeTable = new NodeDAO(dbConnection);
    this.locationTable = new LocationDAO(dbConnection);
    this.moveTable = new MoveDAO(dbConnection);
    this.edgeTable = new EdgeDAO(dbConnection);

    // Service Request Tables
    this.foodRequestTable = new FoodRequestDAO(dbConnection);
    this.supplyRequestTable = new SupplyRequestDAO(dbConnection);
    this.furnitureRequestTable = new FurnitureRequestDAO(dbConnection);
    this.flowerRequestTable = new FlowerRequestDAO(dbConnection);
    this.conferenceRequestTable = new ConferenceRequestDAO(dbConnection);
    this.itRequestTable = new ITRequestDAO(dbConnection);

    // Login & User Accounts Tables
    this.userAccountTable = new UserAccountDAO(dbConnection);

    // Signage Tables
    this.signagePresetTable = new SignagePresetDAO(dbConnection);

    // Alert table
    this.alertTable = new AlertDAO(dbConnection);

    // importLocalCSV();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("[Fdb]: Shutdown received...");
                  nodeTable.updateDatabase(true);
                  locationTable.updateDatabase(true);
                  foodRequestTable.updateDatabase(true);
                  supplyRequestTable.updateDatabase(true);
                  furnitureRequestTable.updateDatabase(true);
                  flowerRequestTable.updateDatabase(true);
                  conferenceRequestTable.updateDatabase(true);
                  itRequestTable.updateDatabase(true);
                  userAccountTable.updateDatabase(true);
                  signagePresetTable.updateDatabase(true);
                  alertTable.updateDatabase(true);

                  disconnect();
                }));
  }

  /**
   * Connect to a PostgreSQL database.
   *
   * @return Database connection object (null if no connection is made)
   */
  private Connection connect() {

    try {

      // Attempt a database connection
      Connection db = ConnectionBuilder.buildConnection();

      if (db != null) {

        // Notify console of successful connection
        System.out.println("[Fdb.connect]: Connection established.");

        // Set timeout to 1 day (86400000 ms)
        String query = "SET idle_session_timeout = 86400000;";
        Statement statement = db.createStatement();
        statement.executeUpdate(query);

      } else {
        System.out.println("[Fdb.connect]: Connection failed.");
      }
      return db;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /** Disconnect from PostgreSQL database. */
  public void disconnect() {
    try {
      dbConnection.close();
      System.out.println("[Fdb.disconnect]: Connection closed.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Insert an entry into the PostgreSQL database.
   *
   * @param entry Entry to insert
   * @return True on successful insertion, false otherwise
   */
  public boolean insertEntry(Object entry) {

    if (entry instanceof Node) {
      return nodeTable.insertEntry((Node) entry);

    } else if (entry instanceof Location) {
      return locationTable.insertEntry((Location) entry);

    } else if (entry instanceof Move) {
      return moveTable.insertEntry((Move) entry);

    } else if (entry instanceof Edge) {
      return edgeTable.insertEntry((Edge) entry);

    } else if (entry instanceof FoodRequest) {
      return foodRequestTable.insertEntry((FoodRequest) entry);

    } else if (entry instanceof SupplyRequest) {
      return supplyRequestTable.insertEntry((SupplyRequest) entry);

    } else if (entry instanceof FurnitureRequest) {
      return furnitureRequestTable.insertEntry((FurnitureRequest) entry);

    } else if (entry instanceof FlowerRequest) {
      return flowerRequestTable.insertEntry((FlowerRequest) entry);

    } else if (entry instanceof ConferenceRequest) {
      return conferenceRequestTable.insertEntry((ConferenceRequest) entry);

    } else if (entry instanceof ITRequest) {
      return itRequestTable.insertEntry((ITRequest) entry);

    } else if (entry instanceof UserAccount) {
      return userAccountTable.insertEntry((UserAccount) entry);

    } else if (entry instanceof SignagePreset) {
      return signagePresetTable.insertEntry((SignagePreset) entry);

    } else if (entry instanceof Alert) {
      return alertTable.insertEntry((Alert) entry);
    }

    return false;
  }

  /**
   * Update an entry in the PostgreSQL database.
   *
   * @param entry Entry to update
   * @return True on successful update, false otherwise
   */
  public boolean updateEntry(Object entry) {

    if (entry instanceof Node) {
      return nodeTable.updateEntry((Node) entry);

    } else if (entry instanceof Location) {
      return locationTable.updateEntry((Location) entry);

    } else if (entry instanceof Move) {
      return moveTable.updateEntry((Move) entry);

    } else if (entry instanceof Edge) {
      return edgeTable.updateEntry((Edge) entry);

    } else if (entry instanceof FoodRequest) {
      return foodRequestTable.updateEntry((FoodRequest) entry);

    } else if (entry instanceof SupplyRequest) {
      return supplyRequestTable.updateEntry((SupplyRequest) entry);

    } else if (entry instanceof FurnitureRequest) {
      return furnitureRequestTable.updateEntry((FurnitureRequest) entry);

    } else if (entry instanceof FlowerRequest) {
      return flowerRequestTable.updateEntry((FlowerRequest) entry);

    } else if (entry instanceof ConferenceRequest) {
      return conferenceRequestTable.updateEntry((ConferenceRequest) entry);

    } else if (entry instanceof ITRequest) {
      return itRequestTable.updateEntry((ITRequest) entry);

    } else if (entry instanceof UserAccount) {
      return userAccountTable.updateEntry((UserAccount) entry);

    } else if (entry instanceof SignagePreset) {
      return signagePresetTable.updateEntry((SignagePreset) entry);

    } else if (entry instanceof Alert) {
      return alertTable.updateEntry((Alert) entry);
    }

    return false;
  }

  /**
   * Remove an entry from the PostgreSQL database.
   *
   * @param identifier The entry's unique identifier (i.e. ID number)
   * @param tableEntryType The intended entry's table entry type
   * @return True on successful removal, false otherwise
   */
  public boolean removeEntry(Object identifier, TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        return nodeTable.removeEntry(identifier);
      case LOCATION:
        return locationTable.removeEntry(identifier);
      case MOVE:
        return moveTable.removeEntry(identifier);
      case EDGE:
        return edgeTable.removeEntry(identifier);
      case FOOD_REQUEST:
        return foodRequestTable.removeEntry(identifier);
      case SUPPLY_REQUEST:
        return supplyRequestTable.removeEntry(identifier);
      case FURNITURE_REQUEST:
        return furnitureRequestTable.removeEntry(identifier);
      case FLOWER_REQUEST:
        return flowerRequestTable.removeEntry(identifier);
      case CONFERENCE_REQUEST:
        return conferenceRequestTable.removeEntry(identifier);
      case IT_REQUEST:
        return itRequestTable.removeEntry(identifier);
      case USER_ACCOUNT:
        return userAccountTable.removeEntry(identifier);
      case SIGNAGE_PRESET:
        return signagePresetTable.removeEntry(identifier);
      case ALERT:
        return alertTable.removeEntry(identifier);
    }

    return false;
  }

  /**
   * Returns an entry from a local table that matches the unique identifier specified.
   *
   * @param identifier The intended entry's table entry type
   * @param tableEntryType The intended entry's table entry type
   * @return The intended table entry, null on error
   */
  public Object getEntry(Object identifier, TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        return nodeTable.getEntry(identifier);
      case LOCATION:
        return locationTable.getEntry(identifier);
      case MOVE:
        return moveTable.getEntry(identifier);
      case EDGE:
        return edgeTable.getEntry(identifier);
      case FOOD_REQUEST:
        return foodRequestTable.getEntry(identifier);
      case SUPPLY_REQUEST:
        return supplyRequestTable.getEntry(identifier);
      case FURNITURE_REQUEST:
        return furnitureRequestTable.getEntry(identifier);
      case FLOWER_REQUEST:
        return flowerRequestTable.getEntry(identifier);
      case CONFERENCE_REQUEST:
        return conferenceRequestTable.getEntry(identifier);
      case IT_REQUEST:
        return itRequestTable.getEntry(identifier);
      case USER_ACCOUNT:
        return userAccountTable.getEntry(identifier);
      case SIGNAGE_PRESET:
        return signagePresetTable.getEntry(identifier);
      case ALERT:
        return alertTable.getEntry(identifier);
    }

    return null;
  }

  /**
   * Returns all entries in a local table in a list.
   *
   * @param tableEntryType The intended table's table entry type
   * @return The intended table entries as a list, null on error
   */
  public ArrayList<?> getAllEntries(TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        return nodeTable.getAllEntries();
      case LOCATION:
        return locationTable.getAllEntries();
      case MOVE:
        return moveTable.getAllEntries();
      case EDGE:
        return edgeTable.getAllEntries();
      case FOOD_REQUEST:
        return foodRequestTable.getAllEntries();
      case SUPPLY_REQUEST:
        return supplyRequestTable.getAllEntries();
      case FURNITURE_REQUEST:
        return furnitureRequestTable.getAllEntries();
      case FLOWER_REQUEST:
        return flowerRequestTable.getAllEntries();
      case CONFERENCE_REQUEST:
        return conferenceRequestTable.getAllEntries();
      case IT_REQUEST:
        return itRequestTable.getAllEntries();
      case USER_ACCOUNT:
        return userAccountTable.getAllEntries();
      case SIGNAGE_PRESET:
        return signagePresetTable.getAllEntries();
      case ALERT:
        return alertTable.getAllEntries();
    }

    return null;
  }

  /**
   * Reverts the most recent change made to a local table. The reverted change will not occur in a
   * PostgreSQL database.
   *
   * @param tableEntryType Type of table to revert a change to
   */
  public void undoChange(TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        nodeTable.undoChange();
        break;
      case LOCATION:
        locationTable.undoChange();
        break;
      case MOVE:
        moveTable.undoChange();
        break;
      case EDGE:
        edgeTable.undoChange();
        break;
      case FOOD_REQUEST:
        foodRequestTable.undoChange();
        break;
      case SUPPLY_REQUEST:
        supplyRequestTable.undoChange();
        break;
      case FURNITURE_REQUEST:
        furnitureRequestTable.undoChange();
        break;
      case FLOWER_REQUEST:
        flowerRequestTable.undoChange();
        break;
      case CONFERENCE_REQUEST:
        conferenceRequestTable.undoChange();
        break;
      case IT_REQUEST:
        itRequestTable.undoChange();
        break;
      case USER_ACCOUNT:
        userAccountTable.undoChange();
        break;
      case SIGNAGE_PRESET:
        signagePresetTable.undoChange();
        break;
      case ALERT:
        alertTable.undoChange();
        break;
    }
  }

  /**
   * Pushes ALL staged changes to a PostgreSQL database table.
   *
   * @param tableEntryType Type of table to push all changes to
   * @return True on success, false otherwise
   */
  public boolean updateDatabase(TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        return nodeTable.updateDatabase(true);
      case LOCATION:
        return locationTable.updateDatabase(true);
      case MOVE:
        return moveTable.updateDatabase(true);
      case EDGE:
        return edgeTable.updateDatabase(true);
      case FOOD_REQUEST:
        return foodRequestTable.updateDatabase(true);
      case SUPPLY_REQUEST:
        return supplyRequestTable.updateDatabase(true);
      case FURNITURE_REQUEST:
        return furnitureRequestTable.updateDatabase(true);
      case FLOWER_REQUEST:
        return flowerRequestTable.updateDatabase(true);
      case CONFERENCE_REQUEST:
        return conferenceRequestTable.updateDatabase(true);
      case IT_REQUEST:
        return itRequestTable.updateDatabase(true);
      case USER_ACCOUNT:
        return userAccountTable.updateDatabase(true);
      case SIGNAGE_PRESET:
        return signagePresetTable.updateDatabase(true);
      case ALERT:
        return alertTable.updateDatabase(true);
    }

    return false;
  }

  // TODO: ALL commas need to be removed/refactored from text fields before exporting
  /**
   * Import a CSV file into a table
   *
   * @param tableEntryType The type of table to import into
   * @param tableFilepath
   * @param backup
   * @return true on success, false otherwise
   */
  public boolean importCSV(String tableFilepath, boolean backup, TableEntryType tableEntryType) {

    // Commented out due to regex error -Christian
    // System.out.println(Pattern.compile("(\\.[^.]+)$").matcher(tableFilepath).toMatchResult().group());

    switch (tableEntryType) {
      case NODE:
        return nodeTable.importCSV(tableFilepath, backup);

      case LOCATION:
        return locationTable.importCSV(tableFilepath, backup);

      case MOVE:
        return moveTable.importCSV(tableFilepath, backup);

      case EDGE:
        return edgeTable.importCSV(tableFilepath, backup);

      case FOOD_REQUEST:
        return foodRequestTable.importCSV(tableFilepath, backup);

      case SUPPLY_REQUEST:
        return supplyRequestTable.importCSV(tableFilepath, backup);

      case FURNITURE_REQUEST:
        return furnitureRequestTable.importCSV(tableFilepath, backup);

      case FLOWER_REQUEST:
        return flowerRequestTable.importCSV(tableFilepath, backup);

      case CONFERENCE_REQUEST:
        return conferenceRequestTable.importCSV(tableFilepath, backup);

      case IT_REQUEST:
        return itRequestTable.importCSV(filepath, backup);

      case USER_ACCOUNT:
        return userAccountTable.importCSV(tableFilepath, backup);

      case SIGNAGE_PRESET:
        return signagePresetTable.importCSV(tableFilepath, backup);

      case ALERT:
        return alertTable.importCSV(tableFilepath, backup);
    }
    return false;
  }

  /**
   * Potentially temporary secondary declaration of importCSV for classes with subtables.
   *
   * @param tableFilepath Filepath of the main table
   * @param subtableFilepath Filepath of the subtable
   * @param backup Backup the current db to a CSV?
   * @param tableEntryType Intended table to perform operation
   * @return True on success, false otherwise
   */
  public boolean importCSV(
      String tableFilepath,
      String subtableFilepath,
      boolean backup,
      TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case FOOD_REQUEST:
        return foodRequestTable.importCSV(tableFilepath, subtableFilepath, backup);

      case SUPPLY_REQUEST:
        return supplyRequestTable.importCSV(tableFilepath, subtableFilepath, backup);

      case FLOWER_REQUEST:
        return flowerRequestTable.importCSV(tableFilepath, subtableFilepath, backup);

      case SIGNAGE_PRESET:
        return signagePresetTable.importCSV(tableFilepath, subtableFilepath, backup);
    }

    return false;
  }

  /**
   * DEBUG ONLY: Import only a subtable from a CSV.
   *
   * @param subtableFilepath Filepath of the subtable
   * @param tableEntryType Intended table to perform operation
   * @return Hashmap of Lists with subtable IDs as keys
   */
  public HashMap<?, ?> importSubtable(String subtableFilepath, TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case FOOD_REQUEST:
        return foodRequestTable.importSubtable(subtableFilepath);

      case SUPPLY_REQUEST:
        return supplyRequestTable.importSubtable(subtableFilepath);

      case FLOWER_REQUEST:
        return flowerRequestTable.importSubtable(subtableFilepath);

      case SIGNAGE_PRESET:
        return signagePresetTable.importSubtable(subtableFilepath);
    }

    return null;
  }

  /**
   * Export a table to a CSV file
   *
   * @param directory The directory to export the file into
   * @param tableEntryType The type of table to export
   * @return true on success, false otherwise
   */
  public boolean exportCSV(String directory, TableEntryType tableEntryType) {

    switch (tableEntryType) {
      case NODE:
        return nodeTable.exportCSV(directory);

      case LOCATION:
        return locationTable.exportCSV(directory);

      case MOVE:
        return moveTable.exportCSV(directory);

      case EDGE:
        return edgeTable.exportCSV(directory);

      case FOOD_REQUEST:
        return foodRequestTable.exportCSV(directory);

      case SUPPLY_REQUEST:
        return supplyRequestTable.exportCSV(directory);

      case FURNITURE_REQUEST:
        return furnitureRequestTable.exportCSV(directory);

      case FLOWER_REQUEST:
        return flowerRequestTable.exportCSV(directory);

      case CONFERENCE_REQUEST:
        return conferenceRequestTable.exportCSV(directory);

      case IT_REQUEST:
        return itRequestTable.exportCSV(directory);

      case USER_ACCOUNT:
        return userAccountTable.exportCSV(directory);

      case SIGNAGE_PRESET:
        return signagePresetTable.exportCSV(directory);

      case ALERT:
        return alertTable.exportCSV(directory);
    }
    return false;
  }

  /** Internal method to import from local CSVs. */
  private void importLocalCSV() {
    nodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Node.csv", false);
    locationTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Location.csv", false);
    moveTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Move.csv", false);
    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Edge.csv", false);
  }

  /**
   * Get the most recent locations at the given node.
   *
   * @param nodeID
   * @return List<Location>
   */
  public List<Location> getLocations(int nodeID, LocalDate date) {

    HashMap<String, LocalDate> dates = new HashMap<>();

    List<Move> allMoves = moveTable.getAllEntries();

    // filter by nodeid and location
    allMoves.forEach(
        move -> {
          if (move.getNodeID() == nodeID && move.getDate().isBefore(date)) {
            dates.put(move.getLongName(), move.getDate());
          }
        });

    // second pass through all moves to check if longnames in moves
    // later get moved to a different node
    allMoves.forEach(
        move -> {
          String longname = move.getLongName();
          if (dates.containsKey(longname) && move.getDate().isAfter(dates.get(longname))) {
            dates.remove(longname);
          }
        });

    // get location for each longname left over in dates map
    return dates.keySet().stream().map(locationTable::getEntry).toList();
  }

  /**
   * Get the Node ID corresponding to the given Location on a given date
   *
   * @param longname the longname of the Location
   * @param date the date to search on
   * @return the ID (int > 0) of the node where the given location is found at the given date,
   *     otherwise -1.
   */
  public int getNodeIDFromLocation(String longname, LocalDate date) {

    int nodeID = -1;
    LocalDate lastMoveDate = LocalDate.MIN;

    for (Move move : moveTable.getAllEntries()) {

      if (move.getLongName().equals(longname)
          && move.getDate().isBefore(date)
          && move.getDate().isAfter(lastMoveDate)) {
        lastMoveDate = move.getDate();
        nodeID = move.getNodeID();
      }
    }

    return nodeID;
  }

  /**
   * Get the Nodes on the given floor.
   *
   * @param floor
   * @return
   */
  public List<Node> getNodesOnFloor(String floor) {

    /* two options:
    1. push all changes to database and query directly
    2. iterate over localtable and filter
     */

    // using option 2 for simplicity
    return nodeTable.getAllEntries().stream().filter(n -> n.getFloor().equals(floor)).toList();
  }

  /**
   * Get the longnames of the Locations that aren't of type HALL, ELEV, or STAI
   *
   * @return
   */
  public List<String> getDestLongnames() {

    return locationTable.getAllEntries().stream()
        .filter(Location::isDestination)
        .map(Location::getLongName)
        .sorted()
        .toList();
  }

  public List<Location> getDestLocations() {

    return locationTable.getAllEntries().stream().filter(Location::isDestination).toList();
  }

  public HashMap<NodeType, List<NodeText>> getLocationLabelsByType(String floor, LocalDate date) {

    HashMap<NodeType, List<NodeText>> map = new HashMap<>();
    nodeTypes.forEach(type -> map.put(type, new ArrayList<>()));

    getNodesOnFloor(floor)
        .forEach(
            node -> {
              int[] offset = {0}; // offset multiple labels at the same node

              getLocations(node.getNodeID(), date)
                  .forEach(
                      location -> {
                        // create text label and add it to list of labels in the map
                        String labelText = location.getShortName();
                        map.get(location.getNodeType())
                            .add(
                                new NodeText(
                                    node.getNodeID(),
                                    node.getX() - labelText.length() * 5 + offset[0],
                                    node.getY() - 10 + offset[0],
                                    labelText));
                        offset[0] += 20;
                      });
            });

    return map;
  }

  public int getNextNodeID() {
    return nodeTable.getNextID();
  }

  public int getNumNodes() {
    return nodeTable.getNumNodes();
  }

  /**
   * Get the IDs of the nodes that share an edge with the given node.
   *
   * @param nodeID
   * @return
   */
  public List<Integer> getNeighborIDs(int nodeID) {

    LinkedList<Integer> neighbors = new LinkedList<>();

    edgeTable
        .getAllEntries()
        .forEach(
            edge -> {
              if (edge.getStartNode() == nodeID) neighbors.add(edge.getEndNode());
              if (edge.getEndNode() == nodeID) neighbors.add(edge.getStartNode());
            });

    return neighbors;
  }

  public List<Edge> getEdgesOnFloor(String floor) {
    return edgeTable.getAllEntries().stream()
        .filter(
            edge -> {
              Node start = nodeTable.getEntry(edge.getStartNode());
              Node end = nodeTable.getEntry(edge.getEndNode());
              if (start != null && end != null) {
                return start.getFloor().equals(floor) && end.getFloor().equals(floor);
              } else return false;
            })
        .toList();
  }

  public Alert getLatestAlert() {
    return alertTable.getLatestAlert();
  }
}
