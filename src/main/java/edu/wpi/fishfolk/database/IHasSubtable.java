package edu.wpi.fishfolk.database;

import java.time.LocalDateTime;
import java.util.List;

public interface IHasSubtable<T> {

  /**
   * Checks for the existence of the proper subtable, and creates one if none exist.
   *
   * @param drop Choose whether to drop the subtable and create a new one on init
   */
  void initSubtable(boolean drop);

  /**
   * Queries the table to determine the key used in the main table column tied to the subtable.
   *
   * @param requestID The LocalDateTime ID of the food order
   * @return The ID for this food order's items in the subtable
   */
  int getSubtableItemsID(LocalDateTime requestID);

  /**
   * Gets a list of items in the subtable with matching subtableIDs.
   *
   * @param subtableID ID in the subtable to query for
   * @return A list of all the items with a matching subtableID
   */
  List<T> getSubtableItems(int subtableID);

  /**
   * Reset the items in the subtable for a specified request to a given list of "new" items.
   *
   * @param requestID ID of the request to do the replacement for
   * @param items List of items to do the replacement with
   */
  void setSubtableItems(LocalDateTime requestID, List<T> items);

  /**
   * Deletes all items in the subtable for a given request.
   *
   * @param requestID ID of the request to remove subtable items from
   */
  void deleteAllSubtableItems(LocalDateTime requestID);
}
