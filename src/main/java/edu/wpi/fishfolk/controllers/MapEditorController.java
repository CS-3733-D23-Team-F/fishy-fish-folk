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

  private BuildingRegion shapiroBuilding;

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



    Polygon shapiroPoly = new Polygon();
    shapiroPoly.getPoints().addAll(
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
            1772.533, 1802.667
    );
    ArrayList<Polygon> shapiroPolyList = new ArrayList<Polygon>();
    shapiroPolyList.add(shapiroPoly);
    shapiroBuilding = new BuildingRegion(shapiroPolyList, "Shapiro", "1");

    // prints mouse location to screen when clicked on map. Used to calculate building boundaries
    mapImg.setOnMouseClicked(
            event -> {
              System.out.println("X: " + event.getX() + " Y: " + event.getY());
              Point2D currPoint = new Point2D(event.getX(), event.getY());
              if(shapiroBuilding.isWithinRegion(currPoint, allFloors.get(currentFloor))){
                System.out.println("I'm in Shapiro Building");
              }else{
                System.out.println("I'm outside");
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
