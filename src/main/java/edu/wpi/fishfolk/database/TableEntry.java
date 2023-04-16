package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public abstract class TableEntry {

  public String id;

  public TableEntry() {};

  public abstract boolean construct(ArrayList<String> data);

  public abstract ArrayList<String> deconstruct();

  @Override
  public boolean equals(Object o) {
    TableEntry other = (TableEntry) o;
    return id.equals(other.id);
  }
}
