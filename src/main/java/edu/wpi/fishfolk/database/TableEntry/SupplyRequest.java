package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class SupplyRequest {

  // Common
  @Getter @Setter private LocalDateTime supplyRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private String link;
  @Getter @Setter private String roomNumber;
  @Getter @Setter private List<SupplyItem> supplies;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Supply Request
   *
   * @param supplyRequestID Unique id of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param link Link to supply items if out of stock
   * @param roomNumber Room number of request
   * @param supplies Supply items of request
   */
  public SupplyRequest(
      LocalDateTime supplyRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      String link,
      String roomNumber,
      List<SupplyItem> supplies) {
    this.supplyRequestID = supplyRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.link = link;
    this.roomNumber = roomNumber;
    this.supplies = supplies;
    this.status = EntryStatus.OLD;
  }

  /**
   * Table entry type: Supply Request; This one sets the LocalDateTime to now.
   *
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param link Link to supply items if out of stock
   * @param roomNumber Room number of request
   * @param supplies Supply items of request
   */
  public SupplyRequest(
      String assignee,
      FormStatus formStatus,
      String notes,
      String link,
      String roomNumber,
      List<SupplyItem> supplies) {
    this.supplyRequestID = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.link = link;
    this.roomNumber = roomNumber;
    this.supplies = supplies;
    this.status = EntryStatus.OLD;
  }
}
