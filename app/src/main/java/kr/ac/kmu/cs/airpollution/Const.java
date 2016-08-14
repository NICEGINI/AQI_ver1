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
    private static int REALTIME_DATASET_RANGE = 30;
    private static int RECEIVE_TIME_FROM_UDOO = 5;
    private static boolean UDOO_STATE = false;
    // private static int connectID_UDOO = -1;
    private static String UDOO_MAC;
    private static String UDOO_CONNECT_ID = "";
    private static String HEART_CONNECT_ID = "";
    private static int battery = 0;
    private static long start_chart_time = 0;
    private static double all_ave_pm_data=0;
    private static float f_all_ave_pm_data=0;

    public static long getStart_chart_time() {
        return start_chart_time;
    }

    public static void setStart_chart_time(long start_chart_time) {
        Const.start_chart_time = start_chart_time;
    }


    public static void setBattery(int battery) {
        Const.battery = battery;
    }

    public static int getBattery() {
        return battery;
    }



    public static void setHeartConnectId(String heartConnectId) {
        HEART_CONNECT_ID = heartConnectId;
    }

    public static String getHeartConnectId() {
        return HEART_CONNECT_ID;
    }



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
    public static int getRealtimeDatasetRange(){
        return REALTIME_DATASET_RANGE;
    }

    public static void setRealtimeDatasetRange(int realtimeDatasetRange){
        REALTIME_DATASET_RANGE = realtimeDatasetRange;
    }

    // UDOO config
    public static int getReceiveTimeFromUdoo(){
        return RECEIVE_TIME_FROM_UDOO;
    }
    public static void setReceiveTimeFromUdoo(int receiveTimeFromUdoo){
        RECEIVE_TIME_FROM_UDOO = receiveTimeFromUdoo;
    }

    public static void setAll_ave_pm_data(double data){
        all_ave_pm_data = data;
    }

    public static double getAll_ave_pm_data(){ return all_ave_pm_data;}
}
