package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationDAO implements IDAO<Location> {

  private final ArrayList<String> headers;

  private HashMap<Integer, Location> tableMap;
  private ArrayList<DataEdit<Location>> dataEdits;

  /** DAO for Location table in PostgreSQL database. */
  public LocationDAO() {
    this.headers = new ArrayList<>(List.of("longname", "shortname", "type"));
  }

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(Location entry) {
    return false;
  }

  @Override
  public boolean updateEntry(Location entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Location entry) {
    return false;
  }

  @Override
  public Location getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<Location> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
