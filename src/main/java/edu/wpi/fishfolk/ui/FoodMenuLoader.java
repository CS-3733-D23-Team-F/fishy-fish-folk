package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuLoader {
  public static List<NewFoodMenuItem> loadItems() {
    List<NewFoodMenuItem> returnable = new ArrayList<NewFoodMenuItem>();
    returnable.add(
        new NewFoodMenuItem(
            "Chocolate Cake",
            "A slice of Delicious, Scrumptious, Overly well described Chocolate Cake. Optionally served with whipped cream.",
            "images/chocoCake.jpg",
            3.50f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Orange Juice",
            "It's orange juice, what else did you expect me to write here? Oh, with pulp.",
            "images/OJ.jpg",
            1.70f,
            FoodCategory.dessert));

    return returnable;
  }
}
