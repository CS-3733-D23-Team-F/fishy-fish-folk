package edu.wpi.fishfolk.controllers;
import java.util.ArrayList;

import edu.wpi.fishfolk.FoodItem;
import edu.wpi.fishfolk.FoodOrder;

public class FoodOrderController {
    FoodOrder currentOrder;
    ArrayList<FoodItem> menu;
    int currentPage = 0; //for menus with more than 3 items


    void init(){
        currentOrder = new FoodOrder();
        menu.add(FoodItem.generic1); menu.add(FoodItem.generic2); menu.add(FoodItem.generic3);
    }

    void nextPage (){
        //todo
    }

    void prevPage (){
        //todo
    }
}
