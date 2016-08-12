package kr.ac.kmu.cs.airpollution.Buffer;

import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.kmu.cs.airpollution.Const;

/**
 * Created by pabel on 2016-07-28.
 */
public class realTimeBuffer {
    private static ArrayList<JSONObject> airDataBuffer = new ArrayList<JSONObject>();
    public static ArrayList<JSONObject> getAirDataBuffer(){
        return airDataBuffer;
    }
    public static void insertData(JSONObject jsonObject){

        airDataBuffer.add(jsonObject);
        if(airDataBuffer.size() > 120){
            airDataBuffer.remove(0);
        }

    }
    public static int getLength(){
        return airDataBuffer.size();
    }
    public static JSONObject indexOf(int index){return airDataBuffer.get(index);}
}
