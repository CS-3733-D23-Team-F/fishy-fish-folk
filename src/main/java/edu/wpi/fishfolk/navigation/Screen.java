package edu.wpi.fishfolk.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/Signage.fxml"),
  FOOD_ORDER_REQUEST("views/FoodOrder.fxml"),
  SUPPLIES_REQUEST("views/NewSupplyOrder.fxml"),
  FURNITURE_REQUEST("views/NewFurnitureOrder.fxml"),
  MAP_EDITOR("views/MapEditor.fxml"),
  VIEW_MASTER_ORDER("views/MasterViewOrder.fxml"),
  VIEW_FOOD_ORDERS("views/ViewFoodOrders.fxml"),
  VIEW_SUPPLY_ORDERS("views/ViewSupplyOrders.fxml"),
  FLOWER_REQUEST("views/FlowerOrder.fxml"),
  VIEW_FURNITURE_ORDERS("views/ViewFurnitureOrders.fxml"),
  PATHFINDING("views/Pathfinding.fxml"),
  LOGIN("views/Login.fxml"),
  OLD_MAP_EDITOR("views/OldMapEditor.fxml"),
  NEW_FOOD_ORDER("views/NewFoodOrder.fxml"),
  TEMPLATE("views/Template.fxml"),
  ACCOUNT_MANAGER("views/AccountManager.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
