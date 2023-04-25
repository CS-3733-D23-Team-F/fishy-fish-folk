package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.List;

public class FlowerMenuLoader {

  public static List<FlowerMenuItem> loadItems() {
    List<FlowerMenuItem> returnable = new ArrayList<FlowerMenuItem>();
    returnable.add(
        new FlowerMenuItem(
            "Natural Beauty",
            "Fresh and vibrant, this bouquet captures the essence of spring with its mix of colorful blooms.",
            "images/flowers/Spring-1.png",
            75.00f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Enchanted Garden",
            "A beautiful combination of soft pastels and bold pops of color, this bouquet is a true springtime treat.",
            "images/flowers/Spring-2.png",
            100.00f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Floral Fantasy",
            "Radiant and full of life, this bouquet evokes the beauty and joy of the season.",
            "images/flowers/Spring-3.png",
            90.0f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Nature's Gift",
            "Rustic and charming, this bouquet combines natural elements with delicate blooms for a unique and beautiful arrangement.",
            "images/flowers/Spring-4.png",
            100.0f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Magical Medley",
            "With its cheerful colors and playful design, this bouquet is sure to brighten anyone's day.",
            "images/flowers/Spring-5.png",
            110.00f,
            FlowerCategory.spring));
    returnable.add(
        new FlowerMenuItem(
            "Tranquility",
            "A beautiful tribute to a life well-lived, this elegant bouquet features pure white blooms that symbolize peace and serenity.",
            "images/flowers/Sympathy-1.png",
            85.00f,
            FlowerCategory.sympathy));
    returnable.add(
        new FlowerMenuItem(
            "Ethereal Beauty",
            "The delicate and ethereal blooms in this bouquet offer a gentle reminder of the beauty and fragility of life.",
            "images/flowers/Sympathy-2.png",
            125.00f,
            FlowerCategory.sympathy));
    returnable.add(
        new FlowerMenuItem(
            "Gentle Comfort",
            "With its soft, graceful blooms, this bouquet offers comfort and consolation during a difficult time.",
            "images/flowers/Sympathy-3.png",
            65.00f,
            FlowerCategory.sympathy));
    returnable.add(
        new FlowerMenuItem(
            "Timeless Grace",
            "Serene and calming, this bouquet is a thoughtful expression of sympathy and support.",
            "images/flowers/Sympathy-4.png",
            95.00f,
            FlowerCategory.sympathy));
    returnable.add(
        new FlowerMenuItem(
            "Pure Serenity",
            "With its gentle curves and soft, flowing blooms, this bouquet evokes a sense of peace and tranquility.",
            "images/flowers/Sympathy-5.png",
            110.00f,
            FlowerCategory.sympathy));
    returnable.add(
        new FlowerMenuItem(
            "Sunny Delight",
            "This beautiful bouquet is the perfect way to show your thanks, with a mix of sunny blooms and lush greens.",
            "images/flowers/gratitude-3.png",
            65.00f,
            FlowerCategory.gratitude));
    returnable.add(
        new FlowerMenuItem(
            "Soft Serenade",
            "With its sweet fragrance and delicate beauty, this charming bouquet is sure to express your gratitude and warm the hearts of your loved ones.",
            "images/flowers/gratitude-4.png",
            90.00f,
            FlowerCategory.gratitude));
    returnable.add(
        new FlowerMenuItem(
            "Garden Symphony",
            "Show your appreciation with this beautiful bouquet of fresh, fragrant flowers, arranged in a classic and timeless style.",
            "images/flowers/gratitude-1.png",
            75.00f,
            FlowerCategory.gratitude));
    returnable.add(
        new FlowerMenuItem(
            "Vibrant Meadows",
            "Say \"thank you\" in style with this stunning arrangement of bright, bold flowers that are sure to make a lasting impression.",
            "images/flowers/gratitude-2.png",
            100.00f,
            FlowerCategory.gratitude));
    returnable.add(
        new FlowerMenuItem(
            "Radiant Bloom",
            "Celebrate the special moments and people in your life with this cheerful and uplifting bouquet of bright and beautiful flowers.",
            "images/flowers/gratitude-5.png",
            75.00f,
            FlowerCategory.gratitude));
    return returnable;
  }
}
