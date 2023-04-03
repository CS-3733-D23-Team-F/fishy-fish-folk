package edu.wpi.fishfolk.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

public class FoodOrder {
  public LinkedList<FoodItem> items;
  public LocalDateTime deliveryTime;
  public CreditCardInfo payer;
  public Room deliveryLocation;
  public float totalPrice;

  public formStatus formStatus;
  public String formID;

  public FoodOrder() {
    items = new LinkedList<FoodItem>();
    deliveryTime = LocalDateTime.now();
    payer = null;
    deliveryLocation = null;
    formStatus = edu.wpi.fishfolk.ui.formStatus.notSubmitted;
    this.formID = formID;
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
    // this will be a print for now
    System.out.println("Submitted order:\n");
    System.out.println(this);
    return true;
  }

  public String toString() {
    ArrayList<quantityItem> quantityItems = new ArrayList<quantityItem>();
    LinkedList<FoodItem> itemsClone = (LinkedList<FoodItem>) items.clone();
    while (!itemsClone.isEmpty()) {
      FoodItem currentItem = itemsClone.remove(0);
      quantityItem current = new quantityItem(currentItem);
      while (itemsClone.remove(currentItem)) {
        current.add();
      }
      quantityItems.add(current);
    }
    String[] itemStrings = new String[quantityItems.size()];
    for (int i = 0; i < itemStrings.length; i++) {
      itemStrings[i] = quantityItems.get(i).toString();
    }
    String items = String.join("\n", itemStrings);

    String full =
        items
            + "\nTotal cost: "
            + String.format("%.2f", totalPrice)
            + "\nTo "
            + deliveryLocation.toString()
            + "\non "
            + deliveryTime.format(DateTimeFormatter.ofPattern("EE, MM/dd"))
            + " at "
            + deliveryTime.format(DateTimeFormatter.ofPattern("hh:mma"))
            + "\nCharging card of "
            + payer.nameOnCard
            + " ending in "
            + ("" + payer.cardNum).substring(12);
    return full;
  }

  private static class quantityItem {
    FoodItem item;
    int quantity;

    public quantityItem(FoodItem thing) {
      item = thing;
      quantity = 1;
    }

    public float totalPrice() {
      return item.price * quantity;
    }

    public String toString() {
      return quantity + " " + item.itemName;
    }

    public void add() {
      quantity++;
    }
  }

  public void setSubmitted() {
    formStatus formStatus = edu.wpi.fishfolk.ui.formStatus.submitted;
  }

  public void setCancelled() {
    formStatus formStatus = edu.wpi.fishfolk.ui.formStatus.cancelled;
  }

  public void setFilled() {
    formStatus formStatus = edu.wpi.fishfolk.ui.formStatus.filled;
  }
}
