package edu.wpi.fishfolk.mapeditor;

import java.awt.*;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class BuildingRegion {
  // collection of RegionShapes and building name

  public String getBuildingName() {
    return buildingName;
  }

  String buildingName;

  String floor;
  Polygon region;

  public BuildingRegion(Polygon region, String buildingName, String floor) {
    this.region = region;
    this.buildingName = buildingName;
    this.floor = floor;
  }

  public boolean isWithinRegion(Point2D interestPoint, String curFloor) {
    if (region.contains(interestPoint) && this.floor.equals(curFloor)) {
      return true;
    }
    return false;
  }

  public static String getBuilding(double x, double y, String floor) {
    return "bingus tower";
  }
}
