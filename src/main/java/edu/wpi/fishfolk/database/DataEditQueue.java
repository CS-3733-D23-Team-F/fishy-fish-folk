package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.DataEdit.DataEdit;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class DataEditQueue<T> {

  private final ArrayList<DataEdit<T>> dataEditQueue = new ArrayList<>();

  @Getter @Setter private int pointer;
  @Setter private int editCount;
  @Getter private final int batchLimit;

  /** Represents a queue of data edits for a PostgreSQL database. */
  public DataEditQueue() {
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

    // Check if queue is empty
    if (dataEditQueue.isEmpty()) {
      return;
    }

    // If the size of the queue matches the pointer, decrease the pointer to stay in bounds
    if (pointer == (dataEditQueue.size() - 1)) pointer -= 1;

    // Remove the last element added
    dataEditQueue.remove(dataEditQueue.size() - 1);

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

    // Check if queue is empty
    if (dataEditQueue.isEmpty()) {
      return null;
    }

    // if needed decrease pointer to stay in bounds
    if (pointer == dataEditQueue.size() - 1) pointer--;

    editCount--;

    // remove and return the most recently added element in the queue
    return dataEditQueue.remove(dataEditQueue.size() - 1);
  }

  /**
   * Returns the next DataEdit at the pointer and increments the pointer.
   *
   * @return DataEdit at pointer.
   */
  public DataEdit<T> next() {

    // return the element at the pointer prior to incrementing
    return dataEditQueue.get(pointer++);
  }

  /**
   * Checks if there is another DataEdit in the queue before the pointer.
   *
   * @return True if there is, false otherwise
   */
  public boolean hasNext() {
    return pointer < dataEditQueue.size();
  }
}
