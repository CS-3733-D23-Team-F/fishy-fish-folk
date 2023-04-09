package edu.wpi.fishfolk.database;

public class RemoveEdit extends DataEdit {

  public RemoveEdit(DataTableType table, String pkey, String id) {

    super(table, pkey, id, DataEditType.REMOVE);
  }
}
