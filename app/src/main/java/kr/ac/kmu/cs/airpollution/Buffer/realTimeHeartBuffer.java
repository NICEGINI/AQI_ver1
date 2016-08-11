package kr.ac.kmu.cs.airpollution.Buffer;

import java.util.ArrayList;

/**
 * Created by KCS on 2016-08-08.
 */
public class realTimeHeartBuffer {
    private static ArrayList<Integer> heartRateBuffer = new ArrayList<>();

    public static int getNow() {
        return now;
    }

    private static int now;
    public static void Insert_Heart_Data(int hr){
        heartRateBuffer.add(hr);
        now = hr;
    }
    public static int indexof(int index){ return heartRateBuffer.get(index);}
    public static int getLength(){
        return heartRateBuffer.size();
    }
}
