package edu.wpi.fishfolk.ui;

import edu.wpi.fishfolk.database.TableEntry;
import java.util.ArrayList;
import java.util.LinkedList;

public class FlowerOrder extends TableEntry {

  public LinkedList<FlowerItem> items;

  public String deliveryTime;
  public CreditCardInfo payer;
  public String deliveryLocation;
  public float totalPrice;

  public FormStatus formStatus;
  public String formID;
  public String assignee;

  public FlowerOrder() {
    items = new LinkedList<FlowerItem>();
    deliveryTime = "";
    payer = CreditCardInfo.dummy;
    deliveryLocation = "";
    formStatus = FormStatus.notSubmitted;
    formID = "" + System.currentTimeMillis();
    formID = formID.substring(formID.length() - 10);
    formStatus = FormStatus.notSubmitted;
  }

  public void addItem(FlowerItem flower) {
    items.add(flower);
    totalPrice += flower.fullCost;
  }

  public void removeItem(FlowerItem flower) {
    if (items.contains(flower)) {
      items.remove(flower);
      totalPrice -= flower.fullCost;
    }
  }

  public boolean submit() {
    if (deliveryTime.equals("") || payer == null || deliveryTime.equals("") || items.isEmpty())
      return false;
    formStatus = FormStatus.submitted;
    return true;
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
