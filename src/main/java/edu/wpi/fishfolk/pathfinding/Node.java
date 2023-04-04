package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class Node extends TableEntry {

  public int nid; // int representation of string id. for use instead of parsing // TODO: nid vs id?
  @Getter @Setter public String id;
  @Getter @Setter public String oldID;
  @Getter @Setter public Point2D point;
  @Getter @Setter public String x;
  @Getter @Setter public String y;
  @Getter @Setter public String floor;
  @Getter @Setter public String building;
  @Getter @Setter public NodeType type;
  @Getter @Setter public String typeName;
  @Getter @Setter public String longName;
  @Getter @Setter public String shortName;

  @Getter @Setter public int degree;

  public Node() {}

  public Node(
      int id,
      Point2D point,
      String floor,
      String building,
      NodeType type,
      String longName,
      String shortName) {

    this.id = Integer.toString(id);
    this.nid = id;
    this.point = point;
    this.x = String.valueOf(point.getX());
    this.y = String.valueOf(point.getY());
    this.floor = floor;
    this.building = building;
    this.type = type;
    this.typeName = "cry";
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
