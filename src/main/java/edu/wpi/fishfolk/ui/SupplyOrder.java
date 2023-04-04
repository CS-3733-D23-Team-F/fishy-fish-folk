package edu.wpi.fishfolk.ui;

import edu.wpi.fishfolk.database.TableEntry;

import java.util.ArrayList;
import java.util.LinkedList;

public class SupplyOrder extends TableEntry {
  public LinkedList<SupplyItem> supplies;
  public String link;
  public String roomNum;
  public String notes;

  FormStatus formStatus;
  public String formID;

  public SupplyOrder() {

    supplies = new LinkedList<SupplyItem>();
    this.link = link;
    this.roomNum = roomNum;
    this.notes = notes;
    formStatus = edu.wpi.fishfolk.ui.FormStatus.notSubmitted;
    this.formID = formID;
  }

  public void addSupply(SupplyItem item) {
    supplies.add(item);
  }

  public void setSubmitted() {
    FormStatus formStatus = edu.wpi.fishfolk.ui.FormStatus.submitted;
  }

  public void setCancelled() {
    FormStatus formStatus = edu.wpi.fishfolk.ui.FormStatus.cancelled;
  }

  public void setFilled() {
    FormStatus formStatus = edu.wpi.fishfolk.ui.FormStatus.filled;
  }

  public String toString() {
    String string =
        "Supply List:"
            + "\n"
            + supplies.get(0).supplyName
            + "\n"
            + supplies.get(1).supplyName
            + "\n"
            + supplies.get(2).supplyName
            + "\n"
            + supplies.get(3).supplyName
            + "\n"
            + supplies.get(4).supplyName
            + "\n"
            + supplies.get(5).supplyName
            + "\n"
            + supplies.get(6).supplyName
            + "\n"
            + "Link if not in stock: "
            + link
            + "\n"
            + "Room Number: "
            + roomNum
            + "\n"
            + "Additional Notes: "
            + notes;
    return string;
  }

  public String listItemsToString() {
    String string = "";
    if (supplies.isEmpty()) return "";
    else {
      for (int i = 0; i < 6; i++) {
        if (supplies.get(i).supplyName == "") string = string + "";
        else string = string + supplies.get(i).supplyName + "-_-";
      }
      // string = string.substring(0, (string.length() - 3));
      return string;
    }
  }

  @Override
  public boolean construct(ArrayList<String> data) {
    return false;
  }

  @Override
  public ArrayList<String> deconstruct() {
    return null;
  }
}
