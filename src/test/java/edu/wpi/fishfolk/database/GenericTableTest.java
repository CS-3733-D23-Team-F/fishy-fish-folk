package edu.wpi.fishfolk.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

class GenericTableTest {

  @Test
  public void test() {

    Fdb dbConnection = new Fdb();
    try {
      dbConnection.db.setSchema("test");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    Table testNodeTable = new Table(dbConnection.db, "test_node_table", true);
    // dbConnection.createTable(dbConnection.db, testNodeTable.getTableName());

    testNodeTable.addHeaders(
        new ArrayList<>(List.of("id", "x", "y", "floor", "bldg", "type", "lname", "sname")),
        new ArrayList<>(
            List.of(
                "String", "double", "double", "String", "String", "String", "String", "String")));

    // imports data correctly. commented to avoid "id already in table" errors
    testNodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv");

    // gets data correctly
    System.out.println(testNodeTable.get("CHALL001L1"));

    // correctly returns null
    System.out.println(testNodeTable.get("yummmy"));

    Node newNode = new Node(1, new Point2D(-1, -1), "f", "b", NodeType.HALL, "ln", "sn");
    testNodeTable.insert(newNode);

    // correctly prints out the values
    System.out.println(testNodeTable.get(Integer.toString(1)));

    // correctly null
    System.out.println(testNodeTable.get(Integer.toString(2)));

    // correctly print out true and false respectively
    testNodeTable.exists(Integer.toString(1));
    testNodeTable.exists(Integer.toString(2));

    // correctly updates floor attribute
    newNode.floor = "newf";
    testNodeTable.update(newNode);
    System.out.println(testNodeTable.get(newNode.id));

    // correctly updates bldg attribute
    testNodeTable.update(newNode.id, "bldg", "newb");
    System.out.println(testNodeTable.get(newNode.id));

    // correctly removes entry from table
    testNodeTable.remove(newNode.id);
    testNodeTable.exists(newNode.id);

    testNodeTable.exportCSV("src/main/resources/edu/wpi/fishfolk/csv/");
  }
}
