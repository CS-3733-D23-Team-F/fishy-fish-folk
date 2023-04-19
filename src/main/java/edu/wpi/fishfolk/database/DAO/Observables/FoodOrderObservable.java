package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.FoodRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class FoodOrderObservable {
  @Getter @Setter public String foodid;
  @Getter @Setter public String foodassignee;
  @Getter @Setter public String foodtotalprice;
  @Getter @Setter public String foodstatus;
  @Getter @Setter public String fooddeliveryroom;
  @Getter @Setter public String fooddeliverytime;
  @Getter @Setter public String foodrecipientname;
  @Getter @Setter public String foodnotes;
  @Getter @Setter public String fooditems;
  @Getter @Setter public LocalDateTime id;

  public FoodOrderObservable(FoodRequest order) {
    this.id = order.getFoodRequestID();
    this.foodid = order.getFoodRequestID().toString();
    this.foodassignee = order.getAssignee();
    this.foodtotalprice = String.format("%.2f", order.getTotalPrice());
    switch (order.getFormStatus()) {
      case submitted:
        this.foodstatus = "submitted";
        break;
      case notSubmitted:
        this.foodstatus = "Not Submitted";
        break;
      case cancelled:
        this.foodstatus = "Cancelled";
        break;
      case filled:
        this.foodstatus = "Filled";
        break;
    }
    this.fooddeliveryroom = order.getDeliveryRoom();
    this.fooddeliverytime = order.getDeliveryTime().toString();
    this.foodrecipientname = order.getRecipientName();
    String[] itemStrings = new String[order.getFoodItems().size()];
    for (int i = 0; i < itemStrings.length; i++) {
      itemStrings[i] = order.getFoodItems().get(i).toString();
    }
    String items = String.join(", ", itemStrings);
    this.fooditems = items;
    this.foodnotes = order.getNotes();
  }
}
