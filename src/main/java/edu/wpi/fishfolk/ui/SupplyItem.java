package edu.wpi.fishfolk.ui;

public class SupplyItem {
  public String supplyName;
  public float supplyPrice;

  public SupplyItem(String supplyName, float supplyPrice) {
    this.supplyName = supplyName;
    this.supplyPrice = supplyPrice;
  }

  public static SupplyItem supply1 = new SupplyItem("Pencil", 1.99F);
  public static SupplyItem supply2 = new SupplyItem("Pen", 1.99F);
  public static SupplyItem supply3 = new SupplyItem("Eraser", 1.99F);
  public static SupplyItem supply4 = new SupplyItem("Marker", 1.99F);
  public static SupplyItem supply5 = new SupplyItem("Notepad", 1.99F);
  public static SupplyItem supply6 = new SupplyItem("Clipboard", 1.99F);
  public static SupplyItem supply7 = new SupplyItem("Apple", 1.99F);
  public static SupplyItem supply8 = new SupplyItem("", 1.99F);
}
