package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public interface ITable {

  void setHeaders(ArrayList<String> headers, ArrayList<String> headerTypes);

  /**
   * Add the provided ArrayLists as headers and header types to this Table. Specify the Java type of
   * each corresponding header. Currently supported types: String, int, double.
   *
   * @param headers
   * @param headerTypes
   * @return true if the headers were successfully added, otherwise false
   */
  boolean addHeaders(ArrayList<String> headers, ArrayList<String> headerTypes);

  /**
   * Get the row where the given attribute matches the given value.
   *
   * @param attr the attribute to search for
   * @param value the value to search for
   * @return the entire row where the attribute matches the value
   */
  ArrayList<String> get(String attr, String value);

  /**
   * Get all the rows in this table.
   *
   * @return an array of ArrayList<String> where each element corresponds to a row. The first
   *     element (index 0) contains the headers.
   */
  ArrayList<String>[] getAll();

  /**
   * Get the number of rows in the Table.
   *
   * @return
   */
  int size();

  /**
   * Insert the provided TableEntry into the Table.
   *
   * @param tableEntry
   * @return true if added successfully, otherwise false.
   */
  boolean insert(TableEntry tableEntry);

  /**
   * Update the TableEntry matching the id of the provided TableEntry with the provided data.
   *
   * @param tableEntry
   * @return true if successfully updated, otherwise false.
   */
  boolean update(TableEntry tableEntry);

  /**
   * Update the attribute of the TableEntry matching the provided id.
   *
   * @param id id of the TableEntry to update
   * @param attr attribute to update
   * @param value new value
   * @return true if successfully updated, otherwise false.
   */
  boolean update(String id, String attr, String value);

  /**
   * Remove the TableEntry where the attribute matches the given value.
   *
   * @param attr the attribute to search for
   * @param value the value to search for
   */
  void remove(String attr, String value);

  /**
   * Check if the provided id exists in the table.
   *
   * @param id
   * @return true if a row in the table matches the provided id, otherwise false
   */
  boolean exists(String id);

  /**
   * Overwrite the Table with the data found at the provided filepath. Make sure the filepath leads
   * to a CSV file, not just a folder.
   *
   * @param filepath the CSV file to read from
   * @param backup set to true if the current data should get backed up before importing, false if
   *     not
   */
  void importCSV(String filepath, boolean backup);

  /**
   * Save the Table to a CSV into the folder pointed to by the provided filepath. The name of the
   * CSV file will contain the name of the table and time of export in the following format:
   * tableName_yy-MM-dd HH:mm:ss.
   *
   * @param filepath folder to save the CSV to
   */
  void exportCSV(String filepath);
}