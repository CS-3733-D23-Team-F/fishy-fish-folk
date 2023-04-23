package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.Location;
import lombok.Getter;
import lombok.Setter;

public class ObservableLocation {

  @Getter @Setter private String longname;
  @Getter @Setter private String shortname;
  @Getter @Setter private String type;

  public ObservableLocation(Location loc) {
    this.longname = loc.getLongName();
    this.shortname = loc.getShortName();
    this.type = loc.getNodeType().toString();
  }
}
