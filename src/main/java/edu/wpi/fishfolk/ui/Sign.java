package edu.wpi.fishfolk.ui;

import lombok.Getter;
import lombok.Setter;

public class Sign {

    @Getter @Setter private String label;
    @Getter @Setter private SignDirection direction;

    public Sign(String label, SignDirection direction) {
        this.label = label;
        this.direction = direction;
    }
}
