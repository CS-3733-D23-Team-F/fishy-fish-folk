package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class FlowerItem {

  @Getter private String name;
  @Getter private int quantity;

  @Getter private double price;

  /**
   * Creates a food item
   *
   * @param n the name of the item
   * @param q how many of the item there are
   */
  public FlowerItem(String n, int q, double price) {
    name = n;
    quantity = q;
    this.price = price;
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
