package edu.wpi.fishfolk.database.rewrite;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

public class DataEditQueue<T> {

  private final LinkedList<DataEdit<T>> dataEditQueue;

  private int pointer;
  @Setter private int editCount;
  @Getter private final int batchLimit;

  /** Represents a queue of data edits for a PostgreSQL database. */
  public DataEditQueue() {
    this.dataEditQueue = new LinkedList<>();
    this.pointer = 0;
    this.editCount = 0;
    this.batchLimit = 4;
  }

  /**
   * Add a data edit to the queue.
   *
   * @param dataEdit DataEdit to add
   * @param countEntry True if entry is counted towards batch limit, false if uncounted.
   * @return True if a batch update is required, false otherwise
   */
  public boolean add(DataEdit<T> dataEdit, boolean countEntry) {
    dataEditQueue.add(dataEdit);
    if (countEntry) editCount += 1;

    return (editCount >= batchLimit);
  }

  /** Remove the most recently added data edit from the queue. Modifies the pointer if necessary. */
  public void removeRecent() {
    if (pointer == (dataEditQueue.size() - 1)) pointer -= 1;
    dataEditQueue.remove(dataEditQueue.get(dataEditQueue.size() - 1));
    editCount -= 1;
  }

  /**
   * Remove the most recently added data edit from the queue and return its value. Modifies the
   * pointer if necessary.
   *
   * @return The most recently added data edit
   */
  public DataEdit<T> popRecent() {
    if (pointer == (dataEditQueue.size() - 1)) pointer -= 1;
    DataEdit<T> edit = dataEditQueue.get(dataEditQueue.size() - 1);
    removeRecent();
    return edit;
  }

  /**
   * Returns the next DataEdit at the pointer and increments the pointer.
   *
   * @return DataEdit at pointer.
   */
  public DataEdit<T> next() {
    pointer += 1;
    return dataEditQueue.get(pointer - 1);
  }

  /**
   * Checks if there is another DataEdit in the queue before the pointer.
   *
   * @return True if there is, false otherwise
   */
  public boolean hasNext() {
    try {
      dataEditQueue.get(pointer);
      return true;
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
  }
}
