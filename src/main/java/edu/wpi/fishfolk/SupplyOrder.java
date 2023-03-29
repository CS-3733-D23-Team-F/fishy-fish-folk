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
}
