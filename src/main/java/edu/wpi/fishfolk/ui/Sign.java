package edu.wpi.fishfolk.ui;

import lombok.Getter;
import lombok.Setter;

public class Sign {

  @Getter @Setter private String label;
  @Getter @Setter private double direction;
  @Getter @Setter private String subtext;

  public Sign(String label, double direction, String subtext) {
    this.label = label;
    this.direction = direction;
    this.subtext = subtext;
  }
}
