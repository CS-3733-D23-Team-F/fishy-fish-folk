package edu.wpi.fishfolk;

public class SupplyItem {
  public String supplyName;
  public float supplyPrice;

  public SupplyItem(String supplyName, float supplyPrice) {
    this.supplyName = supplyName;
    this.supplyPrice = supplyPrice;
  }

  public static SupplyItem supply1 = new SupplyItem("supply1", 1.99F);
  public static SupplyItem supply2 = new SupplyItem("supply2", 1.99F);
  public static SupplyItem supply3 = new SupplyItem("supply3", 1.99F);
  public static SupplyItem supply4 = new SupplyItem("supply4", 1.99F);
  public static SupplyItem supply5 = new SupplyItem("supply5", 1.99F);
  public static SupplyItem supply6 = new SupplyItem("supply6", 1.99F);
  public static SupplyItem supply7 = new SupplyItem("supply7", 1.99F);
  public static SupplyItem supply8 = new SupplyItem("empty", 1.99F);
}
