package edu.wpi.fishfolk;

public class CreditCardInfo {
  public int expMonth;
  public int expYear;
  public String nameOnCard;
  public String cardNum;
  public int CCV;

  public CreditCardInfo(int mon, int yr, String name, String num, int ccv) {

    expMonth = mon;
    expYear = yr;
    nameOnCard = name;
    cardNum = num;
    CCV = ccv;
  }

  public static CreditCardInfo dummy =
      new CreditCardInfo(7, 24, "Generic Payer", "1234123412341234", 666);
}
