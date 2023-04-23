package edu.wpi.fishfolk.controllers;

import edu.wpi.fishfolk.database.DAO.Observables.UserAccountObservable;
import edu.wpi.fishfolk.database.TableEntry.TableEntryType;
import edu.wpi.fishfolk.database.TableEntry.UserAccount;
import edu.wpi.fishfolk.util.PermissionLevel;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class AccountManagerController extends AbsController {

  @FXML TableView<UserAccountObservable> userAccountsTable;
  @FXML TableColumn<UserAccountObservable, String> colUserID, colPermissions, colEmail, colPassword;

  @FXML
  private void initialize() {
    colUserID.setCellValueFactory(
        new PropertyValueFactory<UserAccountObservable, String>("userID"));
    colPermissions.setCellValueFactory(
        new PropertyValueFactory<UserAccountObservable, String>("permissions"));
    colEmail.setCellValueFactory(new PropertyValueFactory<UserAccountObservable, String>("email"));
    colPassword.setCellValueFactory(
        new PropertyValueFactory<UserAccountObservable, String>("passhash"));

    userAccountsTable.setItems(getUsrAcctRows());

    userAccountsTable.setEditable(true);

    colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
    colPermissions.setCellFactory(
        ChoiceBoxTableCell.forTableColumn("ADMIN", "STAFF", "GUEST", "ROOT"));
    colPassword.setCellFactory(TextFieldTableCell.forTableColumn());

    colPermissions.setOnEditCommit(this::onEditPermissions);
    colEmail.setOnEditCommit(this::onEditEmail);
    colPassword.setOnEditCommit(this::onEditPassword);
  }

  private ObservableList<UserAccountObservable> getUsrAcctRows() {
    List<UserAccount> accounts =
        (ArrayList<UserAccount>) dbConnection.getAllEntries(TableEntryType.USER_ACCOUNT);
    ObservableList<UserAccountObservable> returnable = FXCollections.observableArrayList();
    for (UserAccount account : accounts) {
      UserAccountObservable obs = new UserAccountObservable();
      obs.userID = account.getUsername();
      obs.email = account.getEmail();
      obs.permissions = account.getLevel().toString();
      obs.passhash = account.getPassword();
      returnable.add(obs);
    }
    return returnable;
  }

  private void onEditPermissions(TableColumn.CellEditEvent<UserAccountObservable, String> t) {
    UserAccountObservable row = t.getRowValue();
    UserAccount entry =
        (UserAccount) dbConnection.getEntry(row.getUserID(), TableEntryType.USER_ACCOUNT);
    String newPermissions = t.getNewValue();
    entry.setLevel(PermissionLevel.valueOf(newPermissions));
    dbConnection.updateEntry(entry);
  }

  private void onEditEmail(TableColumn.CellEditEvent<UserAccountObservable, String> t) {
    UserAccountObservable row = t.getRowValue();
    UserAccount entry =
        (UserAccount) dbConnection.getEntry(row.getUserID(), TableEntryType.USER_ACCOUNT);
    String newEmail = t.getNewValue();
    entry.setEmail(newEmail);
    dbConnection.updateEntry(entry);
  }

  private void onEditPassword(TableColumn.CellEditEvent<UserAccountObservable, String> t) {
    UserAccountObservable row = t.getRowValue();
    UserAccount entry =
        (UserAccount) dbConnection.getEntry(row.getUserID(), TableEntryType.USER_ACCOUNT);
    // set to the hashed version of the password
    String password = t.getNewValue();
    System.out.print("New password: ");
    System.out.println(password);
    entry.setPassword(String.valueOf(password.hashCode()));
    dbConnection.updateEntry(entry);
    // hide the password, show the hash
    row.setPasshash(String.valueOf(password.hashCode()));
    userAccountsTable.refresh();
  }
}
