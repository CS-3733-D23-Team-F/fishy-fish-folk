package edu.wpi.fishfolk;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class FoodOrder {
  public LinkedList<FoodItem> items;
  public LocalDateTime deliveryTime;
  public CreditCardInfo payer;
  public Room deliveryLocation;
  public float totalPrice;

  public FoodOrder() {
    items = new LinkedList<FoodItem>();
    deliveryTime = LocalDateTime.now();
    payer = null;
    deliveryLocation = null;
  }

  public void addItem(FoodItem toAdd) {
    items.add(toAdd);
    totalPrice += toAdd.price;
  }

  public void removeItem(FoodItem toRemove) {
    if (items.contains(toRemove)) {
      items.remove(toRemove);
      totalPrice -= toRemove.price;
    }
  }

  public boolean submit() {
    if (deliveryLocation == null
        || payer == null
        || LocalDateTime.now().isAfter(deliveryTime)
        || items.isEmpty()) return false;
    // send order to whatever aggregate of orders we have, I suspect this is a DB task later
    return true;
  }
}
