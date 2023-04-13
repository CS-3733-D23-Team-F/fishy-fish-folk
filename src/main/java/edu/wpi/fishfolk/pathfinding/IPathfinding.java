package edu.wpi.fishfolk.pathfinding;

import java.util.ArrayList;

@Deprecated
public interface IPathfinding {

  public ArrayList<Path> pathfind(int start, int end, Graph graph, boolean stairs);
}
