package edu.wpi.fishfolk.database.rewrite.TableEntry;

import lombok.Getter;
import lombok.Setter;

public class UserAccount {

  @Getter @Setter private String username;
  @Getter @Setter private String passwordHash;
  @Getter @Setter private String permissions; // TODO: Change to more specialized enum

  // Email

  /**
   * Table entry type: User Account
   *
   * @param username Username of account
   * @param passwordHash Hashed password of account (NOT PLAINTEXT)
   * @param permissions Permission level of account
   */
  public UserAccount(String username, String passwordHash, String permissions) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.permissions = permissions;
  }
}
