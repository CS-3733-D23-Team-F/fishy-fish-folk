package edu.wpi.fishfolk.database;

import lombok.Getter;

public class DataEdit {

  @Getter private final int id;
  @Getter private final String header;
  @Getter private final String value;

  public DataEdit(int id, String header, String value) {
    this.id = id;
    this.header = header;
    this.value = value;
  }
}
