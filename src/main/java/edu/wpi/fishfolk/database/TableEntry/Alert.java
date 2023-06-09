package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.util.AlertType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class Alert {

  @Getter private final LocalDateTime timestamp;
  @Getter @Setter private String longName;
  @Getter @Setter private LocalDate date;
  @Getter @Setter private String text;
  @Getter private final AlertType type;

  @Getter @Setter private String username;

  // For DAO
  @Getter @Setter private EntryStatus status;

  public Alert(
      LocalDateTime timestamp, String username, String longName, LocalDate date, String text) {
    this.timestamp = timestamp;
    this.longName = longName;
    this.date = date;
    this.text = text;
    this.type = AlertType.MOVE;
    this.username = username;
  }

  public Alert(LocalDateTime timestamp, String username, String text) {
    this.timestamp = timestamp;
    this.longName = "no location";
    this.date = LocalDate.now();
    this.text = text;
    this.type = AlertType.OTHER;
    this.username = username;
  }
}
