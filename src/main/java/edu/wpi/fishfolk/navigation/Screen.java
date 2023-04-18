package edu.wpi.fishfolk.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/Signage.fxml"),
  FOOD_ORDER_REQUEST("views/FoodOrder.fxml"),
  SUPPLIES_REQUEST("views/SupplyRequest.fxml"),
  FURNITURE_REQUEST("views/FurnitureOrder.fxml"),
  VIEW_FOOD_ORDERS("views/ViewFoodOrders.fxml"),
  VIEW_SUPPLY_ORDERS("views/ViewSupplyOrders.fxml"),
  VIEW_FURNITURE_ORDERS("views/ViewFurnitureOrders.fxml"),
  PATHFINDING("views/Pathfinding.fxml"),

  MAP_EDITOR("views/MapEditor.fxml"),
  MOVE_EDITOR("views/MoveEditor.fxml"),

  LOGIN("views/Login.fxml"),


  TEMPLATE("views/Template.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
