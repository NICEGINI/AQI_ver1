package kr.ac.kmu.cs.airpollution.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.ac.kmu.cs.airpollution.Buffer.locBuffer;
import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.fragment.realtimeChartFragment;


/**
 * Created by pabel on 2016-07-28.
 */
public class Realtime_Chart_Activity extends Activity implements OnMapReadyCallback,LocationListener {
    private static String TAG = "Realtime_Chart_Activity";
    private static String baseSeleted;
    private static int val;
    private String select_location;
    private String apiURL;

    private LatLng position;
    private double lat; // latitude
    private double lon; // longitude
    private double geo_lat;
    private double geo_lng;
    private LatLng geo_latlng;
    private Location location;

    private Fragment chartFragment = null;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    public GoogleMap mGoogleMap;
    private Marker marker;
    private Circle circle;
    private LocationManager locationManager;

    private realtimeChartFragment.ICallback mCallback = new realtimeChartFragment.ICallback() {
        @Override
        public void changePosition(LatLng position) {
            if(position != null) {
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                setLATLNG(position);
                setCircleNMarker();
            }
            Log.d("testtest","okok");
        }
    };


    public static void setSeleted(String str) {
        baseSeleted = str;
    }

    public static String getBaseSeleted() {
        return baseSeleted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_chart);

        chartFragment = new realtimeChartFragment();
        realtimeChartFragment.registerCallback(mCallback);

        Log.d(TAG, "Enter the onCreate");
        getFragmentManager().beginTransaction()
                .replace(R.id.chartFragment, chartFragment).commit();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "end fragment");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //이곳에서 모든 구글맵에 관한 메소드를 적용시킴 리스너 포함
        mGoogleMap = googleMap;
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        //if(position != null) mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15));

        //googleMap.setTrafficEnabled(true);
        //googleMap.setIndoorEnabled(true);
        //googleMap.setBuildingsEnabled(true);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        //Marker seoul = mGoogleMap.addMarker(new MarkerOptions().position(SEOUL).title("Seoul"));

        if (locBuffer.getCurrentLoc() != null) {
            lat = locBuffer.getLat();
            lon = locBuffer.getLng();

            position = new LatLng(lat, lon);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }

        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setLATLNG(latLng);

                apiURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                        + geo_lat + "," + geo_lng;

                new AsyncTask<String, String, String>(){
                    @Override
                    protected void onPostExecute(String s) {
                        setCircleNMarker();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        getLocationResult(apiURL);
                        return null;
                    }
                }.execute();
            }
        });
    }

    //set Latitude Longitude
    public void setLATLNG(LatLng latLng){
        geo_lat = latLng.latitude;
        geo_lng = latLng.longitude;
        geo_latlng = new LatLng(geo_lat, geo_lng);
    }

    //set marker and circle
    public void setCircleNMarker(){
        if (circle != null) circle.remove();
        if(marker != null) marker.remove();

        circle = mGoogleMap.addCircle(new CircleOptions().center(geo_latlng).
                radius(Const.getCircleSize()).strokeColor(Color.parseColor("#ff000000")).fillColor(Color.parseColor(setBackgroundColor(40))));

        // 맵 위치를 이동하기
        CameraUpdate update = CameraUpdateFactory.newLatLng(geo_latlng);

        mGoogleMap.moveCamera(update);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(geo_latlng)
                .title(select_location)
                .snippet("AQI")
                .icon(BitmapDescriptorFactory.defaultMarker(setIconColor(140))); // need to modify.

        marker = mGoogleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        marker.setVisible(true);
    }

    //set icon color
    public int setIconColor(float num){
        return  (num < 51) ? 110 : (num < 101) ? 50 : (num < 151) ? 35 : (num < 200) ? 0 :
                (num < 301) ? 290 : (num < 500) ? 10 : 10;
    }

    // setting air color
    public String setBackgroundColor(double num){
        return (num < 51) ? "#8000e400" : (num < 101) ? "#80d3d327" : (num < 151) ? "#80ff7e00" : (num < 200) ? "#80ff0000" :
                (num < 301) ? "#808f3f97" : (num < 500) ? "#807e0023" : "#807e0023";
    }

    // setting AQI level
    public String setCurrentAQIlevel(double num){
        return  (num < 51) ? "Good" : (num < 101) ? "Moderrate" : (num < 151) ? "Unhealthy for sensitive groups" : (num < 200) ? "Unhealthy" :
                (num < 301) ? "Very unhealthy" : (num < 500) ? "Hazardous" : "Hazardous";
    }

    // get location from jsonfile
    private String getLocationResult(String Google_URL){
        String jsonString = new String();
        String buf;
        URL url = null;
        try {
            url = new URL(Google_URL);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            while ((buf = br.readLine()) != null) {
                jsonString += buf;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jObj = null;
        String result = "";
        try {
            jObj = new JSONObject(jsonString);
            JSONArray jArray = jObj.getJSONArray("results");
            jObj = (JSONObject) jArray.get(0);
            jArray = jObj.getJSONArray("address_components");
            result = (String) ((JSONObject) jArray.get(1)).get("short_name");
            result += ", "+ (String) ((JSONObject) jArray.get(2)).get("short_name");
            Log.d("json",jsonString);
            Log.d("json_res",result);
            select_location = result;
//            result = (String) jObj.get("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//
//        //Toast.makeText(MapsActivity.this, "위도  : " + lat + " 경도: " + lng, Toast.LENGTH_SHORT).show();
//
//        position = new LatLng(lat, lng);
//

        //Toast.makeText(getBaseContext(),"gps값 들어감",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

