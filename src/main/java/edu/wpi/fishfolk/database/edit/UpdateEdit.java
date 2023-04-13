package edu.wpi.fishfolk.database.edit;

import edu.wpi.fishfolk.database.DataTableType;

public class UpdateEdit extends DataEdit {

  public String attribute, value;

  public UpdateEdit(DataTableType table, String pkey, String id, String attr, String val) {

    super(table, pkey, id, DataEditType.UPDATE);
    this.attribute = attr;
    this.value = val;
  }
}
