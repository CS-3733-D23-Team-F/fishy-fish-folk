package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.BuildingRegion;
import edu.wpi.fishfolk.database.CircleNode;
import edu.wpi.fishfolk.database.MicroNode;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.awt.*;
import java.util.*;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import net.kurobako.gesturefx.GesturePane;

public class MapEditorController extends AbsController {

  @FXML MFXComboBox<String> floorSelector;
  @FXML ImageView mapImg;
  @FXML GesturePane pane;
  @FXML public Group drawGroup;
  @FXML MFXButton nextButton;
  @FXML MFXButton backButton;

  @FXML MFXTextField xMicroNodeText;
  @FXML MFXTextField yMicroNodeText;
  @FXML MFXTextField buildingMicroNodeText;
  @FXML MFXTextField floorMicroNodeText;

  private Group nodesGroup;

  private int currentFloor = 2;
  private List<MicroNode> unodes;

  private ArrayList<BuildingRegion> shapiroBuilding;
  private ArrayList<BuildingRegion> btmBuilding;

  private ArrayList<BuildingRegion> towerBuilding;

  private ArrayList<ArrayList<BuildingRegion>> buildings;

  private BuildingRegion shapiroFloor1, shapiroFloor2, shapiroFloor3, shapiroL1, shapiroL2;
  private BuildingRegion btmFloor1, btmFloor2, btmFloor3, btmL1, btmL2;
  private BuildingRegion towerFloor1, towerFloor2, towerFloor3, towerL1, towerL2;

  public MapEditorController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    // pane.centreOn();

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(allFloors);
    Collections.reverse(floorsReverse);

    floorSelector.getItems().addAll(floorsReverse);

    mapImgURLs = new HashMap<>();

    mapImgURLs.put("L1", "map/00_thelowerlevel1.png");
    mapImgURLs.put("L2", "map/00_thelowerlevel2.png");
    mapImgURLs.put("GG", "map/00_thegroundfloor.png");
    mapImgURLs.put("1", "map/01_thefirstfloor.png");
    mapImgURLs.put("2", "map/02_thesecondfloor.png");
    mapImgURLs.put("3", "map/03_thethirdfloor.png");

    images = new HashMap<>();

    for (String floor : mapImgURLs.keySet()) {
      images.put(floor, new Image(Fapp.class.getResourceAsStream(mapImgURLs.get(floor))));
    }

    nodesGroup = new Group();
    drawGroup.getChildren().add(nodesGroup);

    switchFloor(allFloors.get(currentFloor));

    floorSelector.setOnAction(
        event -> {
          currentFloor = allFloors.indexOf(floorSelector.getValue());
          switchFloor(allFloors.get(currentFloor));
          floorSelector.setText("Floor " + allFloors.get(currentFloor));
        });
    nextButton.setOnMouseClicked(
        event -> {
          if (currentFloor < allFloors.size() - 1) {
            currentFloor++;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText("Floor " + allFloors.get(currentFloor));
          }
        });

    backButton.setOnMouseClicked(
        event -> {
          if (currentFloor > 0) {
            currentFloor--;
            switchFloor(allFloors.get(currentFloor));
            floorSelector.setText("Floor " + allFloors.get(currentFloor));
          }
        });
    pane.centreOn(new Point2D(1700, 1100));
    pane.zoomTo(0.4, new Point2D(2500, 1600));

    Polygon shapiroPoly1 = new Polygon();
    Polygon shapiroPoly23 = new Polygon();
    Polygon shapiroPolyL1L2 = new Polygon();

    Polygon btmPoly1 = new Polygon();
    Polygon btmPoly23 = new Polygon();
    Polygon btmPolyL1 = new Polygon();
    Polygon btmPolyL2 = new Polygon();

    Polygon towerPoly1 = new Polygon();

    Polygon towerPoly2 = new Polygon();
    Polygon towerPoly3 = new Polygon();
    Polygon towerPolyL1 = new Polygon();
    Polygon towerPolyL2 = new Polygon();

    shapiroPoly1
        .getPoints()
        .addAll(
            1774.133, 2266.667,
            1095.733, 2263.467,
            1081.333, 1839.467,
            1126.133, 1839.467,
            1129.333, 1799.467,
            1162.933, 1799.467,
            1162.933, 1748.267,
            1270.133, 1753.067,
            1271.733, 1769.067,
            1751.733, 1773.867,
            1751.733, 1799.467,
            1772.533, 1802.667);

