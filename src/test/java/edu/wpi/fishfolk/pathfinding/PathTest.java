package edu.wpi.fishfolk.pathfinding;

import static org.junit.jupiter.api.Assertions.*;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

class PathTest {

  @Test
  void interpolate() {

    Path path = new Path();
    path.addFirst(0, new Point2D(0, 0));
    path.addFirst(1, new Point2D(0, 2));
    path.addFirst(2, new Point2D(1, 3));
    path.addFirst(3, new Point2D(2, 3));
    path.addFirst(4, new Point2D(3, 4));
    path.addFirst(5, new Point2D(5, 2));
    path.addFirst(6, new Point2D(4, 0));
    path.addFirst(7, new Point2D(2, -1));

    for (Point2D p : path.interpolate(25)) {
      System.out.println(p.toString());
    }
  }
}
