package edu.wpi.fishfolk.ui;

import java.util.ArrayList;
import java.util.LinkedList;

public class SupplyOrder {
  public LinkedList<SupplyItem> supplies;
  public String link;
  public String roomNum;
  public String notes;

  public FormStatus formStatus;
  public String formID;

  public String assignee;

  public SupplyOrder() {

    supplies = new LinkedList<SupplyItem>();
    this.link = link;
    this.roomNum = roomNum;
    this.notes = notes;
    formStatus = FormStatus.notSubmitted;
    formID = "" + System.currentTimeMillis();
    formID = formID.substring(formID.length() - 10);
  }

  // addSupply() adds a given SupplyItem to supplies
  public void addSupply(SupplyItem item) {
    supplies.add(item);
  }

  // setSubmitted() sets the formStatus to submitted
  public void setSubmitted() {
    formStatus = FormStatus.submitted;
  }

  // setCancelled() sets the formStatus to cancelled
  public void setCancelled() {
    formStatus = FormStatus.cancelled;
  }

  // setFilled() sets the formStatus to filled
  public void setFilled() {
    formStatus = FormStatus.filled;
  }

  // toString() takes the attributes of a SupplyOrder (supplies, link, roomNum, notes)
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
            + notes
            + "\n"
            + "ID Number: "
            + formID;
    return string;
  }

  public String quickToString() {
    String string =
        supplies.get(0)
            + ", "
            + supplies.get(1)
            + ", "
            + supplies.get(2)
            + ", "
            + supplies.get(3)
            + ", "
            + supplies.get(4)
            + ", "
            + supplies.get(5)
            + ", "
            + supplies.get(6)
            + ", "
            + supplies.get(7);
    return string;
  }

  // listItemsToString() takes supplies from a SupplyOrder (LinkedList<FoodItems>) and turns them
  // into a string
  public String listItemsToString() {
    String string = "";
    if (supplies.isEmpty()) return "";
    else {
      for (int i = 0; i < supplies.size(); i++) {
        if (!(supplies.get(i).supplyName == "")) {
          if (string.equals("")) string += supplies.get(i).supplyName;
          else string += "-_-" + supplies.get(i).supplyName;
        }
      }
      // string = string.substring(0, (string.length() - 3));
      return string;
    }
  }

  // stringLoListItem() takes a string generated from listToString()
  // converts it back to LinkedList<SupplyItem> using the worst logic possible lol
  public LinkedList<SupplyItem> stringToListItem(String listToString) {
    LinkedList<SupplyItem> supplyList = new LinkedList<SupplyItem>();
    String[] StringToList = listToString.split("-_-");
    for (int i = 0; i < StringToList.length; i++) {
      if (StringToList[i].equals("Pencil")) supplyList.add(new SupplyItem("Pencil", 1.99F));
      else if (StringToList[i].equals("Pen")) supplyList.add(new SupplyItem("Pen", 1.99F));
      else if (StringToList[i].equals("Eraser")) supplyList.add(new SupplyItem("Eraser", 1.99F));
      else if (StringToList[i].equals("Marker")) supplyList.add(new SupplyItem("Marker", 1.99F));
      else if (StringToList[i].equals("Notepad")) supplyList.add(new SupplyItem("Notepad", 1.99F));
      else if (StringToList[i].equals("Clipboard"))
        supplyList.add(new SupplyItem("Clipboard", 1.99F));
      else if (StringToList[i].equals("Apple")) supplyList.add(new SupplyItem("Apple", 1.99F));
    }
    return supplyList;
  }

  // construct() takes an ArrayList<String> from table and turns it into a SupplyOrder object
  // @Override
  public boolean construct(ArrayList<String> data) {
    SupplyOrder currentSupplyOrder = new SupplyOrder();
    formID = data.get(0);
    supplies = stringToListItem(data.get(1));
    link = data.get(2);
    roomNum = data.get(3);
    notes = data.get(4);
    String status = data.get(5);
    if (status.equals("Filled")) {
      formStatus = FormStatus.filled;
    } else if (status.equals("Cancelled")) {
      formStatus = FormStatus.cancelled;
    } else if (status.equals("Submitted")) {
      formStatus = FormStatus.submitted;
    } else if (status.equals("NotSubmitted")) {
      formStatus = FormStatus.notSubmitted;
    } else return false;
    assignee = data.get(6);
    return true;
  }

  // deconstruct() turns SupplyOrder object into array list to be put into table
  // ID, ListofItems, link, roomNum, notes, assignee
  // @Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> deconstruction = new ArrayList<String>();
    deconstruction.add(formID);
    deconstruction.add(listItemsToString());
    deconstruction.add(link);
    deconstruction.add(roomNum);
    deconstruction.add(notes);
    switch (formStatus) {
      case filled:
        {
          deconstruction.add("Filled");
          break;
        }
      case notSubmitted:
        {
          deconstruction.add("NotSubmitted");
          break;
        }
      case submitted:
        {
          deconstruction.add("Submitted");
          break;
        }
      case cancelled:
        {
          deconstruction.add("Cancelled");
          break;
        }
    }
    deconstruction.add(assignee);
    return deconstruction;
  }
}
