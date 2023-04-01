package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;
import java.util.ArrayList;
import javafx.geometry.Point2D;

public class Node extends TableEntry {

  public String id;
  public Point2D point;
  public String floor;
  public String building;
  public NodeType type;
  public String longName;
  public String shortName;

  public int degree;

  public Node(
      String id,
      Point2D point,
      String floor,
      String building,
      NodeType type,
      String longName,
      String shortName) {

    this.id = id;
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
    return id.hashCode();
  }

  @Override
  public TableEntry construct(ArrayList<String> data) {

    if (data.size() < 8) {
      return null;
    }

    return new Node(
        data.get(0),
        new Point2D(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(2))),
        data.get(3),
        data.get(4),
        NodeType.valueOf(data.get(5).trim()),
        data.get(6),
        data.get(7));
  }

  @Override
  public ArrayList<String> deconstruct() {

    ArrayList<String> data = new ArrayList<>();
    data.add(id);
    data.add(Double.toString(point.getX()));
    data.add(Double.toString(point.getY()));
    data.add(floor);
    data.add(building);
    data.add(String.valueOf(type));
    data.add(longName);
    data.add(shortName);

    return data;
  }
}
