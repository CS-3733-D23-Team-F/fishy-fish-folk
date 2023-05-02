package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CreditsController {

  @FXML ImageView displayImage;

  @FXML Label link;

  @FXML MFXButton hospitalImages, navigation, serviceRequest, misc;
  @FXML MFXScrollPane hospitalImagesPane, navigationPane, serviceRequestPane, miscPane;
  @FXML
  HBox gratitude1,
      gratitude2,
      gratitude3,
      gratitude4,
      gratitude5,
      spring1,
      spring2,
      spring3,
      spring4,
      spring5,
      sympathy1,
      sympathy2,
      sympathy3,
      sympathy4,
      sympathy5;
  @FXML
  HBox pie,
      sauce,
      brownie,
      csalad,
      cburger,
      cpie,
      ctenders,
      ccake,
      cola,
      dump,
      fries,
      fsalad,
      gsalad,
      hamburger,
      meatball,
      milk,
      mshake,
      mozz,
      juice,
      orings,
      rav,
      spaghet,
      syogurt,
      vburger,
      water;
  @FXML
  HBox mapEditor,
      signage,
      request,
      pathfinding,
      up,
      down,
      upArrow,
      left,
      right,
      accounts,
      viewOrders;
  @FXML HBox hospitalImage, hospitalIcon;

  @FXML
  public void initialize() {

    hospitalImages.setOnMouseClicked(
        event -> {
          hospitalImagesPane.setVisible(true);
          hospitalImagesPane.setDisable(false);
          miscPane.setVisible(false);
          miscPane.setDisable(true);
          navigationPane.setVisible(false);
          navigationPane.setDisable(true);
          serviceRequestPane.setVisible(false);
          serviceRequestPane.setDisable(true);
        });

    navigation.setOnMouseClicked(
        event -> {
          hospitalImagesPane.setVisible(false);
          hospitalImagesPane.setDisable(true);
          miscPane.setVisible(false);
          miscPane.setDisable(true);
          navigationPane.setVisible(true);
          navigationPane.setDisable(false);
          serviceRequestPane.setVisible(false);
          serviceRequestPane.setDisable(true);
        });

    serviceRequest.setOnMouseClicked(
        event -> {
          hospitalImagesPane.setVisible(false);
          hospitalImagesPane.setDisable(true);
          miscPane.setVisible(false);
          miscPane.setDisable(true);
          navigationPane.setVisible(false);
          navigationPane.setDisable(true);
          serviceRequestPane.setVisible(true);
          serviceRequestPane.setDisable(false);
        });

    misc.setOnMouseClicked(
        event -> {
          hospitalImagesPane.setVisible(false);
          hospitalImagesPane.setDisable(true);
          miscPane.setVisible(true);
          miscPane.setDisable(false);
          navigationPane.setVisible(false);
          navigationPane.setDisable(true);
          serviceRequestPane.setVisible(false);
          serviceRequestPane.setDisable(true);
        });

    gratitude1.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/gratitude-1.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    gratitude2.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/gratitude-2.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    gratitude3.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/gratitude-3.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    gratitude4.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/gratitude-4.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    gratitude5.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/gratitude-5.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    spring1.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Spring-1.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    spring2.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Spring-2.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    spring3.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Spring-3.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    spring4.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Spring-4.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    spring5.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Spring-5.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    sympathy1.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Sympathy-1.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    sympathy2.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Sympathy-2.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    sympathy3.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Sympathy-3.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    sympathy4.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Sympathy-4.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    sympathy5.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Flowers/Sympathy-5.png")));
          link.setText("https://www.perrosflowers.com/best-sellers/cat1070001");
        });

    pie.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/aplPie.png")));
          link.setText("https://www.flaticon.com/free-icon/apple-pie_3656851");
        });
    sauce.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/applesauce.png")));
          link.setText("https://www.flaticon.com/free-icon/apple-sauce_6271433");
        });
    brownie.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/brownie.png")));
          link.setText("https://www.flaticon.com/free-icon/brownie_7390074");
        });
    csalad.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/caesarsalad.png")));
          link.setText("https://www.flaticon.com/free-icon/salad_1057357");
        });
    cburger.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/cheeseburger.png")));
          link.setText("https://www.flaticon.com/free-icon/cheeseburger_5049506");
        });
    cpie.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/cherryPie.png")));
          link.setText("https://www.flaticon.com/free-icon/pie_8805623");
        });
    ctenders.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/chickentenders.png")));
          link.setText("https://www.flaticon.com/free-icon/fried-chicken_706853");
        });
    ccake.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/chocoCake.png")));
          link.setText("https://www.flaticon.com/free-icon/chocolate-cake_8301787");
        });
    cola.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/cola.png")));
          link.setText("https://www.flaticon.com/free-icon/soda_3076028");
        });
    dump.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/dump.png")));
          link.setText("https://www.flaticon.com/free-icon/gyoza_6632254");
        });
    fries.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/frenchfries.png")));
          link.setText("https://www.flaticon.com/free-icon/fried-potatoes_1046786");
        });
    fsalad.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/fruitsalad.png")));
          link.setText("https://www.flaticon.com/free-icon/fruit-salad_415693");
        });
    gsalad.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/gardensalad.png")));
          link.setText("https://www.flaticon.com/free-icon/salad_189143");
        });
    hamburger.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/hamburger.png")));
          link.setText("https://www.flaticon.com/free-icon/food_10588511");
        });
    meatball.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/meatball.png")));
          link.setText("https://www.flaticon.com/free-icon/meatball_2403398");
        });
    milk.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/milk.png")));
          link.setText("https://www.flaticon.com/free-icon/milk_869664");
        });
    mshake.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/milkshake.png")));
          link.setText("https://www.flaticon.com/free-icon/milkshake_2447812");
        });
    mozz.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/mozzarella.png")));
          link.setText("https://www.flaticon.com/free-icon/mozzarella_7592355");
        });
    juice.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/OJ.png")));
          link.setText("https://www.flaticon.com/free-icon/orange-juice_2442019");
        });
    orings.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/onionrings.png")));
          link.setText("https://www.flaticon.com/free-icon/onion-ring_9391684");
        });
    rav.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/rav.png")));
          link.setText("https://www.flaticon.com/free-icon/ravioli_10367217");
        });
    spaghet.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/spaghet.png")));
          link.setText("https://www.flaticon.com/free-icon/spaghetti_4465708");
        });
    syogurt.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/strawberryyogurt.png")));
          link.setText("https://www.flaticon.com/free-icon/yogurt_3142859");
        });
    vburger.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/Food/veggieburger.png")));
          link.setText("https://www.flaticon.com/free-icon/vegan-burger_2738717");
        });
    water.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/Food/water.png")));
          link.setText("https://www.flaticon.com/free-icon/glass-of-water_3248369");
        });

    hospitalImage.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/hospitalImg.png")));
          link.setText("https://www.brighamandwomens.org/");
        });

    hospitalIcon.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/bwlogo.png")));
          link.setText("https://www.brighamandwomens.org/");
        });

    mapEditor.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/mapEditor.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/drawing_3964687?term=map+and+pencil&page=1&position=8&origin=search&related_id=3964687");
        });

    request.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/serviceRequest.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/desk-bell_7879702?term=service+bell&page=1&position=7&origin=search&related_id=7879702");
        });

    accounts.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(
                  Fapp.class.getResourceAsStream("images/NavigationBar/justAddMorePeople.png")));
          link.setText("https://www.flaticon.com/free-icon/crowd_599979");
        });

    viewOrders.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/anotherTable.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/table_178916?term=table&page=1&position=14&origin=search&related_id=178916");
        });

    signage.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/signage.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/signage_2120245?term=signage&page=1&position=7&origin=search&related_id=2120245");
        });

    pathfinding.setOnMouseClicked(
        event -> {
          displayImage.setImage(
              new Image(Fapp.class.getResourceAsStream("images/NavigationBar/pathfinding.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/maps_2953336?term=map&page=1&position=6&origin=search&related_id=2953336");
        });

    down.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/down.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/down_5529974?term=triangle+down&page=1&position=7&origin=search&related_id=5529974");
        });

    up.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/up.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/up_5436652?term=triangle+up&page=1&position=7&origin=search&related_id=5436652");
        });

    upArrow.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/up-arrow.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/up-arrow_2989972?term=up+arrow&page=1&position=4&origin=search&related_id=2989972");
        });

    left.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/left-turn.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/left-turn_3314251?term=left+turn&page=1&position=2&origin=search&related_id=3314251");
        });

    right.setOnMouseClicked(
        event -> {
          displayImage.setImage(new Image(Fapp.class.getResourceAsStream("images/right-turn.png")));
          link.setText(
              "https://www.flaticon.com/free-icon/right-turn_3314252?term=right+turn&page=1&position=4&origin=search&related_id=3314252");
        });
  }
}
