package edu.wpi.fishfolk.pathfinding;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

public class PathfindingTests {

  @Test
  public void angle_between_points() {

    Point2D orig = Point2D.ZERO;
    Point2D A = new Point2D(1, 0);
    Point2D B = new Point2D(0, 1);

    System.out.println(A.angle(B));
    System.out.println(B.angle(A));

    // results: both print out 90 which means the angle between points is not helpful in
    // differentiating a left vs a right turn

    System.out.println(A.crossProduct(B));
    System.out.println(B.crossProduct(A));

    // results: the cross product works to differentiate between a left vs right turn
  }

  @Test
  public void angle_between_points2() {

    Point2D orig = Point2D.ZERO;
    Point2D A = new Point2D(-1, -1);
    Point2D B = new Point2D(1, 0);

    System.out.println(A.crossProduct(B));
  }
}
