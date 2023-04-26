package edu.wpi.fishfolk.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  ADMIN_DASHBOARD("views/AdminDashboard.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/NewSignage.fxml"),

  NEW_FOOD_ORDER("views/NewFoodOrder.fxml"),
  SUPPLIES_REQUEST("views/NewSupplyOrder.fxml"),
  FURNITURE_REQUEST("views/NewFurnitureOrder.fxml"),
  FLOWER_REQUEST("views/NewFlowerOrder.fxml"),

  VIEW_MASTER_ORDER("views/MasterViewOrder.fxml"),

  PATHFINDING("views/Pathfinding.fxml"),

  MAP_EDITOR("views/MapEditor.fxml"),
  SIGNAGE_EDITOR("views/SignageEditor.fxml"),
  NEW_SIGNAGE("views/NewSignage.fxml"),
  MOVE_EDITOR("views/MoveEditor.fxml"),

  LOGIN("views/Login.fxml"),

  ACCOUNT_MANAGER("views/AccountManager.fxml"),
  STAFF_DASHBOARD("views/StaffDashboard.fxml"),

  TEMPLATE("views/Template.fxml"),
  CONFERENCE("views/NewConference.fxml"),

  CREDITS("views/Credits.fxml"),
  ABOUTME("views/AboutMe.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
