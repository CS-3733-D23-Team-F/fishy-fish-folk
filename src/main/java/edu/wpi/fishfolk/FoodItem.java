package edu.wpi.fishfolk;

import java.util.ArrayList;

public class FoodItem {
  public String itemName;
  public float price;
  public ArrayList<String> allergens;

  public FoodItem(String name, float price, ArrayList<String> aller) {
    itemName = name;
    this.price = price;
    allergens = aller;
  }

  public static FoodItem generic1 = new FoodItem("Generic Soup", 4.50F, new ArrayList<String>());
  public static FoodItem generic2 = new FoodItem("Generic Salad", 3F, new ArrayList<String>());
  public static FoodItem generic3 = new FoodItem("Generic Burger", 7.75F, new ArrayList<String>());
}
