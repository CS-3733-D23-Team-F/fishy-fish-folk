package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Edge extends TableEntry {

  public String edgeid;
  public String startnode;
  public String endnode;

  public Edge(String edgeid, String startnode, String endnode) {
    this.edgeid = edgeid;
    this.startnode = startnode;
    this.endnode = endnode;
  }

  @Override
  public TableEntry construct(ArrayList<String> data) {
    if(data.size() < 3 || data.size() > 3){
      return null;
    }
    return new Edge(
            data.get(0),
            data.get(1),
            data.get(2));
  }

  @Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> data = new ArrayList<>();
    data.add(edgeid);
    data.add(startnode);
    data.add(endnode);
    return data;
  }
}
