package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.Sign;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

public class SignagePreset {

  @Getter @Setter private String name;
  @Getter @Setter private LocalDate date;
  @Getter @Setter private Sign[] signs;
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Signage Preset
   *
   * @param name Name of preset
   * @param date Date to apply preset
   * @param signs Array of signs in preset
   */
  public SignagePreset(String name, LocalDate date, Sign[] signs) {
    this.name = name;
    this.date = date;
    this.signs = signs;
    this.status = EntryStatus.OLD;
  }

  public SignagePreset() {}
}
