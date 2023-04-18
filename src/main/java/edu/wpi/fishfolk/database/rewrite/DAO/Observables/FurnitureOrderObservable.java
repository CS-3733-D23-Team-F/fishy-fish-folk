package edu.wpi.fishfolk.database.rewrite.DAO.Observables;

import edu.wpi.fishfolk.database.rewrite.TableEntry.FurnitureRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class FurnitureOrderObservable {
  @Getter @Setter public String furnitureid;
  @Getter @Setter public String furnitureassignee;
  @Getter @Setter public String furniturestatus;
  @Getter @Setter public String furnituredeliveryroom;
  @Getter @Setter public String furnituredeliverydate;
  @Getter @Setter public String furniturenotes;
  @Getter @Setter public String furnitureservicetype;
  @Getter @Setter public String furniturefurniture;
  @Getter @Setter public LocalDateTime id;

  public FurnitureOrderObservable(FurnitureRequest order) {
    this.id = order.getFurnitureRequestID();
    this.furnitureid = order.getFurnitureRequestID().toString();
    this.furnitureassignee = order.getAssignee();
    switch (order.getFormStatus()) {
      case submitted:
        this.furniturestatus = "submitted";
        break;
      case notSubmitted:
        this.furniturestatus = "Not Submitted";
        break;
      case cancelled:
        this.furniturestatus = "Cancelled";
        break;
      case filled:
        this.furniturestatus = "Filled";
        break;
    }
    this.furnituredeliveryroom = order.getRoomNumber();
    this.furnituredeliverydate = "" + order.getDeliveryDate();
    this.furnitureservicetype = order.getServiceType();
    this.furniturenotes = order.getNotes();
    this.furniturefurniture = order.getItem();
  }
}
