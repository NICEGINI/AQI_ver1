package kr.ac.kmu.cs.airpollution.controller;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pabel on 2016-07-21.
 */

//json control class.managing all json parsing
public class jsonController {
    private final int SIZE = 9;
    private final static double FAKE_LAT_MAX = 32.894354;
    private final static double FAKE_LAT_MIN = 32.865018;
    private final static double FAKE_LON_MAX = -117.216477;
    private final static double FAKE_LON_MIN = -117.245813;

    private static String TAG = "JSON";
    private static String first_fakedata = null;
    private static String middle_fakedata = null;
    private static String end_fakedata = null;
    public static String jsonFile;

    private boolean flag;

    private JSONObject jsonObject = null;
    private JSONArray airdata = null;


    //Constructor
    public jsonController(String jsonFile, boolean flag) {
        this.flag = flag;
        this.jsonFile = jsonFile;
        SetAirdata();
    }

    public void SetAirdata(){
        try {
            if(flag)
                jsonFile = CreateFakeJsonFile();

            jsonObject = new JSONObject(jsonFile);
            jsonObject = jsonObject.getJSONObject("sensorData");
            airdata = jsonObject.getJSONArray("dataArray");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONArray getAirdata(){
        return airdata;
    }

    public JSONObject getJsonObject(){
        return jsonObject;
    }


    public static String CreateFakeJsonFile(){
        double random = 0;
        double random_lat_long = 0.0;
        String airdata[] = new String[7];

        //addition part
        String lat_long[] = new String[2];

        for(int i=0; i < airdata.length; i++)
        {
            random =  (float) (Math.random() * 501);
            random = Math.round(random*10d) /10d;
            airdata[i] = Double.toString(random);
        }

        //addition part
        for(int i= 0; i < lat_long.length; i++){
            if(i==0)
                random_lat_long =  (double) (Math.random() * (FAKE_LAT_MAX - FAKE_LAT_MIN) + FAKE_LAT_MIN);
            else
                random_lat_long =  (double) (Math.random() * (FAKE_LON_MAX - FAKE_LON_MIN) + FAKE_LON_MIN);
            lat_long[i] = Double.toString(random_lat_long);
        }

        first_fakedata =
                "{\"sensorData\": {\n" +
                        "    \"totalRows\": \"1\",\n" +
                        "    \"dataArray\":[{";

        middle_fakedata =
                        "            \"time\": "+airdata[0]+",\n" +
                        "            \"co\": "+airdata[1]+",\n" +
                        "            \"so2\": "+airdata[2]+",\n" +
                        "            \"no2\": "+airdata[3]+",\n" +
                        "            \"pm2.5\": "+airdata[4]+",\n" +
                        "            \"o3\": "+airdata[5]+",\n"+
                       //addition part
                        "            \"temperature\": "+airdata[6]+",\n"+
                        "            \"latitude\": "+lat_long[0]+",\n" +
                        "            \"longitude\": "+lat_long[1]+"\n";

        end_fakedata =
                        " }\n" +
                        "    ]\n" +
                        "\t},\n" +
                        "\t\"sensorInfo\": {\n" +
                        "\t\t\"name\": \"air sensor\"\n" +
                        "\t}\n" +
                        "}    ";
        jsonFile = first_fakedata + middle_fakedata + end_fakedata;

        return jsonFile;
    }
    public static LatLng getLatlng(JSONObject jsonObj){
        LatLng result;
        JSONObject temp;
        double lat,lng;
        Log.d("testtest",jsonObj.toString());
        try {
            temp = jsonObj;
//            temp = jsonObj.getJSONObject("sensorData");
//            JSONArray arrayTest = temp.getJSONArray("dataArray");
//            temp = arrayTest.getJSONObject(0);
//
//

            lat = temp.getDouble("latitude");
            lng = temp.getDouble("longitude");
            result = new LatLng(lat,lng);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Float> splitJSON(String jsonObj){
        ArrayList<Float> temp = new ArrayList<Float>();
        JSONArray jsonArrayTemp = null;
        JSONObject jsonObjTemp = null;
        JSONObject jsonObjItemTemp = null;
        float fTemp;
        String strTemp;
        /* Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            SimpleDateFormat sdf = new SimpleDateFormat("HH: mm: ss");
            sdf.setTimeZone(tz);
            String localTime = sdf.format(new Date()); // I assume your timestamp is in seconds and you're converting to milliseconds?*/

        try {
            jsonObjTemp = new JSONObject(jsonObj);
            jsonObjTemp = jsonObject.getJSONObject("sensorData");
            jsonArrayTemp = jsonObject.getJSONArray("dataArray");
            jsonObjItemTemp = jsonArrayTemp.getJSONObject(0);
            if(jsonArrayTemp != null){
                temp.add(0,Float.parseFloat(jsonObjItemTemp.getString("co2")));
                temp.add(1,Float.parseFloat(jsonObjItemTemp.getString("co")));
                temp.add(2,Float.parseFloat(jsonObjItemTemp.getString("so2")));
                temp.add(3,Float.parseFloat(jsonObjItemTemp.getString("no2")));
                temp.add(4,Float.parseFloat(jsonObjItemTemp.getString("pm2.5")));
                temp.add(5,Float.parseFloat(jsonObjItemTemp.getString("o3")));

                // addition part
                temp.add(6,Float.parseFloat(jsonObjItemTemp.getString("temperature")));
                temp.add(7,Float.parseFloat(jsonObjItemTemp.getString("latitude")));
                temp.add(8,Float.parseFloat(jsonObjItemTemp.getString("longitude")));
            }
            else {
                Log.e(TAG,"jsonArrayTemp assignment fail");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"can't json parsing from splitJSON method");
        }
        return temp;
    }

    public static JSONObject getAirDate(String temp) {
        JSONObject result = null; // apply for received json file
        try {
            result = new JSONObject(temp);
            JSONArray tempArray; // temporary array
            result = result.getJSONObject("sensorData"); // 센서데이터 안으로 들어감
            tempArray = result.getJSONArray("dataArray"); // 안에있는 어레이 받아옴
            result = tempArray.getJSONObject(0); // 어레이 안에 값을 뜯어옴
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"getAirData is don't working");
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Unexpected exception");
        }

        return result;
    }
}
