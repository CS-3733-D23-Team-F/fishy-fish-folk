package edu.wpi.fishfolk.database.TableEntry;

import java.time.LocalDate;
import lombok.Getter;

public class LocationDate {
  @Getter private Location location;
  @Getter private LocalDate date;

  public LocationDate(Location location, LocalDate date) {
    this.location = location;
    this.date = date;
  }

  public String toString() {
    return location.getLongName() + " moving on " + date;
  }
}
