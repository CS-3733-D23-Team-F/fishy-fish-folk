package edu.wpi.fishfolk;

public class CreditCardInfo {
    public int expMonth;
    public int expYear;
    public String nameOnCard;
    public long cardNum;
    public int CCV;
    public CreditCardInfo(int mon, int yr, String name, long num, int ccv) {
        expMonth = mon;
        expYear = yr;
        nameOnCard = name;
        cardNum = num;
        CCV = ccv;
    }
    private static long dummyNum = ((long)12341234) * ((long)100000001);
    //this is just 1234123412341234, intellij doesn't like me just typing this in on its own
    public static CreditCardInfo dummy = new CreditCardInfo(7, 24, "Generic Payer", dummyNum, 666);
}
