package edu.wpi.fishfolk.database.rewrite.TableEntry;

import edu.wpi.fishfolk.ui.CreditCardInfo;
import edu.wpi.fishfolk.ui.FoodItem;
import edu.wpi.fishfolk.ui.FormStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class FoodRequest {

  @Getter @Setter private String id;
  @Getter @Setter private String assignee;
  @Getter @Setter private FormStatus status;
  @Getter @Setter private ArrayList<FoodItem> items;
  @Getter @Setter private LocalDateTime time;
  @Getter @Setter private CreditCardInfo payer;
  @Getter @Setter private String location;
  @Getter @Setter private float totalPrice;

  /**
   * Table entry type: Food Request
   *
   * @param id Unique ID of request
   * @param assignee Assignee of request
   * @param status Status of request
   * @param items Food items of request
   * @param time Time of request
   * @param payer Payment information of request
   * @param location Location of request
   * @param totalPrice Price of request
   */
  public FoodRequest(
      String id,
      String assignee,
      FormStatus status,
      ArrayList<FoodItem> items,
      LocalDateTime time,
      CreditCardInfo payer,
      String location,
      float totalPrice) {
    this.id = id;
    this.assignee = assignee;
    this.status = status;
    this.items = items;
    this.time = time;
    this.payer = payer;
    this.location = location;
    this.totalPrice = totalPrice;
  }
}
