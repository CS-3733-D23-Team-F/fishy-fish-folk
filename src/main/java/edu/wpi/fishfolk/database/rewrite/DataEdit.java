package edu.wpi.fishfolk.database.rewrite;

import lombok.Getter;

public class DataEdit<T> {

  @Getter private final T entry;
  @Getter private final String header;
  @Getter private final Object value;

  /**
   * Representation of an edit of a single attribute or entire entry in a table.
   *
   * @param entry Table entry receiving an edit (can be new)
   * @param header Header of attribute in entry to edit
   * @param value New value of selected attribute
   */
  public DataEdit(T entry, String header, Object value) {
    this.entry = entry;
    this.header = header;
    this.value = value;
  }
}
