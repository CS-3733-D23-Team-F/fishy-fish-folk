package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public class InsertEdit extends DataEdit {

  ArrayList<String> data;

  public InsertEdit(String pkey, ArrayList<String> data) {
    super(pkey, DataEditType.INSERT);
    this.data = data;
  }
}
