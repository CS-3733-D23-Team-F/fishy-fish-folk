package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.Move;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

public class ObservableMove {

  @Getter @Setter private String nodeid;
  @Getter @Setter private String longname;
  @Getter @Setter private String date;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

  public ObservableMove(Move move) {
    this.nodeid = Integer.toString(move.getNodeID());
    this.longname = move.getLongName();
    this.date = move.getDate().format(formatter);
  }

  public static LocalDate parseDate(String date) {
    return LocalDate.parse(date, formatter);
  }

  public boolean matches(String longname, String date) {
    return longname.equals(this.longname) && date.equals(this.date);
  }
}
