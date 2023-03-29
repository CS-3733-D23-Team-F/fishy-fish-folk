package edu.wpi.fishfolk;

import java.util.LinkedList;

public class SupplyOrder {
  public LinkedList<SupplyItem> supplies;
  public String link;
  public String roomNum;
  public String notes;

  public SupplyOrder() {

    supplies = new LinkedList<SupplyItem>();
    this.link = link;
    this.roomNum = roomNum;
    this.notes = notes;
  }

  public void addSupply(SupplyItem item) {
    supplies.add(item);
  }

  public String toString() {
    String string =
        "Supply List:"
            + "\n"
            + supplies.get(0).supplyName
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
            + supplies.get(6).supplyName
            + "\n"
            + "Link if not in stock: "
            + link
            + "\n"
            + "Room Number: "
            + roomNum
            + "\n"
            + "Additional Notes: "
            + notes;
    return string;
  }
}
