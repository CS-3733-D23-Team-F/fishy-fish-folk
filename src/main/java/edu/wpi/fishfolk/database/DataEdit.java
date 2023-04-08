package edu.wpi.fishfolk.database;

public abstract class DataEdit {

  public String pkey;
  public DataEditType type;

  public DataEdit(String pkey, DataEditType type) {
    this.pkey = pkey;
    this.type = type;
  }
}
