package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;

public class Path {

  public ArrayList<Integer> nodes;
  public ArrayList<Point2D> points;

  @Getter @Setter private String floor;
  @Getter @Setter private Direction toNextPath;

  public int numNodes;

  public Path() {
    nodes = new ArrayList<>();
    points = new ArrayList<>();
    numNodes = 0;
  }

  public void addFirst(int nid, Point2D p) {
    // TODO this runs in O(n) time so maybe try batching in groups of 10 or 20 node/point pairs
    nodes.add(0, nid);
    points.add(0, p);
    numNodes++;
  }

  public void addFirst(Node unode) {
    addFirst(unode.getNodeID(), unode.getPoint());
  }

  public int removeLast() {
    numNodes--;
    points.remove(numNodes);
    return nodes.remove(numNodes); // numNodes is now size - 1 which corresponds to the last element
  }

  public double pathLength() {

    double length = 0;
    Point2D prev = points.get(numNodes - 1);

    for (int i = numNodes - 2; i >= 0; i--) {
      length += prev.distance(points.get(i));
      // System.out.print(nodes.get(i + 1) + "_" + nodes.get(i) + "  ");
      prev = points.get(i);
    }

    return length;
  }

  /**
   * Return an array of Point2D evenly spaced along the path.
   *
   * @param count
   * @return
   */
  public Point2D[] interpolate(int count) {

    Point2D[] interPoints = new Point2D[count + 1];
    double[] segmentLengths = new double[numNodes - 1];
    double totalLength = 0;

    for (int i = 0; i < numNodes - 1; i++) {
      segmentLengths[i] = points.get(i).distance(points.get(i + 1));
      totalLength += segmentLengths[i];
    }

    final double dist = totalLength / count;

    // place points dist apart along a segment until a point goes off the end of the segment
    // place the next one along the next segment taking into account how much dist was used on the
    // previous segment
    // see https://www.desmos.com/calculator/xtue9yffxh

    int nextIdx = 1; // index of the path point in front
    Point2D prev = points.get(0), next = points.get(nextIdx);
    double currSegment = prev.distance(next);
    double remainder = currSegment;

    for (int i = 0; i < count; i++) {
      interPoints[i] = prev.interpolate(next, 1 - (remainder / currSegment));
      remainder -= dist;

      // went over this segment, move on to the next one
      if (remainder < 0 && nextIdx < numNodes - 1) {
        prev = next;
        nextIdx++;
        next = points.get(nextIdx);
        currSegment = prev.distance(next);
        remainder = currSegment + remainder;
      }
    }
    interPoints[count] = next;
    return interPoints;
  }

  public List<TextDirection> getDirections() {

    // split path into segments: three points determine a segment
    // to avoid overlaps, dont add the start->mid portion except for the first

    LinkedList<PathSection> segments = new LinkedList<>();

    // keep track of middle index in each set of 3

    for (int midx = 1; midx <= numNodes - 2; midx++) {

      Point2D start = points.get(midx - 1);
      Point2D mid = points.get(midx);
      Point2D end = points.get(midx + 1);

      double angle = mid.angle(start, end);
      if (Math.abs(angle - 180) < 0.1) { // one straight segment start->end

        if (midx == 1) {
          segments.add(new PathSection(start, end));
        } else {
          segments.add(new PathSection(mid, end));
        }

      } else {

        if (midx == 1) {
          segments.add(new PathSection(start, mid));
        }

        if (mid.subtract(start).crossProduct(end.subtract(mid)).getZ() > 0) {
          // cross product > 0 means right turn
          segments.add(new PathSection(mid, Direction.RIGHT));
        } else {

          segments.add(new PathSection(mid, Direction.LEFT));
        }
        segments.add(new PathSection(mid, end)); // turning segments only account for the turn
      }
    }

    // now that the path has been split into segments, combine consecutive straight segments

    Iterator<PathSection> itr = segments.iterator();
    PathSection prev = itr.next(); // first segment

    // combine  consecutive straight segments
    while (itr.hasNext()) {
      PathSection cur = itr.next();

      // two straights in a row
      if (prev.getDirection() == Direction.STRAIGHT && cur.getDirection() == Direction.STRAIGHT) {

        prev.setEnd(cur.getEnd()); // extend prev to cover current
        itr.remove(); // removes current

      } else {
        prev = cur;
      }
    }
    System.out.println("Directions: " + segments.size());
    return segments.stream().map(TextDirection::new).toList();
  }

  public String toString() {
    return floor + ": " + nodes.toString() + " " + toNextPath;
  }

  @Override
  public boolean equals(Object o) {
    Path other = (Path) o;

    return nodes.equals(other.nodes);
  }

  /**
   * Finds a center point from a number of points in a path
   *
   * @param numShown Number of points to center around
   * @return A point that centers around bounds of current point
   */
  public Point2D centerToPath(int numShown) {
    double[] bounds = new double[4];

    if (numShown > numNodes) {
      numShown = numNodes;
    }

    double maxX = -1;
    double maxY = -1;
    double minX = -1;
    double minY = -1;

    for (int point = 0; point < numShown; point++) {

      if (maxX == -1) { // Values unassigned
        maxX = points.get(point).getX();
        maxY = points.get(point).getY();
        minX = maxX;
        minY = maxY;
      }

      // Setting new bounds if greater than current bounds
      if (points.get(point).getX() > maxX) {
        maxX = points.get(point).getX();
      }
      if (points.get(point).getY() > maxY) {
        maxY = points.get(point).getY();
      }
      if (points.get(point).getX() < minX) {
        minX = points.get(point).getX();
      }
      if (points.get(point).getY() < minY) {
        minY = points.get(point).getY();
      }
    }

    return new Point2D((maxX + minX) / 2.0, (maxY + minY) / 2.0);
  }
}
