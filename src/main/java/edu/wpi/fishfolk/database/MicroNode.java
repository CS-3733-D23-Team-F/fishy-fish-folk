package edu.wpi.fishfolk.database;

import java.util.ArrayList;
import javafx.geometry.Point2D;

/** */

// TODO: charles work, check Node.csv
public class MicroNode extends TableEntry {
  public Point2D point;
  public String floor;
  public String building;

  @Override
  public boolean construct(ArrayList<String> data) {
    if (data.size() != 5) {
      return false;
    }

    this.id = data.get(0);
    this.point = new Point2D(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(2)));
    this.floor = data.get(3);
    this.building = data.get(4);
    return true;
  }

  @Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> data = new ArrayList<>();
    data.add(this.id);
    data.add(Double.toString(this.point.getX()));
    data.add(Double.toString(this.point.getY()));
    data.add(floor);
    data.add(building);

    return data;
  }

  public MicroNode(int nodeId, double xcoord, double ycoord, String floor, String building) {
    this.id = Integer.toString(nodeId);
    this.point = new Point2D(xcoord, ycoord);
    this.floor = floor;
    this.building = building;
  }
}
