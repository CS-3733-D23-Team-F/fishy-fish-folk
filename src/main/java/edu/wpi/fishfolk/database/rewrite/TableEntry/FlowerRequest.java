package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.ui.CreditCardInfo;
import edu.wpi.fishfolk.ui.FormStatus;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class FlowerRequest {

  @Getter @Setter private String id;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus status;
  @Getter @Setter private ArrayList<String> items; // TODO: Change to more specialized type
  @Getter @Setter private CreditCardInfo payer;
  @Getter @Setter private String deliveryLocation;
  @Getter @Setter private float totalPrice;

  /**
   * Table entry type: Flower Request
   *
   * @param id Unique ID of request
   * @param assignee Assignee of request
   * @param status Status of request
   * @param items Flower items of request
   * @param payer Payment information of request
   * @param deliveryLocation Delivery location of request
   * @param totalPrice Price of request
   */
  public FlowerRequest(
      String id,
      String assignee,
      FormStatus status,
      ArrayList<String> items,
      CreditCardInfo payer,
      String deliveryLocation,
      float totalPrice) {
    this.id = id;
    this.assignee = assignee;
    this.status = status;
    this.items = items;
    this.payer = payer;
    this.deliveryLocation = deliveryLocation;
    this.totalPrice = totalPrice;
  }
}
