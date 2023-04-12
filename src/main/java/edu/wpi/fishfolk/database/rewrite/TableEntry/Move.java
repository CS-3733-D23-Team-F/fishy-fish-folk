package edu.wpi.fishfolk.database.rewrite.TableEntry;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class Move {

  @Getter @Setter private int nodeID;
  @Getter @Setter private String longName;
  @Getter @Setter private LocalDateTime date;

  /**
   * Table entry type: Move
   *
   * @param nodeID Unique ID of node
   * @param longName Long name of location
   * @param date Date of move
   */
  public Move(int nodeID, String longName, LocalDateTime date) {
    this.nodeID = nodeID;
    this.longName = longName;
    this.date = date;
  }
}
