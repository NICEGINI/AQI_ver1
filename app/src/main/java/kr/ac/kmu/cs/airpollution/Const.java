package kr.ac.kmu.cs.airpollution;

import java.util.UUID;

/**
 * Created by pabel on 2016-08-03.
 */
public class Const {
    public static boolean UDOO_CONNECT = false;
    public static String getUserEmail() {
        return userEmail;
    }
    public static String getUserPassword() {
        return userPassword;
    }

    //user info
    private static String userEmail; // login user E-mail
    public static void setUserPassword(String userPassword) {
        Const.userPassword = userPassword;
    }

    public static void setUserEmail(String userEmail) {
        Const.userEmail = userEmail;
    }

    private static String userPassword;


    public static int getCircleSize() {
        return CIRCLE_SIZE;
    }

    public static void setCircleSize(int circleSize) {
        CIRCLE_SIZE = circleSize;
    }

    //Google Map
    private static int CIRCLE_SIZE = 100;

    public static boolean isUdooState() {
        return UDOO_STATE;
    }

    public static void setUdooState(boolean udooState) {
        UDOO_STATE = udooState;
    }

    private static boolean UDOO_STATE = false;

    public static final String UUID_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";

    public static final String UUID_DEVICE_NAME = "00002a05-0000-1000-8000-00805f9b34fb";

    public static void setConnectID_UDOO(int connectID_UDOO) {
        Const.connectID_UDOO = connectID_UDOO;
    }

    public static int getConnectID_UDOO() {
        return connectID_UDOO;
    }

    private static int connectID_UDOO = -1;

}
