package edu.wpi.fishfolk.database;

public interface ICSVNoSubtable {

  /**
   * Imports data from a CSV file, overwriting the data stored in the table.
   *
   * @param tableFilepath The CSV file to read from
   * @param backup True if a backup should get saved before overwriting.
   * @return True on succesful import, false otherwise.
   */
  boolean importCSV(String tableFilepath, boolean backup);

  /**
   * Exports the entire table to a CSV file.
   *
   * @param directory The directory to place the CSV file.
   * @return True on successful export, false otherwise.
   */
  boolean exportCSV(String directory);
}
