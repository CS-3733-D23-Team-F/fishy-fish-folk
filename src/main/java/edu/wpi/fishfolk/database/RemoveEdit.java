package edu.wpi.fishfolk.database;

public class RemoveEdit extends DataEdit {

  public RemoveEdit(DataEditTable table, String pkey, String id) {

    super(table, pkey, id, DataEditType.REMOVE);
  }
}
