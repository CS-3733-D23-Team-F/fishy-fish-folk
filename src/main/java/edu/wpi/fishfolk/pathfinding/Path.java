package edu.wpi.fishfolk.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.geometry.Point2D;
import lombok.Getter;

public class Path {

  @Getter private ArrayList<String> nodes;
  @Getter private ArrayList<Point2D> points;

  int numNodes;

  public Path() {
    nodes = new ArrayList<>();
    points = new ArrayList<>();
    numNodes = 0;
  }

  public void addFirst(String n, Point2D p) {
    // this runs in O(n) time so maybe try batching in groups of 10 or 20 node/point pairs
    nodes.add(0, n);
    points.add(0, p);
    numNodes++;
  }

  public String removeLast() {
    numNodes--;
    points.remove(numNodes);
    return nodes.remove(numNodes); // numNodes is now size - 1 which corresponds to the last element
  }

  public double pathLength() {

    double length = 0;
    Point2D prev = points.get(numNodes - 1);

    for (int i = numNodes - 2; i >= 0; i--) {
      length += prev.distance(points.get(i));
      prev = points.get(i);
      ;
    }

    return length;
  }

  public String createDirections() {

    // split path into segments: three points determine a segment
    // to avoid overlaps, dont add the start->mid portion except for the first

    LinkedList<PathSegment> segments = new LinkedList<>();

    for (int midx = 1; midx < numNodes - 2; midx++) { // keep track of middle index in each set of 3

      Point2D start = points.get(midx - 1);
      Point2D mid = points.get(midx);
      Point2D end = points.get(midx + 1);

      double angle = mid.angle(start, end);

      if (Math.abs(angle - 180) < 0.1) { // one straight segment start->end

        if (midx == 1) {
          segments.add(new PathSegment(start, end));
        } else {
          segments.add(new PathSegment(mid, end));
        }

      } else {

        if (midx == 1) {
          segments.add(new PathSegment(start, mid));
        }

        if (mid.subtract(start).crossProduct(end.subtract(mid)).getZ() > 0) {
          // cross product > 0 means right turn
          segments.add(new PathSegment(mid, PathSegmentType.RIGHT));
        } else {

          segments.add(new PathSegment(mid, PathSegmentType.LEFT));
        }
        segments.add(new PathSegment(mid, end)); // turning segments only account for the turn
      }
    }

    // now that the path has been split into segments, create directions
    String directions = "start at " + nodes.get(0) + "\n";

    for (PathSegment segment : segments) {

      switch (segment.type) {
        case STRAIGHT:
          directions += "straight for " + segment.end.distance(segment.start) + "\n";
          break;
        case RIGHT:
          directions += "turn right \n";
          break;
        case LEFT:
          directions += "turn left \n";
          break;
      }
    }

    directions += "end at " + nodes.get(numNodes - 1) + "\n";
    System.out.println(segments.size() + " steps");
    return directions;
  }

  public String toString() {
    return nodes.toString();
  }

  @Override
  public boolean equals(Object o) {
    Path other = (Path) o;

    return nodes.equals(other.nodes);
  }
}

class PathSegment {

  PathSegmentType type;

  Point2D start, mid, end;

  public PathSegment(Point2D start, Point2D end) {

    this.type = PathSegmentType.STRAIGHT;

    this.start = start;
    this.end = end;
  }

  public PathSegment(Point2D mid, PathSegmentType dir) {

    this.mid = mid;
    this.type = dir;
  }
}

enum PathSegmentType {
  STRAIGHT,
  LEFT,
  RIGHT
}
