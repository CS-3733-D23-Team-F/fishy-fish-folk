package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class FlowerOrderController extends AbsController {

  @FXML AnchorPane itemPane;

  @FXML ScrollPane itemWindow;

  @FXML MFXButton ClearButton;

  int numflowers;

  String[] namesFlowers;

  double[] prices;

  int[] flowerAmounts;

  @FXML
  private void initialize() {

    numflowers = 18;
    itemPane.setMinHeight(100 + 300 * numflowers);
    namesFlowers = new String[numflowers];
    prices = new double[numflowers];
    flowerAmounts = new int[numflowers];

    for (int i = 0; i < numflowers; i++) {
      prices[i] = 100.00;
      namesFlowers[i] = "Flower #" + (i + 1);
      addFlower(i);
    }
  }

  private void addFlower(int numAdded) {
    numAdded = numAdded + 1;
    int yAdjust = 0;
    int xAdjust = 0;
    if (numAdded % 2 == 0) {
      yAdjust = -300;
      xAdjust = 450;
    }
    Group g = new Group();

    ImageView i = new ImageView();
    i.setImage(new Image(Fapp.class.getResourceAsStream("images/Flower1.png")));
    i.setY(-250 + (300 * numAdded) + yAdjust);
    i.setX(50 + xAdjust);
    i.setVisible(true);
    g.getChildren().add(i);

    Text flowerName = new Text();
    flowerName.setText(namesFlowers[numAdded - 1]);
    flowerName.setY(150 + (300 * numAdded) + yAdjust);
    flowerName.setX(165 + xAdjust);
    flowerName.setFont(new Font("Courier", 30));
    flowerName.setTextAlignment(TextAlignment.CENTER);
    flowerName.setVisible(true);
    g.getChildren().add(flowerName);

    Text flowerPrice = new Text();
    flowerPrice.setText("$" + prices[numAdded - 1]);
    flowerPrice.setY(190 + (300 * numAdded) + yAdjust);
    flowerPrice.setX(175 + xAdjust);
    flowerPrice.setFont(new Font("Courier", 30));
    flowerPrice.setTextAlignment(TextAlignment.CENTER);
    flowerPrice.setVisible(true);
    g.getChildren().add(flowerPrice);

    MFXButton addCartButton = new MFXButton();
    addCartButton.setLayoutX(145 + xAdjust);
    addCartButton.setLayoutY(210 + (300 * numAdded) + yAdjust);
    addCartButton.setMinHeight(50);
    addCartButton.setMinWidth(200);
    addCartButton.setStyle(ClearButton.getStyle());
    addCartButton.setTextFill(Color.WHITE);
    addCartButton.setText("Add to Cart");

    int flowerID = numAdded - 1;
    addCartButton.setOnMouseClicked(
        event -> {
          addToCart(flowerID);
        });

    g.getChildren().add(addCartButton);

    itemPane.getChildren().add(g);
  }

  public void addToCart(int itemNum) {
    flowerAmounts[itemNum] = flowerAmounts[itemNum] + 1;
  }
}
