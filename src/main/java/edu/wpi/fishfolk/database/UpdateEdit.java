package edu.wpi.fishfolk.database;

public class UpdateEdit extends DataEdit {

  String attribute, value;

  public UpdateEdit(DataEditTable table, String pkey, String id, String attr, String val) {

    super(table, pkey, id, DataEditType.UPDATE);
    this.attribute = attr;
    this.value = val;
  }
}
