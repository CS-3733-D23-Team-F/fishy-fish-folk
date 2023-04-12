package edu.wpi.fishfolk.database.rewrite;

import edu.wpi.fishfolk.database.rewrite.DAO.EdgeDAO;
import edu.wpi.fishfolk.database.rewrite.DAO.LocationDAO;
import edu.wpi.fishfolk.database.rewrite.DAO.MoveDAO;
import edu.wpi.fishfolk.database.rewrite.DAO.NodeDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Edge;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Location;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Move;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Fdb {

  private final Connection dbConnection;

  private final NodeDAO nodeTable;
  private final LocationDAO locationTable;
  private final MoveDAO moveTable;
  private final EdgeDAO edgeTable;

  /** Singleton facade for managing all PostgreSQL database communication. */
  public Fdb() {
    this.dbConnection = connect("teamfdb", "teamf", "teamf60");

    this.nodeTable = new NodeDAO();
    this.locationTable = new LocationDAO();
    this.moveTable = new MoveDAO();
    this.edgeTable = new EdgeDAO();
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
      return edgeTable.removeEntry((Edge) entry);
    }

    return false;
  }

  /**
   * Remove an entru from the PostgreSQL database.
   *
   * @param entry Entry to remove
   * @return True on successful removal, false otherwise
   */
  public boolean removeEntry(Object entry) {

    if (entry instanceof Node) {
      return nodeTable.removeEntry((Node) entry);
    } else if (entry instanceof Location) {
      return locationTable.removeEntry((Location) entry);
    } else if (entry instanceof Move) {
      return moveTable.removeEntry((Move) entry);
    } else if (entry instanceof Edge) {
      return edgeTable.removeEntry((Edge) entry);
    }

    return false;
  }
}
