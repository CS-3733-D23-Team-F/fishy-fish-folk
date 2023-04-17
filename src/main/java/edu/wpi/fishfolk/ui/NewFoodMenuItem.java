package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class NewFoodMenuItem {
  @Getter String description;
  @Getter String name;
  @Getter float price;
  @Getter String imageLoc;
  @Getter FoodCategory cat;

  public NewFoodMenuItem(String n, String d, String i, float p, FoodCategory c) {
    name = n;
    description = d;
    imageLoc = i;
    price = p;
    cat = c;
  }
}
