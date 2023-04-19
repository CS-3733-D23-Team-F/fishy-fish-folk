package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;

import edu.wpi.fishfolk.util.PermissionLevel;

import lombok.Getter;
import lombok.Setter;

public class UserAccount {

  @Getter private final String username;
  @Getter @Setter private String password;

  @Getter @Setter
  private String email; // TODO check email format using regex in constructor & setter

  @Getter @Setter private PermissionLevel level;

  // for database versioning
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: User Account
   *
   * @param username Username of account
   * @param password Hashed password of account (NOT PLAINTEXT)
   * @param email Email address of account
   * @param permissionLevel Permission level of account
   */
  public UserAccount(
      String username, String password, String email, PermissionLevel permissionLevel) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.level = permissionLevel;
  }

  @Override
  public boolean equals(Object other) {

    if (other instanceof UserAccount) {
      return username.equals(((UserAccount) other).email);

    } else return false;
  }
}
