package edu.wpi.fishfolk.database.DataEdit;

import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.util.NodeType;
import lombok.Getter;
import lombok.Setter;

public class DataEdit<T> {

  @Getter private final T oldEntry;
  @Getter private final T newEntry;
  @Getter private final DataEditType type;
  @Getter private final TableEntryType table;

  /**
   * Representation of an entry edit in a table.
   *
   * @param oldEntry Table entry receiving an edit
   * @param newEntry Table entry to perform the edit with
   * @param type Type of data edit (insert, update, remove)
   */
  public DataEdit(T oldEntry, T newEntry, DataEditType type) {
    this.oldEntry = oldEntry;
    this.newEntry = newEntry;
    this.type = type;
    this.table = null;
  }

  /**
   * Representation of an entry edit in a table.
   *
   * @param newEntry Table entry to perform the edit with
   * @param type Type of data edit (insert, update, remove)
   */
  public DataEdit(T newEntry, DataEditType type) {
    this.oldEntry = newEntry;
    this.newEntry = newEntry;
    this.type = type;
    this.table = null;
  }
  public DataEdit(T newEntry, DataEditType type, TableEntryType table) {
    this.oldEntry = newEntry;
    this.newEntry = newEntry;
    this.type = type;
    this.table = table;
  }
}
