package edu.wpi.fishfolk.database;

import lombok.Getter;

public class DataEdit {

  @Getter private final int nodeid;
  @Getter private final String header;
  @Getter private final String value;

  public DataEdit(int nodeid, String header, String value) {
    this.nodeid = nodeid;
    this.header = header;
    this.value = value;
  }
}
