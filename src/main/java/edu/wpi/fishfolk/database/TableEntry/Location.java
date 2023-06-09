package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.util.NodeType;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

public class Location {

  @Getter @Setter private String longName;
  @Getter @Setter private String shortName;
  @Getter @Setter private NodeType nodeType;
  @Getter @Setter private EntryStatus status;

  @Getter private ObjectProperty<Location> locationProperty;
  @Getter private ObservableList<NodeDate> movesProperty = FXCollections.observableArrayList();

  // when locations get removed, replace their moves with this one
  public static Location REMOVED = new Location("Deleted Location", "", NodeType.DFLT);

  /**
   * Table entry type: Location
   *
   * @param longName Long name of location
   * @param shortName Short name of location
   * @param nodeType Node type of location
   */
  public Location(String longName, String shortName, NodeType nodeType) {
    this.longName = longName;
    this.shortName = shortName;
    this.nodeType = nodeType;
    this.status = EntryStatus.OLD;

    locationProperty = new SimpleObjectProperty<>(this);
  }

  public Location deepCopy() {
    return new Location(longName, shortName, nodeType);
  }

  public boolean isDestination() {
    return nodeType != NodeType.HALL && nodeType != NodeType.ELEV && nodeType != NodeType.STAI;
  }

  @Override
  public String toString() {
    return "[" + longName + "; " + shortName + "; " + nodeType + "]";
  }

  public boolean addMove(Node node, LocalDate date) {

    for (NodeDate nodeDate : movesProperty) {
      if (nodeDate.getNode().getNodeID() == node.getNodeID() && nodeDate.getDate().equals(date)) {
        return false;
      }
    }
    movesProperty.add(new NodeDate(node, date));
    return true;
  }

  public void removeMove(Move move) {
    // remove if matches both nodeID and date
    movesProperty.removeIf(
        nodeDate ->
            nodeDate.getNode().getNodeID() == move.getNodeID()
                && nodeDate.getDate().isEqual(move.getDate()));
  }

  public Node getNode(LocalDate date) {

    // filter out moves after the given date
    // sort by date and take the first
    List<NodeDate> moves =
        movesProperty.stream()
            .filter(nodeDate -> nodeDate.getDate().isBefore(date))
            .sorted(Comparator.comparing(NodeDate::getDate))
            .toList();

    if (moves.isEmpty()) {
      return null;

    } else {
      return moves.get(0).getNode();
    }
  }

  /**
   * Delete records of the given node from this Location.
   *
   * @param nodeID of the deleted Node
   */
  public void deleteNode(int nodeID) {
    movesProperty.removeIf(nodedate -> nodedate.getNode().getNodeID() == nodeID);
  }

  public List<NodeDate> getMovesBefore(LocalDate date) {
    return movesProperty.stream().filter(move -> move.getDate().isBefore(date)).toList();
  }

  public boolean assignedBefore(LocalDate date) {
    for (NodeDate nodeDate : movesProperty) {
      if (nodeDate.getDate().isBefore(date)) {
        return true;
      }
    }
    return false;
  }

  public boolean movesBetween(LocalDate start, LocalDate end) {

    for (NodeDate nodeDate : movesProperty) {
      if (nodeDate.getDate().isAfter(start) && nodeDate.getDate().isBefore(end)) {
        return true;
      }
    }
    return false;
  }
}
