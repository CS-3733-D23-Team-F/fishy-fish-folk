package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.util.NodeType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class Node {

  @Getter @Setter private int nodeID;
  @Getter @Setter private Point2D point;
  @Getter @Setter private String floor;
  @Getter @Setter private String building;
  @Getter @Setter private EntryStatus status;

  @Getter private ObjectProperty<Node> nodeProperty;

  private ArrayList<LocationDate> moves = new ArrayList<>();

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

    nodeProperty = new SimpleObjectProperty<>(this);
  }

  public double getX() {
    return point.getX();
  }

  public double getY() {
    return point.getY();
  }

  public void addMove(Location location, LocalDate date) {
    moves.add(new LocationDate(location, date));
  }

  public List<Location> getLocations(LocalDate date) {
    return moves.stream()
        .filter(move -> move.getDate().isBefore(date))
        .map(LocationDate::getLocation)
        .toList();
  }

  public boolean containsType(NodeType type) {
    for (LocationDate move : moves) {
      if (move.getLocation().getNodeType() == type) return true;
    }
    return false;
  }

  public List<String> getElevLetters() {
    return moves.stream()
        .filter(move -> move.getLocation().getNodeType() == NodeType.ELEV)
        .map(move -> move.getLocation().getLongName().substring(8, 10)) // extract elevator letter
        .toList();
  }

  /**
   * Snap this node to the nearest point on a lattice grid of given size
   *
   * @param s sidelength of a square in the grid
   */
  public void snapToGrid(double s) {
    point = new Point2D(Math.round(point.getX() / s) * s, Math.round(point.getY() / s) * s);
  }
}

class LocationDate {
  @Getter private Location location;
  @Getter private LocalDate date;

  public LocationDate(Location location, LocalDate date) {
    this.location = location;
    this.date = date;
  }
}
