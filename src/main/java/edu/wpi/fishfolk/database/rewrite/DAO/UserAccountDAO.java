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
    this.headers = new ArrayList<>(List.of("username", "passwordhash", "permissions"));
  }

  @Override
  public boolean insertEntry(UserAccount entry) {
    return false;
  }

  @Override
  public boolean updateEntry(UserAccount entry) {
    return false;
  }

  @Override
  public boolean removeEntry(UserAccount entry) {
    return false;
  }

  @Override
  public UserAccount getEntry(String identifier) {
    return null;
  }

  @Override
  public ArrayList<UserAccount> getAllEntries() {
    return null;
  }

  @Override
  public boolean updateDatabase() {
    return false;
  }
}
