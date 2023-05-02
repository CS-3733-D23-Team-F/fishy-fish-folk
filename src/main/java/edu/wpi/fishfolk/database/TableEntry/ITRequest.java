package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.database.EntryStatus;
import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.ITComponent;
import edu.wpi.fishfolk.ui.ITPriority;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;

public class ITRequest {

  // Common
  @Getter @Setter private LocalDateTime itRequestID;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus formStatus;
  @Getter @Setter private String notes;

  // Specific
  @Getter @Setter private ITComponent component;
  @Getter @Setter private ITPriority priority;
  @Getter @Setter private String roomNumber;
  @Getter @Setter private String contactInfo;

  // For DAO
  @Getter @Setter private EntryStatus status;

  /**
   * Table entry type: IT Request
   *
   * @param itRequestID Unique ID of request
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param component Component needing attention
   * @param priority Priority of issue
   * @param roomNumber Room number of request
   * @param contactInfo Contact info of request submitter
   */
  public ITRequest(
      LocalDateTime itRequestID,
      String assignee,
      FormStatus formStatus,
      String notes,
      ITComponent component,
      ITPriority priority,
      String roomNumber,
      String contactInfo) {
    this.itRequestID = itRequestID;
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.component = component;
    this.priority = priority;
    this.roomNumber = roomNumber;
    this.contactInfo = contactInfo;
    this.status = EntryStatus.OLD;
  }

  /**
   * Table entry type: IT Request (automatic ID creation).
   *
   * @param assignee Assignee of request
   * @param formStatus Status of request
   * @param notes Additional notes of request
   * @param component Component needing attention
   * @param priority Priority of issue
   * @param roomNumber Room number of request
   * @param contactInfo Contact info of request submitter
   */
  public ITRequest(
      String assignee,
      FormStatus formStatus,
      String notes,
      ITComponent component,
      ITPriority priority,
      String roomNumber,
      String contactInfo) {
    this.itRequestID = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    this.assignee = assignee;
    this.formStatus = formStatus;
    this.notes = notes;
    this.component = component;
    this.priority = priority;
    this.roomNumber = roomNumber;
    this.contactInfo = contactInfo;
    this.status = EntryStatus.OLD;
  }
}
