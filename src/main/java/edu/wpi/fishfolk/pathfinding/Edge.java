package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;
import java.util.ArrayList;

public class Edge extends TableEntry {
  public String node1;
  public String node2;

  public Edge(String node1, String node2) {
    this.id = node1 + "_" + node2;
    this.node1 = node1;
    this.node2 = node2;
  }

  @Override
  public boolean construct(ArrayList<String> data) {

    if (data.size() != 3) {
      return false;
    }

    this.id = data.get(0);
    this.node1 = data.get(1);
    this.node2 = data.get(2);
    return true;
  }

  @Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> data = new ArrayList<>();
    data.add(id);
    data.add(node1);
    data.add(node2);
    return data;
  }
}
