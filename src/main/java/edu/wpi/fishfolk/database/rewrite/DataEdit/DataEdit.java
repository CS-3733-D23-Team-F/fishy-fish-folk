package edu.wpi.fishfolk.database.rewrite.DataEdit;

import lombok.Getter;

public class DataEdit<T> {

  @Getter private final T entry;
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
   * @param entry Table entry receiving an edit (can be new)
   * @param type Type of data edit (insert, update, remove)
   */
  public DataEdit(T entry, DataEditType type) {
    this.entry = entry;
    //    this.header = null;
    //    this.value = null;
    this.type = type;
  }
}
