package edu.wpi.fishfolk.database.rewrite;

import java.util.ArrayList;

public interface IDAO<T> {

  /** Take all entries from a PostgreSQL database and populate a local table. */
  void populateLocalTable();

  /**
   * Insert an entry into a local table.
   *
   * @param entry Entry to insert
   * @return True on successful insertion, false otherwise
   */
  boolean insertEntry(T entry);

  /**
   * Update an entry in a local table.
   *
   * @param entry Entry to update
   * @return True on successful update, false otherwise
   */
  boolean updateEntry(T entry);

  /**
   * Remove an entry from a local table.
   *
   * @param entry Entry to remove
   * @return True on successful removal, false otherwise
   */
  boolean removeEntry(T entry);

  /**
   * Retrieve an entry from a local table.
   *
   * @param identifier The entry's identifier (i.e. ID number)
   * @return The retrieved entry, null on failure
   */
  T getEntry(String identifier);

  /**
   * Retrieve a list of all entries in a local table.
   *
   * @return A list of retrieved entries, null on failure
   */
  ArrayList<T> getAllEntries();

  /** Undo a change to a local table. */
  void undoChange();

  /**
   * Updates PostgreSQL database to reflect staged edits.
   *
   * @return True on successful update, false otherwise
   */
  boolean updateDatabase();
}
