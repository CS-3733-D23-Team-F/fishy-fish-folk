package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class SupplyRequest {

  // Common
  @Getter @Setter private LocalDateTime supplyRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private ArrayList<SupplyItem> supplies;
  @Getter @Setter private String link;
  @Getter @Setter private String roomNumber;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Supply Request
   *
   * @param supplyRequestID Unique id of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param supplies Supply items of request
   * @param link Link to supply items if out of stock
   * @param roomNumber Room number of request
   */
  public SupplyRequest(
      LocalDateTime supplyRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      ArrayList<SupplyItem> supplies,
      String link,
      String roomNumber) {
    this.supplyRequestID = supplyRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.supplies = supplies;
    this.link = link;
    this.roomNumber = roomNumber;
    this.status = EntryStatus.OLD;
  }
}
