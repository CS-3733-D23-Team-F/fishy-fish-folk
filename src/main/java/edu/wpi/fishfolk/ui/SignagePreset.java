package edu.wpi.fishfolk.ui;

import java.time.LocalDate;

public class SignagePreset {
  String presetName;

  LocalDate implementationDate;
  String rooml0;
  String rooml1;
  String rooml2;
  String rooml3;

  double directionl0;
  double directionl1;
  double directionl2;
  double directionl3;

  String roomr0;
  String roomr1;
  String roomr2;
  String roomr3;

  double directionr0;
  double directionr1;
  double directionr2;
  double directionr3;

  public SignagePreset() {
    this.presetName = presetName;
    this.implementationDate = implementationDate;

    this.rooml0 = rooml0;
    this.rooml1 = rooml1;
    this.rooml2 = rooml2;
    this.rooml3 = rooml3;

    this.roomr0 = roomr0;
    this.roomr1 = roomr1;
    this.roomr2 = roomr2;
    this.roomr3 = roomr3;

    this.directionl0 = directionl0;
    this.directionl1 = directionl1;
    this.directionl2 = directionl2;
    this.directionl3 = directionl3;

    this.directionr0 = directionr0;
    this.directionr1 = directionr1;
    this.directionr2 = directionr2;
    this.directionr3 = directionr3;
  }

  public void setName(String string) {
    this.presetName = string;
  }

  public void setDate(LocalDate date) {
    this.implementationDate = date;
  }

  public void setRooml0(String room) {
    this.rooml0 = room;
  }

  public void setRooml1(String room) {
    this.rooml1 = room;
  }

  public void setRooml2(String room) {
    this.rooml2 = room;
  }

  public void setRooml3(String room) {
    this.rooml3 = room;
  }

  public void setRoomr0(String room) {
    this.roomr0 = room;
  }

  public void setRoomr1(String room) {
    this.roomr1 = room;
  }

  public void setRoomr2(String room) {
    this.roomr2 = room;
  }

  public void setRoomr3(String room) {
    this.roomr3 = room;
  }

  public void setDirectionl0(double degrees) {
    this.directionl0 = degrees;
  }

  public void setDirectionl1(double degrees) {
    this.directionl1 = degrees;
  }

  public void setDirectionl2(double degrees) {
    this.directionl2 = degrees;
  }

  public void setDirectionl3(double degrees) {
    this.directionl3 = degrees;
  }

  public void setDirectionr0(double degrees) {
    this.directionr0 = degrees;
  }

  public void setDirectionr1(double degrees) {
    this.directionr1 = degrees;
  }

  public void setDirectionr2(double degrees) {
    this.directionr2 = degrees;
  }

  public void setDirectionr3(double degrees) {
    this.directionr3 = degrees;
  }
}
