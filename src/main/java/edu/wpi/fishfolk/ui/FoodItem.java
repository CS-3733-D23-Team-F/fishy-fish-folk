package edu.wpi.fishfolk.ui;

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

  public static FoodItem generic1 = new FoodItem("Generic Soup", 4.50F, new ArrayList<String>()),
      generic2 = new FoodItem("Generic Salad", 3F, new ArrayList<String>()),
      generic3 = new FoodItem("Generic Poutine", 7.75F, new ArrayList<String>()),
      generic4 = new FoodItem("Generic Burger", 10F, new ArrayList<String>()),
      generic5 = new FoodItem("Generic Pizza", 5.50F, new ArrayList<String>()),
      generic6 = new FoodItem("Generic Fries", 2.50F, new ArrayList<String>()),
      filler = new FoodItem("Space Filler", 0F, new ArrayList<String>());
}
