package edu.wpi.fishfolk.database.rewrite;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import java.util.LinkedList;

public class DataEditQueue<T> {

  private final LinkedList<DataEdit<T>> dataEditQueue;
  private int pointer;

  /** Represents a queue of data edits for a PostgreSQL database. */
  public DataEditQueue() {
    this.dataEditQueue = new LinkedList<>();
    this.pointer = 0;
  }

  /**
   * Add a data edit to the queue.
   *
   * @param dataEdit DataEdit to add
   */
  public void add(DataEdit<T> dataEdit) {
    dataEditQueue.add(dataEdit);
  }

  /** Remove the most recently added data edit from the queue. Modifies the pointer if necessary. */
  public void removeRecent() {
    if (pointer == (dataEditQueue.size() - 1)) pointer -= 1;
    dataEditQueue.remove(dataEditQueue.get(dataEditQueue.size() - 1));
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
