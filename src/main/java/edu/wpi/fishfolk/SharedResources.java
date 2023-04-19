package edu.wpi.fishfolk;

import edu.wpi.fishfolk.database.rewrite.TableEntry.UserAccount;

public class SharedResources {
  /** Stores the currently logged in user. */
  private static UserAccount currentAccount = null;

  /**
   * Attempt to log into an account. If the user is already logged in, only log them out if the attempt is successful.
   *
   * @param acct the account to log into.
   * @param password the password used to log in.
   * @return true if logged in successfully, false otherwise.
   */
  public static boolean login(UserAccount acct, String password) {
    // Please note that acct.getPassword() returns the *hash* of the password as a String.
    if (Integer.parseInt(acct.getPassword()) == password.hashCode()) {
      // validated
      currentAccount = acct;
      return true;
    }
    // not validated
    return false;
  }

  public static UserAccount getCurrentUser() {
    return currentAccount;
  }

  /** Log out of the currently logged in account. If not logged in, do nothing. */
  public static void logout() {
    currentAccount = null;
  }
}
