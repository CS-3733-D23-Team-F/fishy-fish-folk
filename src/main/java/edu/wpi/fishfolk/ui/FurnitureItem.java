package edu.wpi.fishfolk.ui;

public class FurnitureItem {
  public String furnitureName;

  public FurnitureItem(String furnitureName) {
    this.furnitureName = furnitureName;
  }

  public static FurnitureItem bed = new FurnitureItem("Bed");
  public static FurnitureItem chair = new FurnitureItem("Chair");
  public static FurnitureItem desk = new FurnitureItem("Desk");
  public static FurnitureItem fileCabinet = new FurnitureItem("File Cabinet");
  public static FurnitureItem clock = new FurnitureItem("Clock");
  public static FurnitureItem xRay = new FurnitureItem("X-Ray Machine");
  public static FurnitureItem trashCan = new FurnitureItem("trashCan");
}
