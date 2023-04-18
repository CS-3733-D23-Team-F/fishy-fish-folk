package edu.wpi.fishfolk.ui;

public class FlowerItem {

  public String itemName;
  public double fullCost;

  public int amount;

  public FlowerItem(String name, double price, int amount) {
    this.itemName = name;
    this.amount = amount;
    this.fullCost = price * amount;
  }
}
