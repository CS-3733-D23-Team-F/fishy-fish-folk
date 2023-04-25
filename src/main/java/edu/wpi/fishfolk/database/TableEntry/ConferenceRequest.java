package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.Recurring;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ConferenceRequest {

  @Getter @Setter private LocalDateTime conferenceRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  @Getter @Setter public String name;
  @Getter @Setter public String startTime;
  @Getter @Setter public String endTime;
  @Getter @Setter public Recurring recurringOption;
  @Getter @Setter public int numAttendees;
  @Getter @Setter public String roomName;

  public ConferenceRequest() {
    // Formula for creating the ID.
    this.conferenceRequestID = LocalDateTime.now();
    // FormStatus is unnecessary so I set it to NULL.
    this.formStatus = null;
    this.assignee = null;
  }

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
