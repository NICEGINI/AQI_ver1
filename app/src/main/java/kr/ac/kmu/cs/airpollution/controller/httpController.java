package kr.ac.kmu.cs.airpollution.controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;

/**
 * Created by pabel on 2016-08-09.
 */
public class httpController extends AsyncTask<String, String, String> {
    //params[0] = url
    //type 0 = 로그인 , 1 = 세션키 , ...
    Context context; // 컨텍스트
    int control;
    int type;
    HashMap<String,String> temp = new HashMap<>();
    HttpURLConnection conn; // 커넥터
    URL url = null; // 주소
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_LOGIN = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/loginAPP";
    public static final String URL_CONNECT = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/requestConnection";
    public static final String URL_TRANSFER = "http://teamb-iot.calit2.net/week3b/bluebase/receive/recieveApp.php/tansferData";

    public void showMsgDialog(String msg){
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
    public httpController(Context context,int type){
        super();
        this.context = context;
        this.type = type;
    }
    public void setJSON(){
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
    }
    //===================================================================
    //로그인 하는부분
    // Triggers when LOGIN Button clicked
    public void checkLogin(String email,String password) {
        // Initialize  AsyncLogin() class with email and password
        temp.put("email",email);
        temp.put("password",password);
         execute(URL_LOGIN,email,password);
    }
//=========================================================================
//세션키 받는 부분

    public String makeJSON(String email,String recTime,String devMAC){
    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("email",email);
        jsonObject.put("recTime",recTime);
        jsonObject.put("devMAC",devMAC);
        return jsonObject.toString(4);

    } catch (JSONException e) {
        e.printStackTrace();
    }

    return "";
}
    public void reqConnect(String URL,String email,String recTime,String devMAC) {
        //devMAC = FF:FF:FF:FF 와 같이 콜론이 붙어있음 없애줘야됨.
        String temp[] = devMAC.split(":");
        String MAC = "x'";
        for(int i = 0; i<temp.length;i++){
            MAC =  MAC.concat(temp[i]);
        }
        String json = makeJSON(email,recTime,MAC);
        // Initialize  AsyncLogin() class with email and password
        execute(URL,json);
    }

    //=====================================================================


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

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);
            String query = "";
            if(type == 0 ){
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("password", params[2]);
                query = builder.build().getEncodedQuery();
            }
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
                        Intent intent = new Intent(context, MainActivity.class);
                        Const.setUserEmail(temp.get("email"));
                        Const.setUserPassword(temp.get("password"));

                        context.startActivity(intent);
                        //성공했을때.
                        break;
                    case 1:
                        showMsgDialog("login fail");

                        break;
                    case 2:
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
