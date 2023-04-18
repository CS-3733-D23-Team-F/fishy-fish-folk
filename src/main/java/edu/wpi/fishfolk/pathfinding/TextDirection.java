package edu.wpi.fishfolk.pathfinding;

import lombok.Getter;

public class TextDirection {

  @Getter private Direction direction;
  @Getter private String text;

  public TextDirection(Direction direction, String text) {
    this.direction = direction;
    this.text = text;
  }

  public TextDirection(PathSegment segment) {
    this.direction = segment.getDirection();

    switch (segment.getDirection()) {
      case STRAIGHT:
        text = "Head straight for " + segment.formatDistance();
        break;

      case LEFT:
        text = "Turn left.";
        break;

      case RIGHT:
        text = "Turn right.";
        break;
    }
  }
}
