package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class NewFoodMenuItem {
  @Getter String description;
  @Getter String name;
  @Getter float price;
  @Getter String imageLoc;
  @Getter FoodCategory cat;

  /**
   * Creates an item to be listed on the food menu
   * @param n the name of the item
   * @param d the description of the item
   * @param i the Location of the item's image
   * @param p the price of the image
   * @param c what category the item belongs in
   */
  public NewFoodMenuItem(String n, String d, String i, float p, FoodCategory c) {
    name = n;
    description = d;
    imageLoc = i;
    price = p;
    cat = c;
  }
}
