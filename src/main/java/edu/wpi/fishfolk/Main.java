package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.rewrite.Fdb;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
import javafx.geometry.Point2D;

public class Main {

  public static void main(String[] args) {

    // Fapp.launch(Fapp.class, args); // run ui

    Fdb fdb = new Fdb();

    fdb.insertEntry(new Node(9997, new Point2D(100, 200), "carpet", "bingus"));
    fdb.insertEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));
    fdb.insertEntry(new Node(9999, new Point2D(500, 600), "uhh, dirt?", "bingus"));

    fdb.updateEntry(new Node(9998, new Point2D(777, 777), "tile", "bingus"));
    fdb.nodeTable.undoChange();
    fdb.nodeTable.undoChange();

    fdb.updateEntry(new Node(9997, new Point2D(1999, 1999), "carpet", "bingus"));

    fdb.removeEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));

    fdb.nodeTable.updateDatabase();
  }
}
