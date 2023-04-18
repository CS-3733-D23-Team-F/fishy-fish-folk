package edu.wpi.fishfolk.database.rewrite.DAO.Observables;

import lombok.Getter;
import lombok.Setter;

public class SupplyOrderObservable {
  @Getter @Setter public String supplyid;
  @Getter @Setter public String supplyassignee;
  @Getter @Setter public String supplystatus;
  @Getter @Setter public String supplydeliveryroom;
  @Getter @Setter public String supplylink;
  @Getter @Setter public String supplynotes;
  @Getter @Setter public String supplysupplies;

  public SupplyOrderObservable() {
    this.supplyid = supplyid;
    this.supplyassignee = supplyassignee;
    this.supplystatus = supplystatus;
    this.supplydeliveryroom = supplydeliveryroom;
    this.supplylink = supplylink;
    this.supplynotes = supplynotes;
    this.supplysupplies = supplysupplies;
  }
}
