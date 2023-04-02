package edu.wpi.fishfolk.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    Table testNodeTable = new Table(dbConnection.db, "test_node_table");
    dbConnection.createTable(dbConnection.db, testNodeTable.getTableName());

    testNodeTable.addHeaders(
        new ArrayList<>(List.of("id", "x", "y", "floor", "bldg", "type", "lname", "sname")),
        new ArrayList<>(
            List.of(
                "String", "double", "double", "String", "String", "String", "String", "String")));

    // imports data correctly. commented to avoid "id already in table" errors
    // testNodeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/L1Nodes.csv");

    //gets data correctly
    System.out.println(testNodeTable.get("CHALL001L1"));

    //correctly returns null
    System.out.println(testNodeTable.get("yummmy"));
  }
}
