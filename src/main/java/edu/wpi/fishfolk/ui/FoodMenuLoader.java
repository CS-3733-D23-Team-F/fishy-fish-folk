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

    returnable.add(
            new NewFoodMenuItem(
                    "Garden Salad",
                    "Have you ever wanted to eat a garden? Well now you can! Introducing the worlds first edible garden, dirt included. Please don't plant rhubarb.",
                    "images/gardensalad.png",
                    2.49f,
                    FoodCategory.app));

    returnable.add(
            new NewFoodMenuItem(
                    "Fruit Salad",
                    "Fruit selection rotates weekly! Please note you'll get a third as much fruit salad as any other kind of salad, for no reason at all!",
                    "images/fruitsalad.png",
                    2.99f,
                    FoodCategory.app));

    returnable.add(
            new NewFoodMenuItem(
                    "Caesar Salad",
                    "Did you know you're supposed to pronounce it 'Kaiser'? Don't let Marcus Antonius hear you say it wrong.",
                    "images/caesarsalad.png",
                    2.49f,
                    FoodCategory.app));

    returnable.add(
            new NewFoodMenuItem(
                    "Strawberry Yogurt",
                    "Our food provider cannot guarantee that this product is yogurt, they keep saying something about Icelandic skyr being 'close enough'. Maybe that's why its so expensive.",
                    "images/strawberryyogurt.jpg",
                    5.29f,
                    FoodCategory.app));

    returnable.add(
            new NewFoodMenuItem(
                    "Milk",
                    "Lactose intolerance will not be tolerated on these premises.",
                    "images/milk.png",
                    1.99f,
                    FoodCategory.drink));

    returnable.add(
            new NewFoodMenuItem(
                    "Water",
                    "Fun fact: You have most likely drunk water that a dinosaur urinated a few million years ago. Have a nice day!",
                    "images/water.png",
                    2.49f,
                    FoodCategory.drink));

    returnable.add(
            new NewFoodMenuItem(
                    "Cola",
                    "The healthiest drink on our menu, Cola has many properties that make it ideal for recovering patients. Thanks for the corporate money!",
                    "images/cola.png",
                    1.99f,
                    FoodCategory.drink));

    return returnable;
  }
}
