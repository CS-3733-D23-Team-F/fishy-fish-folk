package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.FurnitureItem;
import edu.wpi.fishfolk.ui.ServiceType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;

public class FurnitureRequest {

  /*
    Things a Furniture Order has:
  ID (long i'm assuming)
  Assignee (String)
  Status (enum)
  Delivery room (currently String, can change)
  Delivery Date (LocalDate -> String, can change)
  Notes (String)
  ServiceType (enum)
  Furniture (FurnitureItem)

  SupplyItem has:
  Name

     */

  // Common
  @Getter @Setter private LocalDateTime furnitureRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private FurnitureItem item;
  @Getter @Setter private ServiceType serviceType;
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
      FurnitureItem item,
      ServiceType serviceType,
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

  public FurnitureRequest(
      String assignee,
      FormStatus formStatus,
      String notes,
      FurnitureItem item,
      ServiceType serviceType,
      String roomNumber,
      LocalDateTime deliveryDate) {
    this.furnitureRequestID = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
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
