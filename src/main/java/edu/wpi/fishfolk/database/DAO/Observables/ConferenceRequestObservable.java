package edu.wpi.fishfolk.database.DAO.Observables;

import edu.wpi.fishfolk.database.TableEntry.ConferenceRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class ConferenceRequestObservable {
  @Getter @Setter public LocalDateTime id;

  @Getter @Setter
  public String conferenceid,
      conferenceroom,
      conferencestart,
      conferenceend,
      conferencebooker,
      conferenceattendees,
      conferencerecurring,
      conferencenotes;

  public ConferenceRequestObservable(ConferenceRequest request) {
    conferenceid = request.getConferenceRequestID().toString();
    conferenceroom = request.getRoomName();
    conferencestart = request.getStartTime();
    conferenceend = request.getEndTime();
    conferencebooker = request.getUsername();
    conferenceattendees = "" + request.getNumAttendees();
    switch (request.getRecurringOption()) {
      case DAILY:
        {
          conferencerecurring = "Daily";
          break;
        }
      case MONTHLY:
        {
          conferencerecurring = "Monthly";
          break;
        }
      case WEEKLY:
        {
          conferencerecurring = "Weekly";
          break;
        }
      case YEARLY:
        {
          conferencerecurring = "Yearly";
          break;
        }
      case NEVER:
        {
          conferencerecurring = "Never";
          break;
        }
    }
    conferencenotes = request.getNotes();
  }
}