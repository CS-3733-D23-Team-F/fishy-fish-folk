package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.SupplyRequest;
import java.time.LocalDateTime;
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
  @Getter @Setter public LocalDateTime id;

  public SupplyOrderObservable(SupplyRequest order) {
    this.id = order.getSupplyRequestID();
    this.supplyid = order.getSupplyRequestID().toString();
    this.supplyassignee = order.getAssignee();
    switch (order.getFormStatus()) {
      case submitted:
        this.supplystatus = "submitted";
        break;
      case notSubmitted:
        this.supplystatus = "Not Submitted";
        break;
      case cancelled:
        this.supplystatus = "Cancelled";
        break;
      case filled:
        this.supplystatus = "Filled";
        break;
    }
    this.supplydeliveryroom = order.getRoomNumber();
    this.supplylink = order.getLink();
    this.supplynotes = order.getNotes();
    String[] itemStrings = new String[order.getSupplies().size()];
    for (int i = 0; i < itemStrings.length; i++) {
      itemStrings[i] = order.getSupplies().get(i).supplyName;
    }
    String items = String.join(", ", itemStrings);
    this.supplysupplies = items;
  }
}
