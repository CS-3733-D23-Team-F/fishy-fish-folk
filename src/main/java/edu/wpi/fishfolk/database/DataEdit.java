package edu.wpi.fishfolk.database;

public abstract class DataEdit {

  public String pkey;
  public String id;

  public DataEditTable table;
  public DataEditType type;

  public DataEdit(DataEditTable table, String pkey, String id, DataEditType type) {

    this.pkey = pkey;
    this.id = id;

    this.table = table;
    this.type = type;
  }
}
