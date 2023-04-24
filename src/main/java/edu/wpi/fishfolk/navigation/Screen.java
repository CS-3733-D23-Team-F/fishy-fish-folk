package edu.wpi.fishfolk.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/Signage.fxml"),

  NEW_FOOD_ORDER("views/NewFoodOrder.fxml"),
  SUPPLIES_REQUEST("views/NewSupplyOrder.fxml"),
  FURNITURE_REQUEST("views/NewFurnitureOrder.fxml"),
  FLOWER_REQUEST("views/NewFlowerOrder.fxml"),

  VIEW_MASTER_ORDER("views/MasterViewOrder.fxml"),

  PATHFINDING("views/Pathfinding.fxml"),

  MAP_EDITOR("views/MapEditor.fxml"),
  MOVE_EDITOR("views/MoveEditor.fxml"),

  LOGIN("views/Login.fxml"),

  ACCOUNT_MANAGER("views/AccountManager.fxml"),

  TEMPLATE("views/Template.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
