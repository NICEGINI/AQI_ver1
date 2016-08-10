package kr.ac.kmu.cs.airpollution;

import java.util.UUID;

/**
 * Created by pabel on 2016-08-03.
 */
public class Const {
    public static final String UUID_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DEVICE_NAME = "00002a05-0000-1000-8000-00805f9b34fb";

    public static boolean UDOO_CONNECT = false;
    private static String userEmail; // login user E-mail
    private static String userPassword;
    private static int CIRCLE_SIZE = 250;
    private static boolean UDOO_STATE = false;
   // private static int connectID_UDOO = -1;
    private static String UDOO_MAC;
    private static String UDOO_CONNECT_ID = "";

    public static void setUdooConnectId(String udooConnectId) {
        UDOO_CONNECT_ID = udooConnectId;
    }

    public static String getUdooConnectId() {
        return UDOO_CONNECT_ID;
    }


    public static String getUdooMac() {
        return UDOO_MAC;
    }

    public static void setUdooMac(String udooMac) {
        UDOO_MAC = udooMac;
    }




    public static String getUserEmail() {
        return userEmail;
    }
    public static String getUserPassword() {
        return userPassword;
    }


    //user info

    public static void setUserPassword(String userPassword) {
        Const.userPassword = userPassword;
    }

    public static void setUserEmail(String userEmail) {
        Const.userEmail = userEmail;
    }




    public static int getCircleSize() {
        return CIRCLE_SIZE;
    }

    public static void setCircleSize(int circleSize) {
        CIRCLE_SIZE = circleSize;
    }

    //Google Map


    public static boolean isUdooState() {
        return UDOO_STATE;
    }

    public static void setUdooState(boolean udooState) {
        UDOO_STATE = udooState;
    }







    //Realtime chart
    private static int REALTIME_DATASET_RANGE = 30;

    public static int getRealtimeDatasetRange(){
        return REALTIME_DATASET_RANGE;
    }

    public static void setRealtimeDatasetRange(int realtimeDatasetRange){
        REALTIME_DATASET_RANGE = realtimeDatasetRange;
    }

    // UDOO config
    private static int RECEIVE_TIME_FROM_UDOO = 5;

    public static int getReceiveTimeFromUdoo(){
        return RECEIVE_TIME_FROM_UDOO;
    }
    public static void setReceiveTimeFromUdoo(int receiveTimeFromUdoo){
        RECEIVE_TIME_FROM_UDOO = receiveTimeFromUdoo;
    }
}
