package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.pathfinding.NodeType;import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;import java.util.HashMap;import java.util.HashSet;import java.util.List;

public class Node {

  @Getter @Setter private int nodeID;
  @Getter @Setter private Point2D point;
  @Getter @Setter private String floor;
  @Getter @Setter private String building;
  @Getter @Setter private EntryStatus status;
  private HashSet<Location> locations;

  /**
   * Table entry type: Node
   *
   * @param nodeID Unique ID of node
   * @param point X and Y coordinates of node
   * @param floor Floor of node
   * @param building Building of node
   */
  public Node(int nodeID, Point2D point, String floor, String building) {
    this.nodeID = nodeID;
    this.point = point;
    this.floor = floor;
    this.building = building;
    this.status = EntryStatus.OLD;

    this.locations = new HashSet<>();
  }

  public double getX() {
    return point.getX();
  }

  public double getY() {
    return point.getY();
  }

  public boolean addLocation(Location l){
    return locations.add(l);
  }

  public boolean removeLocation(Location l){
    return locations.remove(l);
  }

  public List<Location> getLocations(){
    return locations.stream().toList();
  }

  public boolean containsType(NodeType type){
    for(Location loc : locations){
      if(loc.getNodeType() == type) return true;
    }
    return false;
  }

  public List<String> getElevLetters(){
    return locations.stream()
            .filter(loc -> loc.getNodeType() == NodeType.ELEV)
            .map(loc -> loc.getLongName().substring(8, 10)) //extract elevator letter
            .toList();
  }
}
