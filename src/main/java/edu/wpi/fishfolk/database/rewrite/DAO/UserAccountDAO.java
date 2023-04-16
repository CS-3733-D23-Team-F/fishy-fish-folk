package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.UserAccount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAccountDAO implements IDAO<UserAccount> {

  private final ArrayList<String> headers;

  private HashMap<Integer, UserAccount> tableMap;
  private ArrayList<DataEdit<UserAccount>> dataEdits;

  /** DAO for User Account Request table in PostgreSQL database. */
  public UserAccountDAO() {
    this.headers = new ArrayList<>(List.of("username", "passhash", "permissions"));
  }

  @Override
  public void init(boolean drop) {}

  @Override
  public void populateLocalTable() {}

  @Override
  public boolean insertEntry(UserAccount entry) {
    return false;
  }

  @Override
  public boolean updateEntry(UserAccount entry) {
    return false;
  }

  @Override
  public boolean removeEntry(Object identifier) {
    return false;
  }

  @Override
  public UserAccount getEntry(Object identifier) {
    return null;
  }

  @Override
  public ArrayList<UserAccount> getAllEntries() {
    return null;
  }

  @Override
  public void undoChange() {}

  @Override
  public boolean updateDatabase(boolean updateAll) {
    return false;
  }

  @Override
  public boolean importCSV(String filepath, boolean backup) {
    return false;
  }

  @Override
  public boolean exportCSV(String directory) {
    return false;
  }
}
