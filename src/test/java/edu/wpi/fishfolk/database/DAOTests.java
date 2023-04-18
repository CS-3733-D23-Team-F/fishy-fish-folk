//package edu.wpi.fishfolk.database;
//
//import edu.wpi.fishfolk.database.rewrite.Fdb;
//import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;
//import javafx.geometry.Point2D;
//import org.junit.jupiter.api.Test;
//
//public class DAOTests {
//
//    @Test
//    public void nodeDAOTest() {
//
//        Fdb t
//                  fdb.insertEntry(new Node(9997, new Point2D(100, 200), "carpet", "bingus"));
//                  fdb.insertEntry(new Node(9998, new Point2D(300, 400), "tile", "bingus"));
//                  fdb.insertEntry(new Node(9999, new Point2D(500, 600), "uhh, dirt?", "bingus"));
//
//                  fdb.updateEntry(new Node(9998, new Point2D(777, 777), "tile", "bingus"));
//
//                  System.out.println("[Main]: " + fdb.getEntry(9998, TableEntryType.NODE));
//                  System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.NODE));
//
//                  fdb.undoChange(TableEntryType.NODE);
//                  fdb.undoChange(TableEntryType.NODE);
//
//                  fdb.updateEntry(new Node(9997, new Point2D(1999, 1999), "carpet", "bingus"));
//
//                  fdb.removeEntry(9998, TableEntryType.NODE);
//                }
//
//                // Test LocationDAO
//                if (false) {
//                  fdb.insertEntry(new Location(".Loooong name", "Short name", NodeType.INFO));
//                  fdb.insertEntry(new Location(".Bingus Tower III", "Bingus", NodeType.DEPT));
//                  fdb.insertEntry(new Location(".Joseph Biden", "Joe", NodeType.INFO));
//
//                  fdb.updateEntry(new Location(".Bingus Tower III", "BINGUSSSSSSSS", NodeType.DEPT));
//
//                  System.out.println("[Main]: " + fdb.getEntry(".Bingus Tower III",
//             TableEntryType.LOCATION));
//                  System.out.println("[Main]: " + fdb.getAllEntries(TableEntryType.LOCATION));
//
//                  fdb.undoChange(TableEntryType.LOCATION);
//                  fdb.undoChange(TableEntryType.LOCATION);
//
//                  fdb.updateEntry(new Location(".Loooong name", "This name is SHORT", NodeType.INFO));
//
//                  fdb.removeEntry(".Bingus Tower III", TableEntryType.LOCATION);
//
//                  fdb.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/", TableEntryType.NODE);
//    }
//}
