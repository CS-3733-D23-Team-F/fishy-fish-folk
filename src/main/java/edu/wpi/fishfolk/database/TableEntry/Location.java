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

  public boolean isDestination() {
    return nodeType != NodeType.HALL && nodeType != NodeType.ELEV && nodeType != NodeType.STAI;
  }

  @Override
  public String toString() {
    return "[" + longName + "; " + shortName + "; " + nodeType + "]";
  }

  public void addMove(Node node, LocalDate date) {
    movesProperty.add(new NodeDate(node, date));
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
    return movesProperty.stream()
        .filter(nodeDate -> nodeDate.getDate().isBefore(date))
        .sorted(Comparator.comparing(NodeDate::getDate))
        .toList()
        .get(0)
        .getNode();
  }

  public List<NodeDate> getMoves(LocalDate date) {
    return movesProperty.stream().filter(move -> move.getDate().isBefore(date)).toList();
  }
}
