package edu.wpi.fishfolk.database.edit;

import java.util.ArrayList;

public class InsertEdit extends DataEdit {

  public ArrayList<String> data;

  public InsertEdit(DataTableType table, String pkey, String id, ArrayList<String> data) {

    super(table, pkey, id, DataEditType.INSERT);
    this.data = data;
  }
}
