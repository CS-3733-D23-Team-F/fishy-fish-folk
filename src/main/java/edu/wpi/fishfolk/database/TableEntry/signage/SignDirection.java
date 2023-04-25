package edu.wpi.fishfolk.database.TableEntry.signage;

public enum SignDirection {
    UP(0),
    RIGHT(90),
    DOWN(180),
    LEFT(270);

    public final double angle;

    SignDirection(double angle) {
        this.angle = angle;
    }
}
