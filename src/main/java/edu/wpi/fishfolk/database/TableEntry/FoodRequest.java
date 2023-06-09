package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.NewFoodItem;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class FoodRequest {

  // Common
  @Getter @Setter private LocalDateTime foodRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private double totalPrice;
  @Getter @Setter private String deliveryRoom;
  @Getter @Setter private LocalDateTime deliveryTime;
  @Getter @Setter private String recipientName;
  @Getter @Setter private List<NewFoodItem> foodItems;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Food Request
   *
   * @param foodRequestID Unique ID of food request
   * @param assignee Assignee of request
   * @param formStatus Form status of request
   * @param notes Additional notes of request
   * @param totalPrice Total price of request
   * @param deliveryRoom Delivery room of request
   * @param deliveryTime Delivery time of request
   * @param recipientName Request recipient's name
   * @param foodItems Food items requested
   */
  public FoodRequest(
      LocalDateTime foodRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      double totalPrice,
      String deliveryRoom,
      LocalDateTime deliveryTime,
      String recipientName,
      List<NewFoodItem> foodItems) {
    this.foodRequestID = foodRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;

    this.totalPrice = totalPrice;
    this.deliveryRoom = deliveryRoom;
    this.deliveryTime = deliveryTime;
    this.recipientName = recipientName;
    this.foodItems = foodItems;

    this.status = EntryStatus.OLD;
  }

  /**
   * Table entry type: Food Request; This one sets the LocalDateTime to now.
   *
   * @param assignee Assignee of request
   * @param formStatus Form status of request
   * @param notes Additional notes of request
   * @param totalPrice Total price of request
   * @param deliveryRoom Delivery room of request
   * @param deliveryTime Delivery time of request
   * @param recipientName Request recipient's name
   * @param foodItems Food items requested
   */
  public FoodRequest(
      String assignee,
      FormStatus formStatus,
      String notes,
      double totalPrice,
      String deliveryRoom,
      LocalDateTime deliveryTime,
      String recipientName,
      List<NewFoodItem> foodItems) {
    this.foodRequestID = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;

    this.totalPrice = totalPrice;
    this.deliveryRoom = deliveryRoom;
    this.deliveryTime = deliveryTime;
    this.recipientName = recipientName;
    this.foodItems = foodItems;

    this.status = EntryStatus.OLD;
  }
}
