package edu.wpi.fishfolk.database.rewrite.DAO.Observables;

import lombok.Getter;
import lombok.Setter;

public class FlowerOrderObservable {
  @Getter @Setter public String flowerid;
  @Getter @Setter public String flowerassignee;
  @Getter @Setter public String flowertotalprice;
  @Getter @Setter public String flowerstatus;
  @Getter @Setter public String flowerdeliveryroom;
  @Getter @Setter public String flowerdeliverytime;
  @Getter @Setter public String flowerrecipientname;
  @Getter @Setter public String floweritems;
}
