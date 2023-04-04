package edu.wpi.fishfolk.database;

import lombok.Getter;

public class DataEdit {

  @Getter private final String id;
  @Getter private final String header;
  @Getter private final String value;

  public DataEdit(String id, String header, String value) {
    this.id = id;
    this.header = header;
    this.value = value;
  }
}
