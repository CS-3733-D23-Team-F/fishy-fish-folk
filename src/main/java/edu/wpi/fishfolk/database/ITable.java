package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public interface ITable {

  /**
   * Setter for both headers. Note that this does not modify the database, use addHeaders instead.
   *
   * @param headers
   * @param headerTypes
   */
  void setHeaders(ArrayList<String> headers, ArrayList<String> headerTypes);

  /**
   * Add the provided ArrayLists as headers to this Table with the corresponding data type. Specify
   * the Java type of each corresponding header. Currently supported types: String, int, double.
   *
   * @param headers
   * @param headerTypes
   * @return true if the headers were successfully added, otherwise false
   */
  boolean addHeaders(ArrayList<String> headers, ArrayList<String> headerTypes);

  /**
   * Get the row where the given attribute matches the given value.
   *
   * @param pkey the primary key to use
   * @param id the unique id to search for
   * @return the entire row where the primary key matches the id
   */
  ArrayList<String> get(String pkey, String id);

  /**
   * Get the row where the given attribute matches the given value.
   *
   * @param pkey the primary key to use
   * @param id the unique id to search for
   * @param attr the attribute to return
   * @return the value of the attribute in the row where the primary key matches the id
   */
  String get(String pkey, String id, String attr);

  /**
   * Get an entire column matching a given header.
   *
   * @param header the column to get
   * @return the column as an ArrayList<String>
   */
  ArrayList<String> getColumn(String header);

  /**
   * Get all the rows in this table.
   *
   * @return an ArrayList<String[]> where each element corresponds to a row in the Table.
   */
  ArrayList<String[]> getAll();

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
   * Update the attribute of the TableEntry where id matches the primary key header provided.
   *
   * @param pkey the header of the primary key to use
   * @param id unique identifier of the row to update
   * @param attr attribute to update
   * @param value new value
   * @return true if successfully updated, otherwise false.
   */
  boolean update(String pkey, String id, String attr, String value);

  /**
   * Update the table from a DataEdit object.
   *
   * @param edit the DataEdit object describing the edit.
   * @return true if the successfully updated, otherwise false.
   */
  boolean update(DataEdit edit);

  /**
   * Remove the TableEntry where the attribute matches the given value.
   *
   * @param pkey the primary key to use
   * @param id the unique id of the row to delete
   */
  boolean remove(String pkey, String id);

  /**
   * Check if the provided id exists in the table.
   *
   * @param pkey the primary key to us
   * @param id the unique id of the row to search for
   * @return true if a row in the table matches the provided id, otherwise false
   */
  boolean exists(String pkey, String id);

  /** @return the number of rows in the Table */
  int size();

  /**
   * Execute a SQL query.
   *
   * @param selection a valid SQL fragment of the form "SELECT ..."
   * @param condition a valid SQL fragment describing which rows to select. Can be empty if no
   *     condition is needed.
   * @return an ArrayList where each entry is a String[] corresponding to one row of the result set.
   */
  ArrayList<String[]> executeQuery(String selection, String condition);

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
