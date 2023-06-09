package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuLoader {
  /**
   * Prepares a list of food menu items to display
   *
   * @return The list of all menu items
   */
  public static List<NewFoodMenuItem> loadItems() {
    List<NewFoodMenuItem> returnable = new ArrayList<NewFoodMenuItem>();
    returnable.add(
        new NewFoodMenuItem(
            "Chocolate Cake",
            "A slice of Delicious, Scrumptious, Overly well described Chocolate Cake. Optionally served with whipped cream.",
            "images/Food/chocoCake.png",
            3.50f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Orange Juice",
            "It's orange juice, what else did you expect me to write here? Oh, it comes with pulp.",
            "images/Food/OJ.png",
            1.70f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Apple Pie",
            "A slice of house-made apple pie. This description used to be about Orange Juice, but I decided that wasn't useful.",
            "images/Food/aplPie.png",
            4.33f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Cherry Pie",
            "Cherries. In a pie. Isn't that wonderful? And, for less than the price of apple pie! What isn't to love?",
            "images/Food/cherryPie.png",
            4.07f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Garden Salad",
            "Have you ever wanted to eat a garden? Well now you can! Introducing the world's first edible soil. Please don't plant rhubarb.",
            "images/Food/gardensalad.png",
            2.49f,
            FoodCategory.app));

    returnable.add(
        new NewFoodMenuItem(
            "Fruit Salad",
            "Fruit selection rotates weekly! Please note you'll get a third as much fruit salad as any other kind of salad, for no reason at all!",
            "images/Food/fruitsalad.png",
            2.99f,
            FoodCategory.app));

    returnable.add(
        new NewFoodMenuItem(
            "Caesar Salad",
            "Did you know you're supposed to pronounce it 'Kaiser'? Don't let Marcus Antonius hear you say it wrong.",
            "images/Food/caesarsalad.png",
            2.49f,
            FoodCategory.app));

    returnable.add(
        new NewFoodMenuItem(
            "Strawberry Yogurt",
            "Our food provider cannot guarantee that this product is yogurt, they keep saying something about Icelandic skyr being 'close enough'.",
            "images/Food/strawberryyogurt.png",
            5.29f,
            FoodCategory.app));

    returnable.add(
        new NewFoodMenuItem(
            "Milk",
            "Lactose intolerance will not be tolerated on these premises.",
            "images/Food/milk.png",
            1.99f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Water",
            "Fun fact: You have most likely drunk water that a dinosaur urinated a few million years ago. Have a nice day!",
            "images/Food/water.png",
            2.49f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Cola",
            "The healthiest drink on our menu, Cola has many properties that make it ideal for recovering patients. Thanks for the corporate money!",
            "images/Food/cola.png",
            1.99f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Chicken Tenders",
            "How many chickens could a chicken tender tend if a chicken tender could tend chickens? Find out next time on Total Drama Island.",
            "images/Food/chickentenders.png",
            7.59f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "Hamburger",
            "Hamburger hamburger hamburger hamburger hamburger hamburger. Hamburger? Hamburger, hamburger hamburger!",
            "images/Food/hamburger.png",
            8.24f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "Cheeseburger",
            "I'll make you feel as if you're eating the first cheeseburger you ever ate. The cheap one your parents could barely afford. -Chef Slowik",
            "images/Food/cheeseburger.png",
            8.74f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "Veggie Burger",
            "Look, man, I don't know if the vegans got this one right. Have you ever had a genuinely good veggie burger?",
            "images/Food/veggieburger.png",
            10.74f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "French Fries",
            "Hey man, the French Revolution was bad enough, don't give them any more ideas...",
            "images/Food/frenchfries.png",
            5.49f,
            FoodCategory.side));

    returnable.add(
        new NewFoodMenuItem(
            "Applesauce",
            "Crush some apples, add sugar, and cook. You'll get cider or applesauce and honestly, both are great.",
            "images/Food/applesauce.png",
            4.49f,
            FoodCategory.side));

    returnable.add(
        new NewFoodMenuItem(
            "Mozzarella Sticks",
            "Cheese covered in bread and cooked at high temperature in oil. Could you eat any more lipids and carbohydrates in a single meal?",
            "images/Food/mozzarella.png",
            6.33f,
            FoodCategory.side));

    returnable.add(
        new NewFoodMenuItem(
            "Onion Rings",
            "If you loved it, then you shoulda put a ring on it. Woah oh oh, oh oh oh, ohwoah oh oh, oh oh oh!",
            "images/Food/onionrings.png",
            5.79f,
            FoodCategory.side));

    returnable.add(
        new NewFoodMenuItem(
            "Dumplings",
            "Pork and Cabbage dumplings, served boiled. We're aware that's the worst way, we just don't care.",
            "images/Food/dump.png",
            8f,
            FoodCategory.app));

    returnable.add(
        new NewFoodMenuItem(
            "Ravioli",
            "Delicious meat, wrapped in dough, covered in sauce, Please tell us if you know of more layers, we need to go deeper.",
            "images/Food/rav.png",
            8.99f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "Spaghetti",
            "You toucha my Spaghet! You'll pay for that. You'll pay 11.99, to be exact. At least you'll also get meatballs",
            "images/Food/spaghet.png",
            11.99f,
            FoodCategory.main));

    returnable.add(
        new NewFoodMenuItem(
            "Meatball",
            "A spicy meatball. Devilishly spicy. So much so we priced it after him.",
            "images/Food/meatball.png",
            6.66f,
            FoodCategory.side));

    returnable.add(
        new NewFoodMenuItem(
            "Strawberry Shake",
            "It's so nice, we added it twice! Also, we couldn't decide which category to put it in so we just did both.",
            "images/Food/milkshake.png",
            7.50f,
            FoodCategory.drink));

    returnable.add(
        new NewFoodMenuItem(
            "Strawberry Shake",
            "It's so nice, we added it twice! Also, we couldn't decide which category to put it in so we just did both.",
            "images/Food/milkshake.png",
            7.50f,
            FoodCategory.dessert));

    returnable.add(
        new NewFoodMenuItem(
            "Brownie",
            "Some people claim that the best flavor of ice cream is cookie dough. These people have not heard of brownie cookie dough.",
            "images/Food/brownie.png",
            5.49f,
            FoodCategory.dessert));

    return returnable;
  }
}
