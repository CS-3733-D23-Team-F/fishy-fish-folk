package edu.wpi.fishfolk;

import static edu.wpi.fishfolk.util.PermissionLevel.GUEST;
import static edu.wpi.fishfolk.util.PermissionLevel.ROOT;

import edu.wpi.fishfolk.controllers.RootController;
import edu.wpi.fishfolk.database.TableEntry.UserAccount;
import edu.wpi.fishfolk.navigation.Screen;
import lombok.Setter;

public class SharedResources {
  /** Stores the currently logged in user. */
  private static UserAccount currentAccount = new UserAccount("extremely_guest", "", "", GUEST);

  @Setter private static RootController rootController;
  @Setter public static String defaultLocation = "Watkins Kiosk 1";

  /**
   * Attempt to log into an account. If the user is already logged in, only log them out if the
   * attempt is successful.
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
      // update RootController permissions
      if (rootController != null) rootController.updatePermissionsAccess();
      return true;
    }
    // not validated
    return false;
  }

  public static UserAccount getCurrentUser() {
    return currentAccount;
  }

  public static String getUsername() {
    return currentAccount.getUsername();
  }

  public static boolean isRoot() {
    return currentAccount.getLevel() == ROOT;
  }

  /** Log out of the currently logged in account. If not logged in, do nothing. */
  public static void logout() {
    currentAccount = new UserAccount("extremely_guest", "", "", GUEST);
    rootController.updatePermissionsAccess();
  }

  public static Screen getHome() {

    switch (currentAccount.getLevel()) {
      case ROOT:
      case ADMIN:
        return Screen.ADMIN_DASHBOARD;

      case STAFF:
        return Screen.STAFF_DASHBOARD;

      case GUEST:
      default:
        return Screen.SIGNAGE;
    }
  }
}
