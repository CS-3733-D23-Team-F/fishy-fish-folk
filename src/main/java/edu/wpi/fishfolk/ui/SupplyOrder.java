package edu.wpi.fishfolk.ui;
import edu.wpi.fishfolk.database.TableEntry;
import java.util.ArrayList;
import java.util.LinkedList;

public class SupplyOrder {
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
    formStatus = FormStatus.notSubmitted;
    this.formID = formID;
  }

  // addSupply() adds a given SupplyItem to supplies
  public void addSupply(SupplyItem item) {
    supplies.add(item);
  }

  // setSubmitted() sets the formStatus to submitted
  public void setSubmitted() {
    FormStatus formStatus = FormStatus.submitted;
  }

  // setCancelled() sets the formStatus to cancelled
  public void setCancelled() {
    FormStatus formStatus = FormStatus.cancelled;
  }

  // setFilled() sets the formStatus to filled
  public void setFilled() {
    FormStatus formStatus = FormStatus.filled;
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

  // listItemsToString() takes supplies from a SupplyOrder (LinkedList<FoodItems>) and turns them
  // into a string
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

  // stringLoListItem() takes a string generated from listToString()
  // converts it back to LinkedList<SupplyItem> using the worst logic possible lol
  public LinkedList<SupplyItem> stringToListItem(String listToString) {
    LinkedList<SupplyItem> supplyList = new LinkedList<SupplyItem>();
    String[] StringToList = listToString.split("-_-");
    for (int i = 0; i < 7; i++) {
      if (StringToList[i] == null) break;
      else {
        if (StringToList[i] == "Pencil") supplyList.add(new SupplyItem("Pencil", 1.99F));
        if (StringToList[i] == "Pen") supplyList.add(new SupplyItem("Pen", 1.99F));
        if (StringToList[i] == "Eraser") supplyList.add(new SupplyItem("Eraser", 1.99F));
        if (StringToList[i] == "Marker") supplyList.add(new SupplyItem("Marker", 1.99F));
        if (StringToList[i] == "Notepad") supplyList.add(new SupplyItem("Notepad", 1.99F));
        if (StringToList[i] == "Clipboard") supplyList.add(new SupplyItem("Clipboard", 1.99F));
        if (StringToList[i] == "Apple") supplyList.add(new SupplyItem("Apple", 1.99F));
      }
    }
    return supplyList;
  }

  // construct() takes an ArrayList<String> from table and turns it into a SupplyOrder object
  //@Override
  public boolean construct(ArrayList<String> data) {
    SupplyOrder currentSupplyOrder = new SupplyOrder();
    currentSupplyOrder.formID = data.get(0);
    currentSupplyOrder.supplies = stringToListItem(data.get(1));
    currentSupplyOrder.link = data.get(2);
    currentSupplyOrder.roomNum = data.get(3);
    currentSupplyOrder.notes = data.get(4);
    return true;
  }

  // deconstruct() turns SupplyOrder object into array list to be put into table
  // ID, ListofItems, link, roomNum, notes
  //@Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> deconstruction = new ArrayList<String>();
    deconstruction.add(formID);
    deconstruction.add(listItemsToString());
    deconstruction.add(link);
    deconstruction.add(roomNum);
    deconstruction.add(notes);
    return deconstruction;
  }
}
