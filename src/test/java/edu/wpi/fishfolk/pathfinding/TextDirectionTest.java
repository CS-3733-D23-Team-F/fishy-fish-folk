package edu.wpi.fishfolk.pathfinding;

import static org.junit.jupiter.api.Assertions.*;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

class TextDirectionTest {

  @Test
  public void testPathsegmentConstrctor() {
    PathSegment ps = new PathSegment(new Point2D(0, 0), new Point2D(10, 20));
    TextDirection td = new TextDirection(ps);
  }
}