    shapiroPoly23
        .getPoints()
        .addAll(
            1114.4, 1742.933,
            1111.2, 2237.333,
            1749.6, 2238.933,
            1751.2, 1741.333);

    shapiroPolyL1L2
        .getPoints()
        .addAll(
            1769.867, 2281.6,
            1638.667, 2280.0,
            1638.667, 2259.2,
            1094.667, 2256.0,
            1083.467, 1881.6,
            1096.267, 1868.799,
            1093.067, 1721.6,
            1157.067, 1694.399,
            1160.267, 1625.6,
            1784.267, 1619.199,
            1769.867, 1710.399);

    btmPoly1
        .getPoints()
        .addAll(
            948.533, 2477.333,
            1022.133, 2474.133,
            1049.333, 2470.933,
            1058.933, 2461.333,
            1490.933, 2458.133,
            1618.933, 2469.333,
            1622.133, 2434.133,
            1676.533, 2443.733,
            1742.133, 2442.133,
            1759.733, 2608.533,
            1780.533, 2608.533,
            1783.733, 2794.133,
            1762.933, 2797.333,
            1762.933, 3096.533,
            929.333, 3098.133,
            975.733, 2722.133,
            948.533, 2562.133);

    btmPoly23
        .getPoints()
        .addAll(
            1114.399, 1742.933,
            1752.799, 1744.533,
            1754.399, 2240.533,
            1114.399, 2237.333);

    btmPolyL1
        .getPoints()
        .addAll(
            936.799, 2460.799,
            1013.599, 2459.2,
            1015.199, 2452.799,
            1554.399, 2455.999,
            1672.799, 2435.2,
            1672.799, 2427.2,
            1735.199, 2430.399,
            1741.6, 2454.399,
            1843.999, 2457.599,
            1845.599, 3113.6,
            1087.199, 3116.799,
            1069.6, 3081.599,
            919.199, 3081.599,
            967.199, 2703.999,
            941.599, 2543.999);

    btmPolyL2
        .getPoints()
        .addAll(
            1019.999, 2457.599,
            1848.799, 2459.2,
            1848.799, 3113.6,
            1091.999, 3115.2,
            1018.399, 2991.999);

    towerPoly1
        .getPoints()
        .addAll(
            1123.733, 1217.067,
            1125.333, 641.067,
            1386.133, 644.267,
            1384.533, 708.267,
            1355.733, 706.667,
            1358.933, 893.867,
            1486.933, 898.667,
            1424.0, 799.99,
            1452.8, 697.599,
            1544.0, 649.599,
            1616.0, 673.599,
            1665.60, 721.599,
            1694.40, 916.79,
            1696.0, 702.399,
            1724.4, 715.199,
            1785.6, 660.799,
            1852.80, 647.999,
            1928.0, 679.999,
            1969.6, 731.199,
            1972.80, 731.199,
            1973.333, 804.267,
            2043.733, 804.267,
            2112.533, 834.667,
            2088.533, 869.867,
            2088.533, 892.267,
            2074.133, 921.067,
            2070.933, 1348.267,
            1352.533, 1340.267,
            1349.333, 1221.867);

    towerPoly2
        .getPoints()
        .addAll(
            1863.2, 788.8,
            1120.8, 1496.0,
            1354.4, 891.2,
            1490.4, 888.0,
            1500.0, 881.6,
            1448.80, 840.0,
            1421.6, 766.4,
            1624.80, 666.666,
            1671.2, 719.466,
            1698.4, 705.066,
            1730.4, 721.066,
            1776.80, 663.467,
            1898.4, 650.667,
            1957.6, 706.667,
            1973.6, 781.867,
            1973.6, 797.867,
            1999.199, 786.667,
            2082.399, 818.667,
            2108.0, 833.067,
            2076.0, 881.067,
            2072.8, 1500.267);

