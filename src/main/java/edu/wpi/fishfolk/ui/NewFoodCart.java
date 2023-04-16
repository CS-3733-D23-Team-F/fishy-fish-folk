package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;

public class NewFoodCart {
    List<quantityItem> items;
    float totalPrice;
    public NewFoodCart () {
        items = new ArrayList<quantityItem>();
        totalPrice = 0;
    }
    public static class quantityItem {
        NewFoodMenuItem item;
        byte quantity;
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
    }

    public List<NewFoodItem> getItems() {
        List<NewFoodItem> returnable = new ArrayList<NewFoodItem>();
        for (quantityItem qi : items) {
            returnable.add(new NewFoodItem(qi.item.name, qi.quantity));
        }
        return returnable;
    }
}
