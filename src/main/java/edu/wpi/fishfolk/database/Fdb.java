package edu.wpi.fishfolk.database;

import java.sql.*;

/** @author Christian */
public class Fdb {

  public NodeTable nodeTable;
  public Table edgeTable;

  public Connection db;

  public Fdb() {
    this.db = connect("teamfdb", "teamf", "teamf60");
  }

  /**
   * Initialize the class by connecting to the database and establishing objects for both the node
   * and edge tables. TODO: Database name, user, password and table names are hardcoded -> Make them
   * configurable
   */

  /*
  public void initialize() {

    try {

      // STEP 1: Connect to PostgreSQL database

      db = connect("teamfdb", "teamf", "teamf60");
      db.setSchema("teamfdbd");
      System.out.println("[Fdb.initialize]: Current schema: " + db.getSchema() + ".");

      // STEP 2: Establish table objects

      nodeTable = new NodeTable(db, "nodetable2");
      if (createTable(db, nodeTable.getTableName())) {
        nodeTable.addHeaders();
      }

      edgeTable = new EdgeTable(db, "edgetable2");
      if (createTable(db, edgeTable.getTableName())) {
        edgeTable.addHeaders();
      }

      nodeTable.setEdgeTable(edgeTable);
      edgeTable.setNodeTable(nodeTable);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

   */

  /**
   * Connect to a PostgreSQL database.
   *
   * @param dbName Database name
   * @param dbUser Account
   * @param dbPass Password
   * @return Database connection object (null if no connection is made)
   */
  public Connection connect(String dbName, String dbUser, String dbPass) {
    Connection db = null;
    String dbServer = "jdbc:postgresql://database.cs.wpi.edu:5432/";
    try {
      Class.forName("org.postgresql.Driver");
      db = DriverManager.getConnection(dbServer + dbName, dbUser, dbPass);
      if (db != null) {
        System.out.println("[Fdb.connect]: Connection established.");
      } else {
        System.out.println("[Fdb.connect]: Connection failed.");
      }
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
    return db;
  }

  /** Disconnect from PostgreSQL database. */
  public void disconnect() {
    try {
      db.close();
      System.out.println("[Fdb.disconnect]: Connection closed.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Check if table exists within database.
   *
   * @param db Database connection
   * @param tbName Table name
   * @return True if table exists in database
   */
  public boolean tableExists(Connection db, String tbName) {
    Statement statement;
    try {
      statement = db.createStatement();
      String query =
          "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = '"
              + db.getSchema()
              + "' AND tablename = '"
              + tbName
              + "');";
      statement.execute(query);
      ResultSet results = statement.getResultSet();
      results.next();
      return results.getBoolean("exists");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /**
   * Creates a new empty table within database if it doesn't exist.
   *
   * @param conn Database connection
   * @param tableName Table name
   * @return True if a new table is created
   */
  public boolean createTable(Connection conn, String tableName) {
    Statement statement;
    try {
      statement = conn.createStatement();
      if (tableExists(conn, tableName)) {
        System.out.println("[Fdb.createTable]: Table " + tableName + " already exists.");
        return false;
      } else {
        String query = "CREATE TABLE " + tableName + " (id VARCHAR(10) PRIMARY KEY);";
        statement.executeUpdate(query);
        System.out.println("[Fdb.createTable]: Table " + tableName + " created.");
        return true;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  /** Runs through all the methods to test their functionality. DEBUG ONLY */
  /*
  public void runTests() {

    System.out.println("\n--- TESTING NODE TABLE ---\n");

    nodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv");

    Node existingNode = nodeTable.getNode("CCONF001L1");
    Node newNode =
        new Node(
            "CDEPT999L1",
            new Point2D(1980, 844),
            "L1",
            "Tower",
            NodeType.DEPT,
            "Day Surgery Family Waiting Floor L1",
            "Department C002L1");
    Node newNodeUpdated =
        new Node(
            "CDEPT999L1",
            new Point2D(1980, 844),
            "L2",
            "Space",
            NodeType.DEPT,
            "Night Surgery Family Waiting Floor L1",
            "Department C002L1");

    nodeTable.insertNode(existingNode);
    nodeTable.insertNode(newNode);

    nodeTable.updateNode(newNodeUpdated);

    nodeTable.removeNode(existingNode);

    nodeTable.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/L1NodesOutput.csv");

    System.out.println("\n--- TESTING EDGE TABLE ---\n");

    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Edges.csv");

    Edge existingEdge = edgeTable.getEdge("CCONF002L1_WELEV00HL1");

    Edge newEdge = new Edge("CDEPT002L1", "CDEPT003L1");
    Edge newEdgeUpdated = new Edge("CDEPT002L1AAA", "CDEPT003L1AAA");

    edgeTable.insertEdge(existingEdge);
    edgeTable.insertEdge(newEdge);

    edgeTable.updateEdge(newEdgeUpdated);

    edgeTable.removeEdge(existingEdge);

    edgeTable.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/L1EdgesOutput.csv");
  }
   */
}
