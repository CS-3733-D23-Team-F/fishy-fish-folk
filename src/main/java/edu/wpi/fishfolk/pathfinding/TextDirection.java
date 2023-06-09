package edu.wpi.fishfolk.pathfinding;

import lombok.Getter;

public class TextDirection {

  @Getter private Direction direction;
  @Getter private String text;
  @Getter private String distance = "";

  public TextDirection(Direction direction, String text) {
    this.direction = direction;
    this.text = text;
  }

  public TextDirection(PathSection segment) {
    this.direction = segment.getDirection();

    switch (segment.getDirection()) {
      case STRAIGHT:
        text = "Head straight";
        this.distance = segment.formatDistance();
        break;

      case LEFT:
        text = "Turn left";
        break;

      case RIGHT:
        text = "Turn right";
        break;
    }
  }

  public String toString() {
    String meters =
        distance.length() == 0
            ? ""
            : (String.format("%.1f", Double.parseDouble(distance) / 10.42) + "m");
    return text + " " + meters + "\n\n";
  }
}
