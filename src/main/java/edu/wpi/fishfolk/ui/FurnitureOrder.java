package edu.wpi.fishfolk.ui;

import java.time.LocalDateTime;

public class FurnitureOrder {
  public FurnitureItem furnitureItem;
  public ServiceType serviceType;
  public String roomNum;
  public LocalDateTime deliveryDate;
  public String notes;
  public String formID;
  public FormStatus formStatus;
  public String assignee;

  // sets some of the default parameters for a standard FurnitureOrder
  // including the notSubmitted formStatus and ID based on the system clock
  public FurnitureOrder() {
    formID = "" + System.currentTimeMillis();
    formID = formID.substring(formID.length() - 10);
    formStatus = FormStatus.notSubmitted;
  }

  // addFurniture() adds the furniture item to the given Furniture Order
  public void addFurniture(FurnitureItem item) {
    this.furnitureItem = item;
  }

  // setServiceType sets the service type for the given FurnitureOrder
  // the generic service types are defined in loadServiceTypeChoice() in FurnitureOrderController
  public void setServiceType(ServiceType type) {
    this.serviceType = type;
  }

  // setRoomNum() sets the room number for the given FurnitureOrder
  // the rooms are based on the nodes in NodeTable as defined in loadRoomChoice() in
  // FurnitureOrderController
  public void setRoomNum(String room) {
    this.roomNum = room;
  }

  // addDate() sets the date for the given FurnitureOrder
  // note: takes a string because idk what MFXDatePicker returns
  public void addDate(LocalDateTime date) {
    this.deliveryDate = date;
  }

  // addNotes() adds the additional notes portion of the given FurnitureOrder
  public void addNotes(String note) {
    this.notes = note;
  }

  // setStatus() sets the FormStatus enum for the given FurnitureOrder
  // enum -> notSubmitted, submitted, cancelled, filled
  public void setStatus(FormStatus status) {
    this.formStatus = status;
  }

  // construct() converts the values from a given furnitureorder table row into a FurnitureOrder
  public boolean construct(ArrayList<String> data) {
    formID = data.get(0);
    if (data.get(1).equals("replacement")) serviceType = ServiceType.replacement;
    if (data.get(1).equals("cleaning")) serviceType = ServiceType.cleaning;
    if (data.get(1).equals("delivery")) serviceType = ServiceType.delivery;
    if (data.get(1).equals("maintenance")) serviceType = ServiceType.maintenance;
    if (data.get(1).equals("removal")) serviceType = ServiceType.removal;

    if (data.get(2).equals(FurnitureItem.bed.furnitureName)) furnitureItem = FurnitureItem.bed;
    if (data.get(2).equals(FurnitureItem.chair.furnitureName)) furnitureItem = FurnitureItem.chair;
    if (data.get(2).equals(FurnitureItem.desk.furnitureName)) furnitureItem = FurnitureItem.desk;
    if (data.get(2).equals(FurnitureItem.fileCabinet.furnitureName))
      furnitureItem = FurnitureItem.fileCabinet;
    if (data.get(2).equals(FurnitureItem.clock.furnitureName)) furnitureItem = FurnitureItem.clock;
    if (data.get(2).equals(FurnitureItem.xRay.furnitureName)) furnitureItem = FurnitureItem.xRay;
    if (data.get(2).equals(FurnitureItem.trashCan.furnitureName))
      furnitureItem = FurnitureItem.trashCan;
    roomNum = data.get(3);
    deliveryDate = data.get(4);
    notes = data.get(5);
    if (data.get(6).equals("notSubmitted")) setStatus(FormStatus.notSubmitted);
    if (data.get(6).equals("submitted")) setStatus(FormStatus.submitted);
    if (data.get(6).equals("cancelled")) setStatus(FormStatus.cancelled);
    if (data.get(6).equals("filled")) setStatus(FormStatus.filled);
    assignee = data.get(7);
    return true;
  }

  // deconstruct() converts the currentFurnitureOrder into an ArrayList<String> for the
  // furnitureorder table format
  public ArrayList<String> deconstruct() {
    ArrayList<String> deconstruction = new ArrayList<String>();
    deconstruction.add(formID);
    deconstruction.add("" + serviceType);
    deconstruction.add(furnitureItem.furnitureName);
    deconstruction.add(roomNum);
    deconstruction.add(deliveryDate);
    deconstruction.add(notes);
    deconstruction.add("" + formStatus);
    deconstruction.add(assignee);
    return deconstruction;
  }
}
