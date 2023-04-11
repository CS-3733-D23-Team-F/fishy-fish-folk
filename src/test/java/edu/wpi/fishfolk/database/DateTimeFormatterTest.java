package edu.wpi.fishfolk.database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class DateTimeFormatterTest {

  @Test
  public void test() {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    System.out.println("chk1");

    String day1 = "01/03/2023";
    String day2 = "01/02/2023";
    System.out.println(LocalDate.parse(day1, format).compareTo(LocalDate.parse(day2, format)));

    System.out.println("chk2");
  }
}
