package edu.wpi.fishfolk.pathfinding;

import java.util.ArrayList;
import javafx.geometry.Point2D;

@Deprecated
public class Node extends TableEntry {

  public int nid; // int representation of string id. for use instead of parsing
  public Point2D point;
  public String floor;
  public String building;
  public NodeType type;
  public String longName;
  public String shortName;

  public int degree;

  public Node() {}

  public Node(
      int id,
      Point2D point,
      String floor,
      String building,
      NodeType type,
      String longName,
      String shortName) {

    this.id = String.valueOf(id);
    this.nid = id;
    this.point = point;
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.longName = longName;
    this.shortName = shortName;

    this.degree = 0;
  }

  @Override
  public int hashCode() {
    return id.hashCode(); // potentially salt this before hashing
  }

  @Override
  public boolean construct(ArrayList<String> data) {

    if (data.size() != 8) {
      return false;
    }

    this.id = data.get(0);
    this.point = new Point2D(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(2)));
    this.floor = data.get(3);
    this.building = data.get(4);
    this.type = NodeType.valueOf(data.get(5).trim());
    this.longName = data.get(6);
    this.shortName = data.get(7);
    return true;
  }

  @Override
  public ArrayList<String> deconstruct() {

    ArrayList<String> data = new ArrayList<>();
    data.add(id);
    data.add(String.valueOf(point.getX()));
    data.add(String.valueOf(point.getY()));
    data.add(floor);
    data.add(building);
    data.add(String.valueOf(type));
    data.add(longName);
    data.add(shortName);

    return data;
  }
}
