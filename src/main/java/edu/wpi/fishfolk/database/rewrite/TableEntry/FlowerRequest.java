package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.ui.FlowerItem;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class FlowerRequest {

  // Common
  @Getter @Setter private LocalDateTime flowerRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private String recipientName;
  @Getter @Setter private String deliveryLocation;
  @Getter @Setter private double totalPrice;
  @Getter @Setter private List<FlowerItem> items;
  @Getter @Setter private LocalDateTime deliveryTime;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Flower Request
   *
   * @param flowerRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param recipientName Payment information of request
   * @param deliveryLocation Delivery location of request
   * @param deliveryTime Delivery time of request
   * @param totalPrice Price of request
   * @param items Flower items of request
   */
  public FlowerRequest(
      LocalDateTime flowerRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      String recipientName,
      String deliveryLocation,
      LocalDateTime deliveryTime,
      double totalPrice,
      List<FlowerItem> items) {
    this.flowerRequestID = flowerRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.recipientName = recipientName;
    this.deliveryLocation = deliveryLocation;
    this.deliveryTime = deliveryTime;
    this.totalPrice = totalPrice;
    this.items = items;
    this.status = EntryStatus.OLD;
  }

  /**
   * Table entry type: Flower Request; This one sets the LocalDateTime to now.
   *
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param recipientName Payment information of request
   * @param deliveryLocation Delivery location of request
   * @param deliveryTime Delivery time of request
   * @param totalPrice Price of request
   * @param items Flower items of request
   */
  public FlowerRequest(
      String assignee,
      FormStatus formStatus,
      String notes,
      String recipientName,
      String deliveryLocation,
      LocalDateTime deliveryTime,
      double totalPrice,
      List<FlowerItem> items) {
    this.flowerRequestID = LocalDateTime.now();
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.recipientName = recipientName;
    this.deliveryLocation = deliveryLocation;
    this.deliveryTime = deliveryTime;
    this.totalPrice = totalPrice;
    this.items = items;
    this.status = EntryStatus.OLD;
  }
}
