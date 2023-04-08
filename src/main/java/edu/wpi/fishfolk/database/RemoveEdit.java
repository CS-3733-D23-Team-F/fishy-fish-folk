package edu.wpi.fishfolk.database;

public class RemoveEdit extends DataEdit {

  public RemoveEdit(String pkey) {
    super(pkey, DataEditType.REMOVE);
  }
}
