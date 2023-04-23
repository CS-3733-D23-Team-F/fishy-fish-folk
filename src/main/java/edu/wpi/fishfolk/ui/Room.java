package edu.wpi.fishfolk.ui;

@Deprecated
public class Room {
  public int roomNumber;

  public Room(int rNum) {
    roomNumber = rNum;
  }

  public static Room generic1 = new Room(116),
      generic2 = new Room(243),
      generic3 = new Room(191),
      generic4 = new Room(332);

  public String toString() {
    return "Room #" + roomNumber;
  }
}
