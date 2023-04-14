package edu.wpi.fishfolk.database.rewrite.DataEdit;

import lombok.Getter;

public class DataEdit<T> {

  @Getter private final T oldEntry;
  @Getter private final T newEntry;
  //  @Getter private final String header;
  //  @Getter private final Object value;
  @Getter private final DataEditType type;

  //  /**
  //   * Representation of an edit of a single attribute or entire entry in a table.
  //   *
  //   * @param header Header of attribute in entry to edit
  //   * @param value New value of selected attribute
  //   * @param type Type of data edit (insert, update, remove)
  //   */
  //  public DataEdit(String header, Object value, DataEditType type) {
  //    this.entry = null;
  //    this.header = header;
  //    this.value = value;
  //    this.type = type;
  //  }

  /**
   * Representation of an edit of a single attribute or entire entry in a table.
   *
   * @param oldEntry Table entry receiving an edit
   * @param newEntry Table entry to perform the edit with
   * @param type Type of data edit (insert, update, remove)
   */
  public DataEdit(T oldEntry, T newEntry, DataEditType type) {
    this.oldEntry = oldEntry;
    this.newEntry = newEntry;
    //    this.header = null;
    //    this.value = null;
    this.type = type;
  }

  /**
   * Representation of an edit of a single attribute or entire entry in a table.
   *
   * @param newEntry Table entry to perform the edit with
   * @param type Type of data edit (insert, update, remove)
   */
  public DataEdit(T newEntry, DataEditType type) {
    this.oldEntry = null;
    this.newEntry = newEntry;
    //    this.header = null;
    //    this.value = null;
    this.type = type;
  }
}
