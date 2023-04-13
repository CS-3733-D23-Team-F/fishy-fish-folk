package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.ui.FormStatus;
import edu.wpi.fishfolk.ui.SupplyItem;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class SupplyRequest {

  @Getter @Setter private String id;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus status;
  @Getter @Setter private ArrayList<SupplyItem> supplies;
  @Getter @Setter private String link;
  @Getter @Setter private String roomNumber;
  @Getter @Setter private String notes;

  /**
   * Table entry type: Supply Request
   *
   * @param id Unique id of request
   * @param assignee Assignee of request
   * @param status Status of request
   * @param supplies Supply items of request
   * @param link Link to supply items if out of stock
   * @param roomNumber Room number of request
   * @param notes Additional notes of request
   */
  public SupplyRequest(
      String id,
      String assignee,
      FormStatus status,
      ArrayList<SupplyItem> supplies,
      String link,
      String roomNumber,
      String notes) {
    this.id = id;
    this.assignee = assignee;
    this.status = status;
    this.supplies = supplies;
    this.link = link;
    this.roomNumber = roomNumber;
    this.notes = notes;
  }
}
