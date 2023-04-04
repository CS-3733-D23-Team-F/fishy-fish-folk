package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.pathfinding.*;

public class Main {

  public static void main(String[] args) {

    Fapp.launch(Fapp.class, args); // run ui

    /*

    set up database connection and nodeTable,  edgeTable variables

    Fdb fdb = new Fdb();
    try {
      fdb.db.setSchema("proto2db");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    NodeTable nodeTable = new NodeTable(fdb.db, "nodeTable");
    Table edgeTable = new Table(fdb.db, "edge");
    edgeTable.setHeaders(
        new ArrayList<>(List.of("node1", "node2")), new ArrayList<>(List.of("String", "String")));
    edgeTable.init(false);

     */

    /*

    import CSVs into 3 subtables and edge

    nodeTable.importCSV(
        "src/main/resources/edu/wpi/fishfolk/csv/MicroNode.csv",
        "src/main/resources/edu/wpi/fishfolk/csv/Location.csv",
        "src/main/resources/edu/wpi/fishfolk/csv/Move.csv", false);


    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Edge.csv", false);

     */

    /*

    create and speed test graph

    Graph g = new Graph(nodeTable, edgeTable);
    g.populate();

    g.speedTest(500, false);

     */
  }
}
