package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class FurnitureRequest {

  // Common
  @Getter @Setter private LocalDateTime furnitureRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private String item; // TODO: Change to more specialized type
  @Getter @Setter private String serviceType; // TODO: Change to more specialized enum
  @Getter @Setter private String roomNumber;
  @Getter @Setter private LocalDateTime deliveryDate;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Furniture Request
   *
   * @param furnitureRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param item Furniture item of request
   * @param serviceType Type of service to perform on/with furniture item
   * @param roomNumber Room number of request
   * @param deliveryDate Date of request
   */
  public FurnitureRequest(
      LocalDateTime furnitureRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      String item,
      String serviceType,
      String roomNumber,
      LocalDateTime deliveryDate) {
    this.furnitureRequestID = furnitureRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.item = item;
    this.serviceType = serviceType;
    this.roomNumber = roomNumber;
    this.deliveryDate = deliveryDate;
    this.status = EntryStatus.OLD;
  }
}
