package edu.wpi.fishfolk.database.TableEntry;

import edu.wpi.fishfolk.ui.FormStatus;
import lombok.Getter;
import lombok.Setter;

public class ConferenceRequest {

  @Getter @Setter private String id;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus status;

  // TODO: Additional fields

  /**
   * Table entry type: Conference Request
   *
   * @param id Unique ID of request
   * @param assignee Assignee of request
   * @param status Status of request
   */
  public ConferenceRequest(String id, String assignee, FormStatus status) {
    this.id = id;
    this.assignee = assignee;
    this.status = status;
  }
}
