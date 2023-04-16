package edu.wpi.fishfolk.database;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Move extends TableEntry {
  public String longName;
  public String date;

  public static final DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");

  @Override
  public boolean construct(ArrayList<String> data) {
    if (data.size() != 8) {
      return false;
    }

    this.id = data.get(0);
    this.longName = data.get(1);
    this.date = data.get(2);
    return true;
  }

  @Override
  public ArrayList<String> deconstruct() {
    ArrayList<String> data = new ArrayList<>();
    data.add(this.id);
    data.add(this.longName);
    data.add(this.date);

    return data;
  }

  public Move(int nodeId, String longName, String date) {
    this.id = Integer.toString(nodeId);
    this.longName = longName;
    this.date = date;
  }

  public static String sanitizeDate(String date) {
    String[] pieces = date.split("/");
    // add 0's to single digit days and months
    if (pieces[0].length() == 1) pieces[0] = "0" + pieces[0];
    if (pieces[1].length() == 1) pieces[1] = "0" + pieces[1];
    if (pieces[2].length() == 2) pieces[2] = "20" + pieces[2];
    return String.join("/", pieces);
  }
}
