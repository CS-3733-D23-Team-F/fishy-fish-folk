package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.ui.Sign;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

public class SignagePreset {

    @Getter @Setter private String name;
    @Getter @Setter private LocalDate date;
    @Getter @Setter private ArrayList<Sign> signs;

    /**
     * Table entry type: Signage Preset
     * @param name
     * @param date
     * @param signs
     */
    public SignagePreset(String name, LocalDate date, ArrayList<Sign> signs) {
        this.name = name;
        this.date = date;
        this.signs = signs;
    }
}
