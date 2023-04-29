package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ITRequest {

  // Common
  @Getter @Setter private LocalDateTime itRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: IT Request
   *
   * @param itRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   */
  public ITRequest(
      LocalDateTime itRequestID, String assignee, FormStatus formStatus, String notes) {
    this.itRequestID = itRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.status = EntryStatus.OLD;
  }

  /**
   * Table entry type: IT Request (automatic ID creation).
   *
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   */
  public ITRequest(String assignee, FormStatus formStatus, String notes) {
    this.itRequestID = LocalDateTime.now();
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.status = EntryStatus.OLD;
  }
}
