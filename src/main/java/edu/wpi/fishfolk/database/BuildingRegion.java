package edu.wpi.fishfolk.database;

import java.awt.*;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

public class BuildingRegion {
  // collection of RegionShapes and building name

  String buildingName;

  String floor;
  ArrayList<Polygon> components;

  public BuildingRegion(ArrayList<Polygon> components, String buildingName, String floor) {
    this.components = components;
    this.buildingName = buildingName;
    this.floor = floor;
  }

  public void addPolygon(Polygon poly) {
    components.add(poly);
  }

  public boolean isWithinRegion(Point2D interestPoint, String curFloor) {
    for (Polygon component : components) {
      if (component.contains(interestPoint) && this.floor.equals(curFloor)) {
        return true;
      }
    }
    return false;
  }
}
