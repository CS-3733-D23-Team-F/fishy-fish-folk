package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class FurnitureRequest {

  @Getter @Setter private String id;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus status;
  @Getter @Setter private String item; // TODO: Change to more specialized type
  @Getter @Setter private String serviceType; // TODO: Change to more specialized enum
  @Getter @Setter private String roomNumber;
  @Getter @Setter private LocalDateTime deliveryDate;

  /**
   * Table entry type: Furniture Request
   *
   * @param id Unique ID of request
   * @param assignee Assignee of request
   * @param status Status of request
   * @param item Furniture item of request
   * @param serviceType Type of service to perform on/with furniture item
   * @param roomNumber Room number of request
   * @param deliveryDate Date of request
   */
  public FurnitureRequest(
      String id,
      String assignee,
      FormStatus status,
      String item,
      String serviceType,
      String roomNumber,
      LocalDateTime deliveryDate) {
    this.id = id;
    this.assignee = assignee;
    this.status = status;
    this.item = item;
    this.serviceType = serviceType;
    this.roomNumber = roomNumber;
    this.deliveryDate = deliveryDate;
  }
}
