package edu.wpi.fishfolk.controllers;
import java.time.LocalDateTime;
import java.util.ArrayList;

import edu.wpi.fishfolk.FoodItem;
import edu.wpi.fishfolk.FoodOrder;

public class FoodOrderController {
    FoodOrder currentOrder;
    ArrayList<FoodItem> menu;
    int hoursAhead = 0;
    int currentPage = 0; //for menus with more than 3 items
    int[] itemQuantities;


    void init(){
        currentOrder = new FoodOrder();
        loadMenu();
        itemQuantities = new int[menu.size()];
        currentPage = 0;
        hoursAhead = 0;
    }


    void loadMenu(){
        menu.add(FoodItem.generic1); menu.add(FoodItem.generic2); menu.add(FoodItem.generic3);
    }

    void nextPage (){
        //todo
    }

    void prevPage (){
        //todo
    }

    void setTimeAsap(){
        currentOrder.deliveryTime = LocalDateTime.now().plusMinutes(15);
    }
    void setTimeHours(int hours){
        currentOrder.deliveryTime = LocalDateTime.now().plusHours(hours);
    }
}
