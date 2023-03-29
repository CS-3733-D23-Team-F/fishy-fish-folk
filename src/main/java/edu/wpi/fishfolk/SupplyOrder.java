package edu.wpi.fishfolk;

import java.util.LinkedList;

public class SupplyOrder {
  public LinkedList<SupplyItem> supplies;

  public SupplyOrder() {
    supplies = new LinkedList<SupplyItem>();
  }

  public void addSupply(SupplyItem item) {
    supplies.add(item);
  }

  public String toString() {
    String string =
        supplies.get(0).supplyName
            + "\n"
            + supplies.get(1).supplyName
            + "\n"
            + supplies.get(2).supplyName
            + "\n"
            + supplies.get(3).supplyName
            + "\n"
            + supplies.get(4).supplyName
            + "\n"
            + supplies.get(5).supplyName
            + "\n"
            + supplies.get(6).supplyName;
    return string;
  }
}
