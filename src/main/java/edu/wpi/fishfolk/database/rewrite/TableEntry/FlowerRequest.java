package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.ui.CreditCardInfo;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class FlowerRequest {

  // Common
  @Getter @Setter private LocalDateTime flowerRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private ArrayList<String> items; // TODO: Change to more specialized type
  @Getter @Setter private CreditCardInfo payer;
  @Getter @Setter private String deliveryLocation;
  @Getter @Setter private double totalPrice;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Flower Request
   *
   * @param flowerRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param items Flower items of request
   * @param payer Payment information of request
   * @param deliveryLocation Delivery location of request
   * @param totalPrice Price of request
   */
  public FlowerRequest(
      LocalDateTime flowerRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      ArrayList<String> items,
      CreditCardInfo payer,
      String deliveryLocation,
      double totalPrice) {
    this.flowerRequestID = flowerRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.items = items;
    this.payer = payer;
    this.deliveryLocation = deliveryLocation;
    this.totalPrice = totalPrice;
    this.status = EntryStatus.OLD;
  }
}
