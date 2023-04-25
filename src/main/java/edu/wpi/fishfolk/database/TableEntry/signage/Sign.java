package edu.wpi.fishfolk.database.TableEntry.signage;

import lombok.Getter;
import lombok.Setter;

public class Sign {

    @Getter
    @Setter
    SignDirection direction;
    @Getter
    @Setter
    String label;

    Sign(SignDirection direction, String label) {
        this.direction = direction;
        this.label = label;
    }
}
