package edu.wpi.fishfolk.database;

public class DataEdit {

  public String id;
  public String attr;
  public String value;
  public boolean valid;

  public DataEdit(String id, String attr, String value, boolean valid) {
    this.id = id;
    this.attr = attr;
    this.value = value;
    this.valid = valid;
  }
}
