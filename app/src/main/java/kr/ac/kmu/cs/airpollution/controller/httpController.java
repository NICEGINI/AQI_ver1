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

/**
 * Created by pabel on 2016-08-09.
 */
public class httpController extends AsyncTask<String, String, String> {

    public enum HTTP {
        REQ_LOGIN(0), REQ_CONNECT_UDOO(1), REQ_CONNECT_HEART(2), SEND_UDOO_REAL(3), SEND_HEART_REAL(4);
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
    public static final String URL_TRANSFER = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/tansferData";

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

    public void reqConnect(String email, String recTime, String devMAC) {
        //devMAC = FF:FF:FF:FF 와 같이 콜론이 붙어있음 없애줘야됨.
        Log.d("reqConnect",devMAC);
        Log.d("reqConnect",email);

        String temp[] = devMAC.split(":");
        String MAC = "x'";
        for (int i = 0; i < temp.length; i++) {
            MAC = MAC.concat(temp[i]);
        }
        now = HTTP.REQ_CONNECT_UDOO;
        String json = makeJSON(email, recTime, MAC);
        // Initialize  AsyncLogin() class with email and password
        Log.d("reqConnect",json);

        execute(URL_CONNECT, json);
        Log.d("URL_CONNECT","URL_CONNECT");
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
                temp.put("longtitude",String.valueOf(locBuffer.getLng()));
                JSONArray jsonArray = new JSONArray(temp);


                parser.put("data",jsonArray);
                execute(URL_TRANSFER,parser.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

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
            String query = "";
            if(now == HTTP.REQ_LOGIN){
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2]);
                query = builder.build().getEncodedQuery();
            } else
            {
                setJSONSetting();
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
        int type = -1;
        try {
            JSONObject parser = new JSONObject(result);
            //type , status
            status = parser.getString("status");
            type = parser.getInt("type");
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
                                String connectID = parser.getString("connectionID");
                                Const.setUdooConnectId(connectID);
                                Log.d("Req_connect_udoo",connectID);
                                Toast.makeText(context,"성공적으로 받음"+connectID,Toast.LENGTH_SHORT);
                                break;
                            case SEND_UDOO_REAL:
                                Log.d("realtime","yes send");
                                break;
                        }

                        //성공했을때.
                        break;
                    case 1:
                        Log.d("http fail","http fail 1");
                        showMsgDialog("login fail");
                        break;
                    case 2:
                        Log.d("http fail","http fail 2");
                        showMsgDialog("login fail");

                        break;
                    default:
                        showMsgDialog("check your connect");
                        break;

                }
            } else showMsgDialog("error");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
