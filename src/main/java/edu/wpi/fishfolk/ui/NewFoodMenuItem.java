package edu.wpi.fishfolk.ui;

public class NewFoodMenuItem {
    String description;
    String name;
    float price;
    String imageLoc;
    category cat;
    public NewFoodMenuItem(String n, String d, String i, float p, category c) {
        name = n;
        description = d;
        imageLoc = i;
        price = p;
        cat = c;
    }
    public enum category {
        app,
        drink,
        main,
        side,
        dessert;
    }
}