    towerPoly3
        .getPoints()
        .addAll(
            1098.4, 1496.0,
            1306.4, 640.0,
            1336.8, 638.4,
            1370.4, 638.4,
            1370.4, 705.5999999999999,
            1338.4, 707.1999999999999,
            1335.2, 891.1999999999999,
            1456.8000000000002, 894.4,
            1455.2, 878.4,
            1456.8000000000002, 873.5999999999999,
            1428.0, 843.1999999999999,
            1412.0, 804.8,
            1397.6, 796.8,
            1394.4, 790.4,
            1397.6, 775.9999999999999,
            1404.0, 769.5999999999999,
            1415.2, 732.8,
            1429.6, 702.4,
            1426.4, 684.8,
            1437.6, 671.9999999999999,
            1453.6, 678.4,
            1482.4, 659.1999999999999,
            1520.8000000000002, 652.8,
            1532.0, 638.4,
            1546.4, 641.5999999999999,
            1556.0, 655.9999999999999,
            1588.0, 668.8,
            1629.6, 703.9999999999999,
            1644.0, 703.9999999999999,
            1655.2, 718.4,
            1644.0, 732.8,
            1655.2, 767.9999999999999,
            1709.6, 772.8,
            1717.6, 737.5999999999999,
            1711.2, 716.8,
            1720.8000000000002, 700.8,
            1733.6, 702.4,
            1764.0, 676.8,
            1807.2, 654.4,
            1812.0, 641.5999999999999,
            1828.0, 638.4,
            1837.6, 654.4,
            1877.6, 662.4,
            1908.0, 676.8,
            1922.4, 675.1999999999999,
            1935.1999999999998, 689.5999999999999,
            1932.0, 697.5999999999999,
            1949.6, 731.1999999999999,
            1957.6, 772.8,
            1973.6, 791.9999999999999,
            2087.2, 835.1999999999999,
            2087.2, 846.4,
            2116.0, 844.8,
            2111.2, 1496.0,
            1100.0, 1497.6);

    towerPolyL1
        .getPoints()
        .addAll(
            1090.4, 1537.599999999999,
            1160.8, 1539.1999999999994,
            1160.8, 1688.5333333333326,
            1092.0, 1720.5333333333326,
            1096.8, 1867.7333333333327,
            1084.0, 1878.9333333333327,
            1094.667, 2256.0,
            1083.467, 1881.6,
            1796.0, 1540.2666666666662,
            2146.1333333333337, 1537.0666666666657,
            1795.2000000000003, 1540.2666666666655,
            2150.4, 1540.2666666666655);

    towerPolyL2
        .getPoints()
        .addAll(
            1796.2666666666667, 1540.266666666666,
            2151.4666666666667, 1537.0666666666662,
            1849.0666666666666, 596.266666666666,
            1849.0666666666666, 565.8666666666659,
            1650.6666666666667, 562.6666666666658,
            1650.6666666666667, 596.266666666666,
            1044.2666666666667, 599.466666666666,
            1089.0666666666666, 1535.4666666666658,
            1089.0666666666666, 1535.4666666666658,
            1159.4666666666667, 1541.8666666666657,
            1094.667, 2256.0,
            1083.467, 1881.6);

    shapiroBuilding = new ArrayList<BuildingRegion>();
    btmBuilding = new ArrayList<BuildingRegion>();
    towerBuilding = new ArrayList<BuildingRegion>();

    shapiroFloor1 = new BuildingRegion(shapiroPoly1, "Shapiro", "1");
    shapiroFloor2 = new BuildingRegion(shapiroPoly23, "Shapiro", "2");
    shapiroFloor3 = new BuildingRegion(shapiroPoly23, "Shapiro", "3");
    shapiroL1 = new BuildingRegion(shapiroPolyL1L2, "Shapiro", "L1");
    shapiroL2 = new BuildingRegion(shapiroPolyL1L2, "Shapiro", "L2");

    shapiroBuilding.add(shapiroFloor1);
    shapiroBuilding.add(shapiroFloor2);
    shapiroBuilding.add(shapiroFloor3);
    shapiroBuilding.add(shapiroL1);
    shapiroBuilding.add(shapiroL2);

    btmFloor1 = new BuildingRegion(btmPoly1, "BTM", "1");
    btmFloor2 = new BuildingRegion(btmPoly23, "BTM", "2");
    btmFloor3 = new BuildingRegion(btmPoly23, "BTM", "3");
    btmL1 = new BuildingRegion(btmPolyL1, "BTM", "L1");
    btmL2 = new BuildingRegion(btmPolyL2, "BTM", "L2");

