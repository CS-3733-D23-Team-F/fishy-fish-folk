package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class NewFoodItem {
  @Getter private String name;
  @Getter private int quantity;

  /**
   * Creates a food item
   *
   * @param n the name of the item
   * @param q how many of the item there are
   */
  public NewFoodItem(String n, int q) {
    name = n;
    quantity = q;
  }

  /**
   * Returns the item in question as a string, with quantity listed before name
   *
   * @return a string of the item
   */
  public String toString() {
    return quantity + " " + name;
  }
}
