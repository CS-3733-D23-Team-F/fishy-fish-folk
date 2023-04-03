package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.Fdb;
import edu.wpi.fishfolk.database.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    /*

    commented everything out since this code creates the tables
    and thus doenst have to run multiple times


    Fdb fdb = new Fdb();
    try {
      fdb.db.setSchema("proto2db");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    NodeTable nodeTable = new NodeTable(fdb.db, "nodes");
    nodeTable.importCSV(
        "src/main/resources/edu/wpi/fishfolk/csv/MicroNode.csv",
        "src/main/resources/edu/wpi/fishfolk/csv/Location.csv",
        "src/main/resources/edu/wpi/fishfolk/csv/Move.csv");

    Table edgeTable = new Table(fdb.db, "edge");
    edgeTable.init();
    edgeTable.addHeaders(
        new ArrayList<>(List.of("node1", "node2")), new ArrayList<>(List.of("String", "String")));
    edgeTable.importCSV("src/main/resources/edu/wpi/fishfolk/csv/Edge.csv");

     */
  }
}
