package edu.wpi.fishfolk.pathfinding;

public enum PathfindSingleton {
  PATHFINDER;

  private IPathfinding pathMethod;

  public IPathfinding getPathMethod() {
    return pathMethod;
  }

  public void setPathMethod(IPathfinding pathMethod) {
    this.pathMethod = pathMethod;
  }
}
