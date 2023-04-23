package edu.wpi.fishfolk.ui;

import java.time.LocalDate;

public class SignagePreset {
  String presetName;

  LocalDate implementationDate;
  String rooml0;
  String rooml1;
  String rooml2;
  String rooml3;

  int directionl0;
  int directionl1;
  int directionl2;
  int directionl3;

  String roomr0;
  String roomr1;
  String roomr2;
  String roomr3;

  int directionr0;
  int directionr1;
  int directionr2;
  int directionr3;

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
}
