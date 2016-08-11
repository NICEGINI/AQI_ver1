package kr.ac.kmu.cs.airpollution;

/**
 * Created by KCS on 2016-08-11.
 */
public class Const_rr_data {
    public static int total_HR = 0;
    public static int count_nnF = 0;
    public static int pre_RR = 0;

    private Const_rr_data (){ }
    public static Const_rr_data instance = new Const_rr_data();
    public static Const_rr_data getInstance() { return instance;}

}
