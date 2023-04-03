package edu.wpi.fishfolk.ui;

import edu.wpi.fishfolk.database.TableEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class FoodOrder extends TableEntry {
  private static String[] headersArray = {"ID", "Items", "Status", "Assignee", "Room", "Time"};
  public static ArrayList<String> headers = new ArrayList<String>(Arrays.asList(headersArray));
  public LinkedList<FoodItem> items;
  public LocalDateTime deliveryTime;
  public CreditCardInfo payer;
  public Room deliveryLocation;
  public float totalPrice;

  public FormStatus formStatus;
  public String formID;
  public String assignee;

  public FoodOrder() {
    items = new LinkedList<FoodItem>();
    deliveryTime = LocalDateTime.now();
    payer = null;
    deliveryLocation = null;
    formStatus = FormStatus.notSubmitted;
    formID = "" + System.currentTimeMillis();
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

  public boolean construct(ArrayList<String> data) {
    if (data.size() != 6) {
      return false;
    }
    formID = data.get(0);
    String[] itemsArray = data.get(1).split("-_-");
    DB_ITEMS: for (String s : itemsArray) {
      String itemName = s.substring(0, s.lastIndexOf(' '));
      float price = Float.parseFloat(s.substring(s.lastIndexOf(' ') + 1));
      for (FoodItem f : items) {
        if (f.price == price && itemName.equals(f.itemName)) {
          items.add(f);
          continue DB_ITEMS;
        }
      }
      items.add(new FoodItem(itemName, price, new ArrayList<String>()));
    }
    String status = data.get(2);
    if (status.equals("Filled")) {
      formStatus = FormStatus.filled;
    } else if (status.equals("Cancelled")) {
      formStatus = FormStatus.cancelled;
    } else if (status.equals("Submitted")) {
      formStatus = FormStatus.submitted;
    } else if (status.equals("NotSubmitted")) {
      formStatus = FormStatus.notSubmitted;
    } else return false;
    assignee = data.get(3);
    //TODO process room
    //TODO process time
    return true;
  }

  public ArrayList<String> deconstruct() {
    ArrayList<String> item = new ArrayList<String>();
    item.add(formID);
    if (items.isEmpty()) {
      item.add("");
    } else {
      String itemString = String.format("%s %.2f", items.get(0).itemName, items.get(0).price);
      for (int i = 1; i < items.size(); i++) {
        itemString += String.format("-_-%s %.2f", items.get(i).itemName, items.get(i).price);
      }
      item.add(itemString);
    }
    switch (formStatus) {
      case filled -> {
        item.add("Filled");
      }
      case notSubmitted -> {
        item.add("NotSubmitted");
      }
      case submitted -> {
        item.add("Submitted");
      }
      case cancelled -> {
        item.add("Cancelled");
      }
    }
    item.add(assignee);
    item.add(deliveryLocation.toString());
    item.add(deliveryTime.format(DateTimeFormatter.ofPattern("EE, MM/dd\nh:ma")));
    return item;
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
}
