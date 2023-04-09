package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public class InsertEdit extends DataEdit {

  ArrayList<String> data;

  public InsertEdit(DataEditTable table, String pkey, String id, ArrayList<String> data) {

    super(table, pkey, id, DataEditType.INSERT);
    this.data = data;
  }
}
