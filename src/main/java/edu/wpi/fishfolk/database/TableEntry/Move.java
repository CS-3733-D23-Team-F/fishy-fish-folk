package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import java.sql.Date;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public class Move {

  @Getter @Setter private int nodeID;
  @Getter @Setter private String longName;
  @Getter @Setter private LocalDate date;
  @Getter @Setter private EntryStatus status;

  @Getter private ObjectProperty<Move> moveProperty;

  /**
   * Table entry type: Move
   *
   * @param nodeID Unique ID of node
   * @param longName Long name of location
   * @param date Date of move
   */
  public Move(int nodeID, String longName, LocalDate date) {
    this.nodeID = nodeID;
    this.longName = longName;
    this.date = date;

    moveProperty = new SimpleObjectProperty<>(this);
  }

  /**
   * Get unique id for this move for use in the local Hashmap.
   *
   * @return
   */
  public String getMoveID() {
    return longName + date.toString();
  }

  @Override
  public boolean equals(Object other) {
    return this.getMoveID().equals(((Move) other).getMoveID());
  }

  /**
   * Sanitize dates stored as strings to fit dd/MM/yyyy format. Adds leading zeroes and "20" in
   * front of shorthand years.
   *
   * @param date
   * @return
   */
  public static String sanitizeDate(String date) {
    String[] pieces = date.split("/");
    // add 0's to single digit days and months
    if (pieces[0].length() == 1) pieces[0] = "0" + pieces[0];
    if (pieces[1].length() == 1) pieces[1] = "0" + pieces[1];
    if (pieces[2].length() == 2) pieces[2] = "20" + pieces[2];
    return String.join("/", pieces);
  }

  /**
   * Convert from LocalDate to SQL date
   *
   * @return
   */
  public Date getSQLDate() {
    return Date.valueOf(date);
  }

  @Override
  public String toString() {
    return "[longname: " + longName + "; nodeID:" + nodeID + "; date: " + date + "]";
  }
}
