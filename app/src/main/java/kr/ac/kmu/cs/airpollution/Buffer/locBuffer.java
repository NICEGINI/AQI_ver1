package kr.ac.kmu.cs.airpollution.Buffer;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pabel on 2016-08-09.
 */
public class locBuffer {
    private static locBuffer buffer = new locBuffer();
    private static double lat,lng;
    private static LatLng currentLoc;
    private static float accuracy;
    public static locBuffer getBuffer() {
        return buffer;
    }
    private locBuffer(){
        super();
    }

    public static void setCurrentLoction(Location location){
        lat = location.getLatitude();
        lng = location.getLongitude();
        currentLoc = new LatLng(lat,lng);
        accuracy = location.getAccuracy();
    }




    private static ArrayList<HashMap<Long,LatLng>> locBuffer = new ArrayList<>();
    private static long startTime = -1;
    public LatLng getLatLng(long Time){
        long index = Time - startTime;
        return locBuffer.get((int)index).get(Time);
    }
    public static void addLocData(HashMap<Long,LatLng> currentLoc){
        if(currentLoc != null){
            locBuffer.add(currentLoc);
        }
    }

    public static float getAccuracy() {
        return accuracy;
    }

    public static LatLng getCurrentLoc() {
        return currentLoc;
    }
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        currentLoc = new LatLng(lat,lng);
        accuracy = location.getAccuracy();
    }
}
