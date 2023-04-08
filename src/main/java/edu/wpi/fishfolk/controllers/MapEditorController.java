package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.MicroNode;
import java.util.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MapEditorController extends AbsController {

  @FXML ComboBox<String> floorSelector;
  @FXML ImageView mapImg;

  private HashMap<String, String> mapImgURLs;
  private HashMap<String, Image> images;
  private final ArrayList<String> floors = new ArrayList<>(List.of("L2", "L1", "1", "2", "3"));
  private String currentFloor = "1";

  private ArrayList<MicroNode> unodes;

  private Group nodesGroup;

  public MapEditorController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {

    // copy contents, not reference
    ArrayList<String> floorsReverse = new ArrayList<>(floors);
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

    switchFloor(currentFloor);

    floorSelector.setOnAction(
        event -> {
          currentFloor = floorSelector.getValue();
        });
  }

  private void switchFloor(String floor) {

    mapImg.setImage(images.get(floor));

    nodesGroup = new Group();

    unodes =
        (ArrayList<MicroNode>)
            dbConnection.micronodeTable.executeQuery("SELECT *", "WHERE type != 'HALL").stream()
                .map(
                    elt -> {
                      MicroNode un = new MicroNode();
                      un.construct(new ArrayList(List.<String>of(elt)));
                      return un;
                    })
                .toList();

    unodes.forEach(this::drawNode);
  }

  private void drawNode(MicroNode unode) {

    Point2D p = convert(unode.point);
    Circle c = new Circle(p.getX(), p.getY(), 4);
    c.setFill(Color.TRANSPARENT);
    c.setStroke(Color.rgb(32, 128, 54)); // #208036

    nodesGroup.getChildren().add(c);
  }

  public Point2D convert(Point2D p) {

    // center gets mapped to center. center1 -> center2

    // values from viewport
    Point2D center1 = new Point2D(900 + 4050 / 2, 150 + 3000 / 2);

    // fit width/height
    Point2D center2 = new Point2D(900 / 2, 600 / 2);

    Point2D rel = p.subtract(center1); // p relative to the center

    // strech x and y separately
    // fit width/height / img width/height
    double x = rel.getX() * 900 / 4050;
    double y = rel.getY() * 600 / 3000;

    return new Point2D(x, y).add(center2);
  }
}
