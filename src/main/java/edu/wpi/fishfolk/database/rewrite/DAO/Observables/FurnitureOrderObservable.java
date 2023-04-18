package edu.wpi.fishfolk.database.rewrite.DAO.Observables;

import lombok.Getter;
import lombok.Setter;

public class FurnitureOrderObservable {
  @Getter @Setter public String furnitureid;
  @Getter @Setter public String furnitureassignee;
  @Getter @Setter public String furniturestatus;
  @Getter @Setter public String furnituredeliveryroom;
  @Getter @Setter public String furnituredeliverydate;
  @Getter @Setter public String furniturenotes;
  @Getter @Setter public String furnitureservicetype;
  @Getter @Setter public String furniturefurniture;

  public FurnitureOrderObservable() {
    this.furnitureid = furnitureid;
    this.furnitureassignee = furnitureassignee;
    this.furniturestatus = furniturestatus;
    this.furnituredeliveryroom = furnituredeliveryroom;
    this.furnituredeliverydate = furnituredeliverydate;
    this.furniturenotes = furniturenotes;
    this.furnitureservicetype = furnitureservicetype;
    this.furniturefurniture = furniturefurniture;
  }
}
