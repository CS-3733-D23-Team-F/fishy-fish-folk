package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public interface ITable {

  boolean addHeaders(ArrayList<String> headers, ArrayList<String> headerTypes);

  ArrayList<String> get(String id);

  boolean insert(TableEntry tableEntry);

  boolean update(TableEntry tableEntry);

  void remove(TableEntry tableEntry);

  void importCSV(String filepath);

  void exportCSV(String filepath);
}
