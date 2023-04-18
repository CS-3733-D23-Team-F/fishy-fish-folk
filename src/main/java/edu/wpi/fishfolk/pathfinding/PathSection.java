package edu.wpi.fishfolk.pathfinding;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class PathSection {

  @Getter @Setter private Direction direction;
  @Getter @Setter private Point2D start, mid, end;

  public PathSection(Point2D start, Point2D end) {

    this.direction = Direction.STRAIGHT;

    this.start = start;
    this.end = end;
  }

  public PathSection(Point2D mid, Direction dir) {

    this.mid = mid;
    this.direction = dir;
  }

  public String formatDistance() {
    double distance = start.distance(end);
    return String.format("%.1f", distance);
  }
}
