package edu.wpi.fishfolk.database;

public interface ICSVWithSubtable {

  /**
   * Imports data from a CSV file, overwriting the data stored in the table.
   *
   * @param tableFilepath Filepath of the main table
   * @param subtableFilepath Filepath of the subtable
   * @param backup Backup the current db to a CSV?
   * @return True on success, false otherwise
   */
  boolean importCSV(String tableFilepath, String subtableFilepath, boolean backup);

  /**
   * Exports the entire table to a CSV file.
   *
   * @param directory The directory to place the CSV file.
   * @return True on successful export, false otherwise.
   */
  boolean exportCSV(String directory);
}
