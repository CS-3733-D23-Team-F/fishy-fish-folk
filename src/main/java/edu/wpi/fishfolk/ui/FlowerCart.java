package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class FlowerCart {
  @Getter private List<FlowerCart.quantityItem> items;
  @Getter private float totalPrice;

  /** Creates an empty cart */
  public FlowerCart() {
    items = new ArrayList<FlowerCart.quantityItem>();
    totalPrice = 0;
  }

  /**
   * allows for tracking of quantity of a menu item rather than a food item (which lacks other
   * necessary features)
   */
  public static class quantityItem {
    @Getter FlowerMenuItem item;
    @Getter byte quantity;

    /**
     * Creates a quantity item to track the quantity of a given item
     *
     * @param i the item to track the quantity of
     */
    quantityItem(FlowerMenuItem i) {
      item = i;
      quantity = 1;
    }
  }

  /**
   * adds an item to the cart
   *
   * @param item the item to add
   */
  public void add(FlowerMenuItem item) {
    for (FlowerCart.quantityItem qi : items) {
      if (qi.item == item) {
        qi.quantity++;
        totalPrice += item.getPrice();
        return;
      }
    }
    items.add(new quantityItem(item));
    totalPrice += item.getPrice();
  }

  /**
   * removes an item from the cart, with no effect if the item is not in the cart
   *
   * @param item the item to remove
   * @return whether the item was removed (typically false if the item was not in the cart)
   */
  public boolean remove(FlowerMenuItem item) {
    for (FlowerCart.quantityItem qi : items) {
      if (qi.item == item) {
        qi.quantity--;
        totalPrice -= item.getPrice();
        if (qi.quantity == 0) items.remove(qi);
        return true;
      }
    }
    return false;
  }

  /**
   * Gets all of the items in the cart as a list of FoodItem
   *
   * @return The List of items in the cart
   */
  public List<FlowerItem> getSubmittableItems() {
    List<FlowerItem> returnable = new ArrayList<FlowerItem>();
    for (FlowerCart.quantityItem qi : items) {
      returnable.add(new FlowerItem(qi.item.getName(), qi.quantity, qi.item.getPrice()));
    }
    return returnable;
  }

  /**
   * removes all instances of a given item from the cart
   *
   * @param item the item to remove all of
   */
  public void removeAll(FlowerMenuItem item) {
    while (remove(item)) ; // wtf is this, I love it
  }
}
