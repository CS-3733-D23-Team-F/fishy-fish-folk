package edu.wpi.fishfolk.ui;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

public class SignagePreset {
  @Getter @Setter private String presetName;
  @Getter @Setter private LocalDate implementationDate;

  // ArrayList<Sign> signs;
  public Sign[] signs = new Sign[8];

  public SignagePreset() {}

  public void setName(String string) {
    this.presetName = string;
  }

  public void setDate(LocalDate date) {
    this.implementationDate = date;
  }

  public void addSign(Sign sign, int index) {
    signs[index] = sign;
  }
}
