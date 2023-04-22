package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;

public class FlowerMenuLoader {

  public static List<FlowerMenuItem> loadItems() {
    List<FlowerMenuItem> returnable = new ArrayList<FlowerMenuItem>();
    returnable.add(
        new FlowerMenuItem(
            "Test Flower",
            "Cool flower description",
            "images/Flower1.png",
            3.50f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Test Flower",
            "Cool flower description",
            "images/Flower1.png",
            3.50f,
            FlowerCategory.exotic));
    returnable.add(
        new FlowerMenuItem(
            "Test Flower",
            "Cool flower description",
            "images/Flower1.png",
            3.50f,
            FlowerCategory.sympathy));
    return returnable;
  }
}
