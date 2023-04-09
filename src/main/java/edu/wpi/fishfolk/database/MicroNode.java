package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.NodeType;
import java.util.ArrayList;
import javafx.geometry.Point2D;

/** */
public class MicroNode extends TableEntry {

  public int nid;
  public Point2D point;
  public String floor;
  public String building;

  public int degree;
  private ArrayList<Location> locations;

  public MicroNode() {
    super();
  }

  public MicroNode(int nodeId, double xcoord, double ycoord, String floor, String building) {
    this.id = Integer.toString(nodeId);
    this.nid = nodeId;
    this.point = new Point2D(xcoord, ycoord);
    this.floor = floor;
    this.building = building;

    degree = 0;
    locations = new ArrayList<>();
  }

  @Override
  public boolean construct(ArrayList<String> data) {
    if (data.size() != 5) {
      return false;
    }

    this.id = data.get(0);
    this.nid = Integer.parseInt(this.id);
    this.point = new Point2D(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(2)));
    this.floor = data.get(3);
    this.building = data.get(4);

    degree = 0;
    locations = new ArrayList<>();
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

  /**
   * Add a location to this MicroNode.
   *
   * @param loc the location to add
   * @return true if added, false if loc was already present
   */
  public boolean addLocation(Location loc) {
    if (locations.contains(loc)) {
      return false;
    }
    locations.add(loc);
    return true;
  }

  /**
   * Remove a location from this MicroNode.
   *
   * @param loc the location to remove
   * @return true upon successful removal, otherwise false
   */
  public boolean removeLocation(Location loc) {
    return locations.remove(loc);
  }

  /** Clear this MicroNode's associated locations. */
  public void clearLocations() {
    locations.clear();
  }

  public boolean containsType(NodeType type) {
    for (Location loc : locations) {
      if (loc.type == type) return true;
    }
    return false;
  }

  /**
   * Get the elevator letters associated with this node. The only case in which there could be
   * multiple letters is if one node holds elevator nodes from two different shafts.
   *
   * @return an ArrayList<String> containing the elevator letters
   */
  public ArrayList<String> getElevLetters() {
    ArrayList<String> letters = new ArrayList<>();
    for (Location loc : locations) {
      if (loc.type == NodeType.ELEV) {
        letters.add(loc.longname.substring(8, 10));
      }
    }
    return letters;
  }
}
