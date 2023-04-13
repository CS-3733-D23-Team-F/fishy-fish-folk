package edu.wpi.fishfolk.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/Signage.fxml"),
  FOOD_ORDER_REQUEST("views/FoodOrder.fxml"),
  SUPPLIES_REQUEST("views/SupplyRequest.fxml"),
  FURNITURE_REQUEST("views/FurnitureOrder.fxml"),
  MAP_EDITOR("views/MapEditor.fxml"),
  VIEW_FOOD_ORDERS("views/ViewFoodOrders.fxml"),
  VIEW_SUPPLY_ORDERS("views/ViewSupplyOrders.fxml"),
  VIEW_FURNITURE_ORDERS("views/ViewFurnitureOrders.fxml"),
  PATHFINDING("views/Pathfinding.fxml"),

  OLD_MAP_EDITOR("views/OldMapEditor.fxml"),
  TEMPLATE("views/Template.fxml");
  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
