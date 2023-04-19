package edu.wpi.fishfolk.database.rewrite;

import edu.wpi.fishfolk.database.rewrite.DAO.*;
import edu.wpi.fishfolk.database.rewrite.TableEntry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Fdb {

  private final Connection dbConnection;

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

  // Login & User Accounts Tables
  private final UserAccountDAO userAccountTable;

  // TODO use map from tabletype -> dao table object to simplify delegation

  /** Singleton facade for managing all PostgreSQL database communication. */
  public Fdb() {

    this.dbConnection = connect("teamfdb", "teamf", "teamf60");

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

    // Login & User Accounts Tables
    this.userAccountTable = new UserAccountDAO(dbConnection);

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

                  disconnect();
                }));
  }

  /**
   * Connect to a PostgreSQL database.
   *
   * @param dbName Database name
   * @param dbUser Account
   * @param dbPass Password
   * @return Database connection object (null if no connection is made)
   */
  private Connection connect(String dbName, String dbUser, String dbPass) {
    String dbServer = "jdbc:postgresql://database.cs.wpi.edu:5432/";
    try {
      Class.forName("org.postgresql.Driver");
      Connection db = DriverManager.getConnection(dbServer + dbName, dbUser, dbPass);
      if (db != null) {
        System.out.println("[Fdb.connect]: Connection established.");
        db.setSchema("iter2db");
      } else {
        System.out.println("[Fdb.connect]: Connection failed.");
      }
      return db;
    } catch (ClassNotFoundException | SQLException e) {
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

    } else if (entry instanceof UserAccount) {
      return userAccountTable.insertEntry((UserAccount) entry);
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

    } else if (entry instanceof UserAccount) {
      return userAccountTable.updateEntry((UserAccount) entry);
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
      case USER_ACCOUNT:
        return userAccountTable.removeEntry(identifier);
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
      case USER_ACCOUNT:
        return userAccountTable.getEntry(identifier);
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
      case USER_ACCOUNT:
        return userAccountTable.getAllEntries();
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
      case USER_ACCOUNT:
        userAccountTable.undoChange();
        break;
    }
  }

  // TODO: ALL commas need to be removed/refactored from text fields before exporting
  /**
   * Import a CSV file into a table
   *
   * @param tableEntryType The type of table to import into
   * @param filepath
   * @param backup
   * @return true on success, false otherwise
   */
  public boolean importCSV(String filepath, boolean backup, TableEntryType tableEntryType) {

    System.out.println(Pattern.compile("(\\.[^.]+)$").matcher(filepath).toMatchResult().group());

    switch (tableEntryType) {
      case NODE:
        return nodeTable.importCSV(filepath, backup);

      case LOCATION:
        return locationTable.importCSV(filepath, backup);

      case MOVE:
        return moveTable.importCSV(filepath, backup);

      case EDGE:
        return edgeTable.importCSV(filepath, backup);

      case FOOD_REQUEST:
        return foodRequestTable.importCSV(filepath, backup);

      case SUPPLY_REQUEST:
        return supplyRequestTable.importCSV(filepath, backup);

      case FURNITURE_REQUEST:
        return furnitureRequestTable.importCSV(filepath, backup);

      case FLOWER_REQUEST:
        return flowerRequestTable.importCSV(filepath, backup);

      case CONFERENCE_REQUEST:
        return conferenceRequestTable.importCSV(filepath, backup);

      case USER_ACCOUNT:
        return userAccountTable.importCSV(filepath, backup);
    }
    return false;
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

      case USER_ACCOUNT:
        return userAccountTable.exportCSV(directory);
    }
    return false;
  }
}