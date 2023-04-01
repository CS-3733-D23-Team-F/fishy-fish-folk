package edu.wpi.fishfolk.controllers;

import static org.junit.jupiter.api.Assertions.*;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

class PathfindingControllerTest {

  @Test
  public void test_point_converter() {

    PathfindingController controller = new PathfindingController();

    System.out.println(controller.convert(new Point2D(850, 150)));

    System.out.println(controller.convert(new Point2D(5000, 3400)));

    System.out.println(controller.convert(new Point2D(850, 0)));
  }
}
