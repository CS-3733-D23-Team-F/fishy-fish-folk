package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.database.rewrite.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ConferenceRequest {

  @Getter @Setter private LocalDateTime conferenceRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  // TODO: Additional fields

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: Conference Request
   *
   * @param conferenceRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   */
  public ConferenceRequest(
      LocalDateTime conferenceRequestID, String assignee, FormStatus formStatus, String notes) {
    this.conferenceRequestID = conferenceRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.status = EntryStatus.OLD;
  }
}
