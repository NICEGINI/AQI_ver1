package kr.ac.kmu.cs.airpollution.controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kmu.cs.airpollution.Buffer.locBuffer;
import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;
import kr.ac.kmu.cs.airpollution.service.realtimeService;

/**
 * Created by pabel on 2016-08-09.
 */
public class httpController extends AsyncTask<String, String, String> {

    public enum HTTP {
        REQ_LOGIN(0), REQ_CONNECT_UDOO(1), REQ_CONNECT_HEART(2), SEND_UDOO_REAL(3), SEND_HEART_REAL(4),SEND_CSV(5),REQ_ALL_AVE_AIR_DATA(6);
        private int value;

        private HTTP(int value) {
            value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;
    HTTP now;

    Context context; // 컨텍스트
    int control;
    int type;
    HashMap<String, String> temp = new HashMap<>();
    HttpURLConnection conn; // 커넥터
    URL url = null; // 주소
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public static final String URL_LOGIN = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/loginAPP";
    public static final String URL_CONNECT = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/requestConnection";
    public static final String URL_TRANSFER = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/transferData";
    public static final String URL_CSV = "http://teamb-iot.calit2.net/week3b/bluebase/main/test/receivecsv.php";
    public static final String URL_REQ_AVE_AIR = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/avgValue";

    public void showMsgDialog(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();
    }

    public httpController(Context context) {
        super();
        this.context = context;

    }

    public void setJSONSetting() {
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
    }

    //===================================================================
    //로그인 하는부분
    // Triggers when LOGIN Button clicked
    public void checkLogin(String email, String password) {
        // Initialize  AsyncLogin() class with email and password
        temp.put("email", email);
        temp.put("password", password);
        now = HTTP.REQ_LOGIN;
        execute(URL_LOGIN, email, password);
    }
//=========================================================================
//세션키 받는 부분

    public String makeJSON(String email, String recTime, String devMAC) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("recTime", recTime);
            jsonObject.put("devMAC", devMAC);
            return jsonObject.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void reqConnect(String email, String recTime, String devMAC,int type) {
        //devMAC = FF:FF:FF:FF 와 같이 콜론이 붙어있음 없애줘야됨.
        Log.d("reqConnect",devMAC);
        Log.d("reqConnect",email);

        String temp[] = devMAC.split(":");
        String MAC = "x'";
        for (int i = 0; i < temp.length; i++) {
            MAC = MAC.concat(temp[i]);
        }
        //0 = udoo , 1 = heart
        switch (type){
            case 0:
                now = HTTP.REQ_CONNECT_UDOO;
                break;
            case 1:
                now = HTTP.REQ_CONNECT_HEART;
                break;
        }

        String json = makeJSON(email, recTime, MAC);
        // Initialize  AsyncLogin() class with email and password
        Log.d("reqConnect",json);

        execute(URL_CONNECT, json);
        Log.d("URL_CONNECT","URL_CONNECT");
    }
    public void sendCSV(String fileName){
        now = HTTP.SEND_CSV;
        execute(URL_CSV,fileName);
    }

    //=====================================================================
    //리얼 타임 전송하는 부분
//    {
//        "connectionID": 2,
//            "devType": 1,
//            "data": [{
//        " timestamp " : 1470542902,
//                "SO2": 100,
//                "NO2": 77,
//                "O3": 77,
//                "CO": 77,
//                "PM": 77,
//                "temperature": 77,
//                "latitude": 54.00,
//                "longitude":110.33
//    }]
//  }

    public void sendRealtimeUdoo(String connectID,String json){
        now = HTTP.SEND_UDOO_REAL;
        try {
            JSONObject parser = new JSONObject();
            parser.put("connectionID",connectID);
            parser.put("devType","1");

            JSONObject temp = new JSONObject(json);
            temp.put("latitude", String.valueOf(locBuffer.getLat()));
            temp.put("longitude",String.valueOf(locBuffer.getLng()));
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(temp);

            parser.put("data",jsonArray);
            Log.d("udooreal","send real udoo");
            Log.d("RealTimeData",parser.toString());
            execute(URL_TRANSFER,parser.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
//    {
//        "connectionID": 2,
//            "devType": 0,
//            "data": [{
//        "timestamp" : 1470542905,
//                "heartrate": 100,
//                "latitude": 54.00,
//                "longitude":110.33
//    },

    public void sendRealtimeHeart(String connectID,String json){
        now = HTTP.SEND_HEART_REAL;
        Log.d("ble","send REAL TIME DATA");

//            JSONObject parser = new JSONObject();
//            parser.put("connectionID",connectID);
//            parser.put("devType","0");
//
//            JSONObject temp = new JSONObject(json);
//            temp.put("latitude", String.valueOf(locBuffer.getLat()));
//            temp.put("longtitude",String.valueOf(locBuffer.getLng()));
//            JSONArray jsonArray = new JSONArray(temp);
//
//
//            parser.put("data",jsonArray);
        Log.d("RealTimeData",json);
        execute(URL_TRANSFER,json);

    };

    // request ave air data
    public void recieve_ave_PMpollution(String airtype, double lat, double lng, double
            radius) {
        now = HTTP.REQ_ALL_AVE_AIR_DATA;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sensorType", airtype);
            jsonObject.put("latitude", lat);
            jsonObject.put("longitude", lng);
            jsonObject.put("radius", radius);

            Log.d("send_air_json",jsonObject.toString());
            execute(URL_REQ_AVE_AIR,jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //==================================================================


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            url = new URL(params[0]);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "exception";
        }
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            if(now == HTTP.SEND_CSV){
                conn.setRequestProperty("uploaded_file", params[1]);


            }
            String query = "";
            if(now == HTTP.REQ_LOGIN){
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2]);
                query = builder.build().getEncodedQuery();
            } else
            {
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                query = params[1];
            }
            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Append parameters to URL


            // Open connection for sending data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "exception";
        }

        try {
            //String temp = conn.getResponseMessage();
            int response_code = conn.getResponseCode();
            String temp = conn.getResponseMessage();
            Log.d("tttt",response_code+"");
            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("tttt2",result.toString());
                // Pass data to onPostExecute method
                return(result.toString());

            }else{

                return("unsuccessful connect");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        } finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("onPostExecute",result);
        String status = "";
        String connectID;
        String all_ave_air_data="";
        int type = -1;
        try {
            JSONObject parser = null;
            if(!result.equals("unsuccessful connect")) {
                parser = new JSONObject(result);

                //type , status
                status = parser.getString("status");
                type = parser.getInt("type");
            }
            if(status == null) return;
            if(type != -1){
                switch (type){
                    case 0:
                        switch (now){
                            case REQ_LOGIN:
                                Intent intent = new Intent(context, MainActivity.class);
                                Const.setUserEmail(temp.get("email"));
                                Const.setUserPassword(temp.get("password"));

                                context.startActivity(intent);
                                break;
                            case REQ_CONNECT_UDOO:
                                connectID = parser.getString("connectionID");
                                Const.setUdooConnectId(connectID);
                                Log.d("Req_connect_udoo",connectID);
                                Toast.makeText(context,"Receive UDOO Connection ID",Toast.LENGTH_LONG).show();
                                //Toast.makeText(context,"성공적으로 받음"+connectID,Toast.LENGTH_SHORT);
                                break;
                            case REQ_CONNECT_HEART:
                                connectID = parser.getString("connectionID");
                                Const.setHeartConnectId(connectID);
                                Log.d("Req_connect_heart",connectID);
                                Toast.makeText(context,"Receive Polar Connection ID",Toast.LENGTH_LONG).show();
                                realtimeService.setHeartConnect(true);
                                break;
                            case SEND_HEART_REAL:
                                break;
                            case SEND_UDOO_REAL:
                                Log.d("realtime","yes send");
                                break;
                            case REQ_ALL_AVE_AIR_DATA:
                                all_ave_air_data = parser.getString("data");
                                if(all_ave_air_data.toString().equals("null"))
                                    all_ave_air_data = "0";
                                Const.setAll_ave_pm_data(Double.parseDouble(all_ave_air_data));
                                Const.drawCircle();
                                break;
                        }

                        //성공했을때.
                        break;
                    case 1:
                        if(status.contains("email")){
                            showMsgDialog("check your information correctly.");
                        }
                        Log.d("http fail","http fail 1");
                        //
                        break;
                    case 2:
                        if(status.contains("password")){
                            showMsgDialog("check your information correctly.");
                        }
                        Log.d("http fail","http fail 2");
                        //

                        break;
                    default:
                        //showMsgDialog("check your connect");
                        break;

                }
            } else ;//showMsgDialog("error");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
