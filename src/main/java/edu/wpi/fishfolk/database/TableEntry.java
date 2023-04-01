package edu.wpi.fishfolk.database;


//TODO: needs starting 5 Subclasses NodeEntry, EdgeEntry, LocEntry, MoveEntry, MicroNodeEntry
import edu.wpi.fishfolk.pathfinding.Node;
import java.util.ArrayList;

public abstract class TableEntry {

  public String id;

  public abstract TableEntry construct(ArrayList<String> data);

  public abstract ArrayList<String> deconstruct();

  @Override
  public boolean equals(Object o) {
    Node other = (Node) o;
    return id.equals(other.id);
  }
}
