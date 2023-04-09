package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import edu.wpi.fishfolk.database.MicroNode;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.util.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.kurobako.gesturefx.GesturePane;

public class MapEditorController extends AbsController {

  @FXML MFXComboBox<String> floorSelector;
  @FXML ImageView mapImg;
  @FXML GesturePane pane;
  @FXML public Group drawGroup;
  @FXML MFXButton nextButton;
  @FXML MFXButton backButton;
  private Group nodesGroup;

  private HashMap<String, String> mapImgURLs;
  private HashMap<String, Image> images;

  private int currentFloor = 2;
  private List<MicroNode> unodes;

  public MapEditorController() {
    super();
    // System.out.println("constructed pathfinding controller");
  }

  @FXML
  private void initialize() {
    // pane.centreOn();

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

    nodesGroup = new Group();
    drawGroup.getChildren().add(nodesGroup);

    switchFloor(floors.get(currentFloor));

    floorSelector.setOnAction(
        event -> {
          currentFloor = floors.indexOf(floorSelector.getValue());
          switchFloor(floors.get(currentFloor));
          floorSelector.setText("Floor " + floors.get(currentFloor));
        });
    nextButton.setOnMouseClicked(
        event -> {
          if (currentFloor < floors.size() - 1) {
            currentFloor++;
            switchFloor(floors.get(currentFloor));
            floorSelector.setText("Floor " + floors.get(currentFloor));
          }
        });

    backButton.setOnMouseClicked(
        event -> {
          if (currentFloor > 0) {
            currentFloor--;
            switchFloor(floors.get(currentFloor));
            floorSelector.setText("Floor " + floors.get(currentFloor));
          }
        });
    pane.centreOn(new Point2D(1700, 1100));
    pane.zoomTo(0.4, new Point2D(2500, 1600));
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

  private void drawNode(MicroNode unode) {

    Point2D p = unode.point;
    Circle c = new Circle(p.getX(), p.getY(), 4);
    c.setStrokeWidth(5);
    // c.setFill(Color.TRANSPARENT);
    c.setFill(Color.rgb(12, 212, 252));
    c.setStroke(Color.rgb(12, 212, 252)); // #208036

    nodesGroup.getChildren().add(c);
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
