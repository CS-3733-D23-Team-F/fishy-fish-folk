package edu.wpi.fishfolk.database.edit;

import edu.wpi.fishfolk.database.DataTableType;

public abstract class DataEdit {

  public String pkey;
  public String id;

  public DataTableType table;
  public DataEditType type;

  public DataEdit(DataTableType table, String pkey, String id, DataEditType type) {

    this.pkey = pkey;
    this.id = id;

    this.table = table;
    this.type = type;
  }
}
