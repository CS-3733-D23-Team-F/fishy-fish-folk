package edu.wpi.fishfolk.ui;

import lombok.Getter;

public class NewFoodItem {
  @Getter
  String name;
  @Getter
  int quantity;

  public NewFoodItem(String n, int q) {
    name = n;
    quantity = q;
  }
}
