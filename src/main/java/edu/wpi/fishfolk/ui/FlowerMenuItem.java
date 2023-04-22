package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class FlowerMenuItem {

  @Getter private String description;
  @Getter private String name;
  @Getter private float price;
  @Getter private String imageLoc;
  @Getter private FlowerCategory cat;

  /**
   * Creates an item to be listed on the food menu
   *
   * @param n the name of the item
   * @param d the description of the item
   * @param i the Location of the item's image
   * @param p the price of the image
   * @param c what category the item belongs in
   */
  public FlowerMenuItem(String n, String d, String i, float p, FlowerCategory c) {
    name = n;
    description = d;
    imageLoc = i;
    price = p;
    cat = c;
  }
}
