package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class NewFoodCart {
  @Getter List<quantityItem> items;
  float totalPrice;

  public NewFoodCart() {
    items = new ArrayList<quantityItem>();
    totalPrice = 0;
  }

  public static class quantityItem {
    @Getter NewFoodMenuItem item;
    @Getter byte quantity;

    quantityItem(NewFoodMenuItem i) {
      item = i;
      quantity = 1;
    }
  }

  public void add(NewFoodMenuItem item) {
    for (quantityItem qi : items) {
      if (qi.item == item) {
        qi.quantity++;
        return;
      }
    }
    items.add(new quantityItem(item));
    totalPrice += item.price;
  }

  public boolean remove(NewFoodMenuItem item) {
    for (quantityItem qi : items) {
      if (qi.item == item) {
        qi.quantity--;
        totalPrice -= item.price;
        if (qi.quantity == 0) items.remove(qi);
        return true;
      }
    }
    return false;
  }

  public List<NewFoodItem> getSubmittableItems() {
    List<NewFoodItem> returnable = new ArrayList<NewFoodItem>();
    for (quantityItem qi : items) {
      returnable.add(new NewFoodItem(qi.item.name, qi.quantity));
    }
    return returnable;
  }

  public void removeAll(NewFoodMenuItem item) {
    while (remove(item)) ; // wtf is this, I love it
  }
}
