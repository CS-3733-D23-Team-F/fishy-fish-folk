package edu.wpi.fishfolk.database;

public class UpdateEdit extends DataEdit {

  String attribute, value;

  public UpdateEdit(String pkey, String attr, String val) {
    super(pkey, DataEditType.INSERT);
    this.attribute = attr;
    this.value = val;
  }
}
