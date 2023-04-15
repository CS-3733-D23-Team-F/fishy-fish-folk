package edu.wpi.fishfolk.database.rewrite;

import edu.wpi.fishfolk.database.rewrite.DAO.*;
import edu.wpi.fishfolk.database.rewrite.TableEntry.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Fdb {

  private final Connection dbConnection;

  // Hospital Map Tables
  public final NodeDAO nodeTable;
  public final LocationDAO locationTable;
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

  /** Singleton facade for managing all PostgreSQL database communication. */
  public Fdb() {

    this.dbConnection = connect("teamfdb", "teamf", "teamf60");

    // Hospital Map Tables
    this.nodeTable = new NodeDAO(dbConnection);
    this.locationTable = new LocationDAO(dbConnection);
    this.moveTable = new MoveDAO();
    this.edgeTable = new EdgeDAO();

    // Service Request Tables
    this.foodRequestTable = new FoodRequestDAO();
    this.supplyRequestTable = new SupplyRequestDAO();
    this.furnitureRequestTable = new FurnitureRequestDAO();
    this.flowerRequestTable = new FlowerRequestDAO();
    this.conferenceRequestTable = new ConferenceRequestDAO();

    // Login & User Accounts Tables
    this.userAccountTable = new UserAccountDAO();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  nodeTable.updateDatabase(true);
                  locationTable.updateDatabase(true);
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
        db.setSchema("proto2db");
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

  // TODO: Add generic undoChange method
}
