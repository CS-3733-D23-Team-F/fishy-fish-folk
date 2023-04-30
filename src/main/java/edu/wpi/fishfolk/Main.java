package edu.wpi.fishfolk;

import com.sun.javafx.application.LauncherImpl;

public class Main {

  public static void main(String[] args) {
    LauncherImpl.launchApplication(Fapp.class, Preloader.class, args); // run ui
  }
}
