package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.ITRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ITRequestObservable {
  @Getter @Setter public String itid;
  @Getter @Setter public String itassignee;
  @Getter @Setter public String itstatus;
  @Getter @Setter public String itissue;
  @Getter @Setter public String itcomponent;
  @Getter @Setter public String itpriority;
  @Getter @Setter public String itroom;
  @Getter @Setter public String itcontactinfo;
  @Getter @Setter public LocalDateTime id;

  public ITRequestObservable(ITRequest request) {
    this.id = request.getItRequestID();
    this.itid = request.getItRequestID().toString();
    this.itassignee = request.getAssignee();
    switch (request.getFormStatus()) {
      case submitted:
        this.itstatus = "Submitted";
        break;
      case notSubmitted:
        this.itstatus = "Not Submitted";
        break;
      case cancelled:
        this.itstatus = "Cancelled";
        break;
      case filled:
        this.itstatus = "Filled";
        break;
    }
    this.itissue = request.getNotes();
    switch (request.getComponent()) {
      case COMPUTER:
        {
          this.itcomponent = "Computer";
          break;
        }
      case KEYBOARD:
        {
          this.itcomponent = "Keyboard";
          break;
        }
      case MOUSE:
        {
          this.itcomponent = "Mouse";
          break;
        }
      case XRAY:
        {
          this.itcomponent = "X-Ray Machine";
          break;
        }
      case PRINTER:
        {
          this.itcomponent = "Printer";
          break;
        }
      case PHONE:
        {
          this.itcomponent = "Phone";
          break;
        }
      case HEADSET:
        {
          this.itcomponent = "Gamer Headset";
          break;
        }
      case APPLE:
        {
          this.itcomponent = "Apple";
          break;
        }
      case OUTOFIDEAS:
        {
          this.itcomponent = "Im out of ideas";
          break;
        }
    }
    switch (request.getPriority()) {
      case LOW:
        {
          this.itpriority = "Low";
          break;
        }
      case MEDIUM:
        {
          this.itpriority = "Medium";
          break;
        }
      case HIGH:
        {
          this.itpriority = "High";
          break;
        }
    }
    this.itroom = request.getRoomNumber();
    this.itcontactinfo = request.getContactInfo();
  }
}
