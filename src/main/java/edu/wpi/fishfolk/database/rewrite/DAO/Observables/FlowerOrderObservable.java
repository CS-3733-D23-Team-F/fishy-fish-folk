package edu.wpi.fishfolk.database.rewrite.DAO.Observables;

import edu.wpi.fishfolk.database.rewrite.TableEntry.FlowerRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class FlowerOrderObservable {
  @Getter @Setter public String flowerid;
  @Getter @Setter public String flowerassignee;
  @Getter @Setter public String flowertotalprice;
  @Getter @Setter public String flowerstatus;
  @Getter @Setter public String flowerdeliveryroom;
  @Getter @Setter public String flowerdeliverytime;
  @Getter @Setter public String flowerrecipientname;
  @Getter @Setter public String floweritems;
  @Getter @Setter public LocalDateTime id;

  public FlowerOrderObservable(FlowerRequest order) {
    this.id = order.getFlowerRequestID();
    this.flowerid = order.getFlowerRequestID().toString();
    this.flowerassignee = order.getAssignee();
    this.flowertotalprice = String.format("%.2f", order.getTotalPrice());
    switch (order.getFormStatus()) {
      case submitted:
        this.flowerstatus = "submitted";
        break;
      case notSubmitted:
        this.flowerstatus = "Not Submitted";
        break;
      case cancelled:
        this.flowerstatus = "Cancelled";
        break;
      case filled:
        this.flowerstatus = "Filled";
        break;
    }
    this.flowerdeliveryroom = order.getDeliveryLocation();
    // this.flowerdeliverytime = order.get.toString(); can't find a time mentioned in FlowerRequest?
    this.flowerrecipientname = order.getPayer().nameOnCard;
    String[] itemStrings = new String[order.getItems().size()];
    for (int i = 0; i < itemStrings.length; i++) {
      itemStrings[i] = order.getItems().get(i).toString();
    }
    String items = String.join(", ", itemStrings);
    this.floweritems = items;
    // this.flowernotes = order.getNotes(); can't find notes in Max's original field list
  }
}
