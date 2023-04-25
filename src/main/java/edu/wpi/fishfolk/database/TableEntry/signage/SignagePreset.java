package edu.wpi.fishfolk.database.TableEntry.signage;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SignagePreset {

    @Getter @Setter String name;
    @Getter @Setter LocalDate date;
    @Getter @Setter ArrayList<Sign> signs;

    public SignagePreset(String name, LocalDate date, ArrayList<Sign> signs) {
        this.name = name;
        this.date = date;
        this.signs = signs;
    }
}

