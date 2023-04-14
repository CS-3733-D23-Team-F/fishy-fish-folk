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

    // Add data edit to queue
    dataEditQueue.add(dataEdit);

    // Count the entry if required
    if (countEntry) editCount += 1;

    // Return if the batch limit has been reached
    return (editCount >= batchLimit);
  }

  /** Remove the most recently added data edit from the queue. Modifies the pointer if necessary. */
  public void removeRecent() {

    // If the size of the queue matches the pointer, decrease the pointer to stay in bounds
    if (pointer == (dataEditQueue.size() - 1)) pointer -= 1;

    // Remove the most recently added item to the queue
    dataEditQueue.remove(dataEditQueue.get(dataEditQueue.size() - 1));

    // Decrease the edit count
    editCount -= 1;
  }

  /**
   * Remove the most recently added data edit from the queue and return its value. Modifies the
   * pointer if necessary.
   *
   * @return The most recently added data edit
   */
  public DataEdit<T> popRecent() {

    // Get the most recently added data edit in the queue
    DataEdit<T> edit = dataEditQueue.get(dataEditQueue.size() - 1);

    // Remove it from the queue
    removeRecent();

    // Return it
    return edit;
  }

  /**
   * Returns the next DataEdit at the pointer and increments the pointer.
   *
   * @return DataEdit at pointer.
   */
  public DataEdit<T> next() {

    // Increment the pointer (data edit is being sent to database)
    pointer += 1;

    // Return the next data edit in the queue
    return dataEditQueue.get(pointer - 1);
  }

  /**
   * Checks if there is another DataEdit in the queue before the pointer.
   *
   * @return True if there is, false otherwise
   */
  public boolean hasNext() {

    try {

      // Try to get the data edit at index pointer
      dataEditQueue.get(pointer);

      // If it exists, return true
      return true;

    } catch (IndexOutOfBoundsException e) {

      // If it does not exist, return false
      return false;
    }
  }
}
