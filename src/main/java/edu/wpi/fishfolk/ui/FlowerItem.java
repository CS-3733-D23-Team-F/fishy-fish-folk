package edu.wpi.fishfolk.ui;

public class FlowerItem {

  public String itemName;
  public int fullCost;

  public int amount;

  public FlowerItem(String name, int price, int amount) {
    this.itemName = name;
    this.amount = amount;
    this.fullCost = price * amount;
  }
}
