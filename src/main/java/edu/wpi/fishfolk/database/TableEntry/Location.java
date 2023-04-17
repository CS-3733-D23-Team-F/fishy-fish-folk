package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.pathfinding.NodeType;
import lombok.Getter;
import lombok.Setter;

public class Location {

  @Getter @Setter private String longName;
  @Getter @Setter private String shortName;
  @Getter @Setter private NodeType nodeType;
  @Getter @Setter private EntryStatus status;

  @Getter @Setter private Node node;

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
  }

  public boolean isDestination(){
    return nodeType != NodeType.HALL
            & nodeType != NodeType.ELEV
            & nodeType != NodeType.STAI;
  }
}