    btmBuilding.add(btmFloor1);
    btmBuilding.add(btmFloor2);
    btmBuilding.add(btmFloor3);
    btmBuilding.add(btmL1);
    btmBuilding.add(btmL2);

    towerFloor1 = new BuildingRegion(towerPoly1, "Tower", "1");
    towerFloor2 = new BuildingRegion(towerPoly2, "Tower", "2");
    towerFloor3 = new BuildingRegion(towerPoly3, "Tower", "3");
    towerL1 = new BuildingRegion(towerPolyL1, "Tower", "L1");
    towerL2 = new BuildingRegion(towerPolyL2, "Tower", "L2");

    towerBuilding.add(towerFloor1);
    towerBuilding.add(towerFloor2);
    towerBuilding.add(towerFloor3);
    towerBuilding.add(towerL1);
    towerBuilding.add(towerL2);

    buildings = new ArrayList<ArrayList<BuildingRegion>>();

    buildings.add(towerBuilding);
    buildings.add(btmBuilding);
    buildings.add(shapiroBuilding);

    // prints mouse location to screen when clicked on map. Used to calculate building boundaries
    mapImg.setOnMouseClicked(
        event -> {
          System.out.println(event.getX() + ", " + event.getY() + ",");
          Point2D currPoint = new Point2D(event.getX(), event.getY());

          for (ArrayList<BuildingRegion> building : buildings) {
            boolean inBuilding = false;
            for (BuildingRegion buildingFloor : building) {
              inBuilding =
                  inBuilding
                      || buildingFloor.isWithinRegion(currPoint, allFloors.get(currentFloor));
            }
            if (inBuilding) {
              System.out.println("I'm in: " + building.get(0).getBuildingName());
            }
          }
        });
  }

  private void switchFloor(String floor) {

    System.out.println("switching floor to " + floor);

    mapImg.setImage(images.get(floor));

    nodesGroup.getChildren().clear();

    unodes =
        dbConnection.micronodeTable.executeQuery("SELECT *", "WHERE floor = '" + floor + "'")
            .stream()
            .map(
                elt -> {
                  MicroNode un = new MicroNode();
                  un.construct(new ArrayList(List.<String>of(elt)));
                  return un;
                })
            .toList();

    System.out.println(unodes.size());

    unodes.forEach(this::drawNode);
  }

  // this also does some initialization now
  private void drawNode(MicroNode unode) {

    Point2D p = unode.point;
    CircleNode c = new CircleNode(unode.id, p.getX(), p.getY(), 4);
    c.setStrokeWidth(5);
    // c.setFill(Color.TRANSPARENT);
    c.setFill(Color.rgb(12, 212, 252));
    c.setStroke(Color.rgb(12, 212, 252)); // #208036

    c.setOnMouseClicked(
        event -> {
          fillMicroNodeFields(event, c);
        });

    nodesGroup.getChildren().add(c);
  }

  /**
   * event handler for mouse click on nodes which checks for double click and fills fields for X and
   * Y location on map, (and TODO: gets info from db about building and floor ?)
   *
   * @param e
   * @param c
   */
  public void fillMicroNodeFields(MouseEvent e, CircleNode c) {
    if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
      System.out.println(
          "I'm circle: "
              + c.getCircleNodeID()
              + " my cord is: "
              + c.getCenterX()
              + " and am being double clicked");
      xMicroNodeText.setText(String.valueOf(c.getCenterX()));
      yMicroNodeText.setText(String.valueOf(c.getCenterY()));
    }
  }

  /*
    public Point2D convert(Point2D p) {

      // center gets mapped to center. center1 -> center2

      // values from viewport
      Point2D center1 = new Point2D(900 + 4050 / 2, 150 + 3000 / 2);

      // fit width/height
      Point2D center2 = new Point2D(810 / 2, 605 / 2);

      Point2D rel = p.subtract(center1); // p relative to the center

      // strech x and y separately
      // fit width/height / img width/height
      double x = rel.getX() * 810 / 4050;
      double y = rel.getY() * 605 / 3000 + 44;

      return new Point2D(x, y).add(center2);
    }
  */

}
