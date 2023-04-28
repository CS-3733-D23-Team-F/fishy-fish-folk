package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.Recurring;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;

public class ConferenceRequest {

  // Common
  @Getter @Setter private LocalDateTime conferenceRequestID;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private String username;
  @Getter @Setter private String startTime;
  @Getter @Setter private String endTime;
  @Getter @Setter private Recurring recurringOption;
  @Getter @Setter private int numAttendees;
  @Getter @Setter private String roomName;

  // For DAO
  @Getter @Setter private EntryStatus status;

  public ConferenceRequest(
      String notes,
      String username,
      String startTime,
      String endTime,
      Recurring recurringOption,
      int numAttendees,
      String roomName) {
    this.conferenceRequestID = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    this.notes = notes;
    this.username = username;
    this.startTime = startTime;
    this.endTime = endTime;
    this.recurringOption = recurringOption;
    this.numAttendees = numAttendees;
    this.roomName = roomName;
    this.status = EntryStatus.OLD;
  }

  public ConferenceRequest(
      LocalDateTime conferenceRequestID,
      String notes,
      String username,
      String startTime,
      String endTime,
      Recurring recurringOption,
      int numAttendees,
      String roomName) {
    this.conferenceRequestID = conferenceRequestID;
    this.notes = notes;
    this.username = username;
    this.startTime = startTime;
    this.endTime = endTime;
    this.recurringOption = recurringOption;
    this.numAttendees = numAttendees;
    this.roomName = roomName;
    this.status = EntryStatus.OLD;
  }
}
