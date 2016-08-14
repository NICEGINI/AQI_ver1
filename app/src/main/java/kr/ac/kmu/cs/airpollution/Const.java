package kr.ac.kmu.cs.airpollution;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private static double radius_to_latlng=0;

    private static double geo_lat;
    private static double geo_lng;
    private static LatLng geo_latlng;
    private static String select_location;
    private static GoogleMap mGoogleMap;
    private static Marker marker;
    private static Circle circle;


    public static void set_sel_location(String loc){
        select_location = loc;

    }
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
        radius_to_latlng =  circleSize * 0.00000721815;
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

    public static void setRadius_to_latlng() {
        radius_to_latlng =  CIRCLE_SIZE * 0.00000721815;
    }

    public static double getradius_to_latlng()
    {
        return radius_to_latlng;
    }

    public static void setgeo_latlng(double lat, double lng){
        geo_latlng = new LatLng(lat, lng);
    }

    public static void setmGoogleMap(GoogleMap gmap){
        mGoogleMap = gmap;
    }

    public static void drawCircle(){
        if (circle != null) circle.remove();
        if(marker != null) marker.remove();

        circle = mGoogleMap.addCircle(new CircleOptions().center(geo_latlng).
                radius(Const.getCircleSize()).strokeColor(Color.parseColor("#ff000000")).fillColor(Color.parseColor(setBackgroundColor(all_ave_pm_data))));

        // 맵 위치를 이동하기
        CameraUpdate update = CameraUpdateFactory.newLatLng(geo_latlng);

        mGoogleMap.moveCamera(update);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(geo_latlng)
                .title(select_location)
                .snippet(setCurrentAQIlevel(all_ave_pm_data))
                .icon(BitmapDescriptorFactory.defaultMarker((float)all_ave_pm_data)); // need to modify.

        marker = mGoogleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        marker.setVisible(true);
    }

    //set icon color
    public double setIconColor(float num){
        return  (num < 51) ? 110 : (num < 101) ? 50 : (num < 151) ? 35 : (num < 200) ? 0 :
                (num < 301) ? 290 : (num < 500) ? 10 : 10;
    }

    // setting air color
    public static String setBackgroundColor(double num){
        return (num == 0d) ? "#00000000" : (num < 51) ? "#4000e400" : (num < 101) ? "#40d3d327" : (num < 151) ? "#40ff7e00" : (num < 200) ? "#40ff0000" :
                (num < 301) ? "#408f3f97" : (num < 500) ? "#407e0023" : "#407e0023";
    }

    // setting AQI level
    public static String setCurrentAQIlevel(double num){
        return  (num == 0d) ? "No data" : (num < 51) ? "Good" : (num < 101) ? "Moderrate" : (num < 151) ? "Unhealthy for sensitive groups" : (num < 200) ? "Unhealthy" :
                (num < 301) ? "Very unhealthy" : (num < 500) ? "Hazardous" : "Hazardous";
    }
}
