package edu.wpi.fishfolk.ui;

import lombok.Getter;
import lombok.Setter;

public class ITRequest {
  @Getter @Setter ITComponent component;
  @Getter @Setter String issueNotes;
  @Getter @Setter ITPriority priority;
  @Getter @Setter String roomNum;
  @Getter @Setter String contactInfo;
  @Getter @Setter FormStatus formStatus;
  @Getter @Setter String assignee;
}
