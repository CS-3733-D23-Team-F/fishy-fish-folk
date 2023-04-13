package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.*;
import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import edu.wpi.fishfolk.pathfinding.*;
import javafx.geometry.Point2D;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args); // run ui

    Fdb fdb = new Fdb();

    fdb.nodeTable.insertEntry(new Node(9997, new Point2D(100, 200), "carpet", "bingus"));
    fdb.nodeTable.insertEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));
    fdb.nodeTable.insertEntry(new Node(9999, new Point2D(500, 600), "uhh, dirt?", "bingus"));

    fdb.updateEntry(new Node(9998, new Point2D(777, 777), "tile", "bingus"));

    fdb.nodeTable.undoChange();

    // set up database connection and nodeTable,  edgeTable variables

    // Fdb fdb = new Fdb();
    // fdb.loadTablesFromCSV();

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
