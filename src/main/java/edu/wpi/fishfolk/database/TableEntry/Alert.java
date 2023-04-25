package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.util.AlertType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

public class Alert {

  @Getter @Setter private String longName;
  @Getter @Setter private LocalDate date;
  @Getter @Setter private String text;
  @Getter private final AlertType type;

  // For DAO
  @Getter @Setter private EntryStatus status;

  public Alert(String longName, LocalDate date, String text) {
    this.longName = longName;
    this.date = date;
    this.text = text;
    this.type = AlertType.MOVE;
  }

  public Alert(String text) {
    this.longName = "";
    this.date = null;
    this.text = text;
    this.type = AlertType.OTHER;
  }
}
