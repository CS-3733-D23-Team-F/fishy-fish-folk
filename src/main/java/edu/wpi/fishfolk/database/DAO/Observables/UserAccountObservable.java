package edu.wpi.fishfolk.database.DAO.Observables;

import lombok.Getter;
import lombok.Setter;

public class UserAccountObservable {
  @Getter @Setter public String userID;
  @Getter @Setter public String email;
  @Getter @Setter public String permissions;
  @Getter @Setter public String passhash;
}
