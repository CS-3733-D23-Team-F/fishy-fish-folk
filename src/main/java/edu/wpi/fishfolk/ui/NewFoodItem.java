package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class NewFoodItem {
  @Getter String name;
  @Getter int quantity;

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
}
