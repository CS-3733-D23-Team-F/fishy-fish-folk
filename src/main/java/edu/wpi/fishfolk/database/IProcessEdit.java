package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;

public interface IProcessEdit {

  /**
   * Process a data edit.
   *
   * @param edit
   */
  void processEdit(DataEdit<Object> edit);
}
