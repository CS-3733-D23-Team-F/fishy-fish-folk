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
}
