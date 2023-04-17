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
            "It's orange juice, what else did you expect me to write here? Oh, it comes with pulp.",
            "images/OJ.jpg",
            1.70f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Apple Pie",
            "A slice of house-made apple pie. This description used to be about Orange Juice, but I decided that wasn't useful.",
            "images/aplPie.jpg",
            4.33f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Cherry Pie",
            "Cherries. In a pie. Isn't that wonderful? And, for less than the price of apple pie! What isn't to love?",
            "images/cherryPie.jpg",
            4.07f,
            FoodCategory.dessert));

    return returnable;
  }
}
