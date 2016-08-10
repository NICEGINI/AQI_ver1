package kr.ac.kmu.cs.airpollution.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.controller.httpController;
import kr.ac.kmu.cs.airpollution.fragment.User_Setting_Fragment;

/**
 * Created by KCS on 2016-08-02.
 */
public class LoginActivity extends AppCompatActivity {
    private Button btn_Signin;
    private Button btn_Signup;
    private Button btn_forgot_password;
    private EditText et_Email;
    private EditText et_Password;

    private boolean test_flag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        // initialize
        btn_Signin = (Button) findViewById(R.id.btn_SignIn);
        btn_Signup = (Button)findViewById(R.id.btn_SignUp);
        btn_forgot_password = (Button)findViewById(R.id.btn_ForgotPassword);
        et_Email = (EditText)findViewById(R.id.et_Email);
        et_Password = (EditText)findViewById(R.id.et_Password);

        setLocationPermission();

        btn_Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Signin button click", Toast.LENGTH_SHORT).show();
                User_Setting_Fragment.setUserEmail(et_Email.getText().toString());
                // if user don't fill any fields.
                if(isEmpty()) showMsgDialog("You must fill all fields.");

                else {
                    if(test_flag) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        et_Password.setText("");
                        startActivity(intent);
                }
                    else {
                        new httpController(LoginActivity.this).checkLogin(et_Email.getText().toString(), et_Password.getText().toString());;

                        et_Password.setText("");
                      //=================================================================
                        //커넥션부분
//                        String email = "shineleaver@gmail.com";
//                        String devMAC = "A1:B2:C3:D4:E5:F6";
//                        long epoch = System.currentTimeMillis()/1000;
//                        String recTime = Long.toString(epoch);
//                        new httpController(LoginActivity.this).reqConnect(email,recTime,devMAC);
                        //=============================================================
                    }
                }
            }
        }) ;

        //button Sign up
        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Sign up click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),WebControlActivity.class);
                WebControlActivity.setUrl("http://teamb-iot.calit2.net/week3b/bluebase/signup/signup.html");//"http://teamb-iot.calit2.net/week3b/bluebase/signup/signup.html"
                startActivity(intent);
                et_Password.setText("");
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://teamb-iot.calit2.net/week3b/bluebase/signup/signup.html"));
//                startActivity(intent);
            }
        }) ;

        //button Forgot password
        btn_forgot_password.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(v.getContext(), "forgot password click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),WebControlActivity.class);
                WebControlActivity.setUrl("http://teamb-iot.calit2.net/week3b/bluebase/resetpassword/verify-email.html");
                startActivity(intent);
                et_Password.setText("");
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://teamb-iot.calit2.net/week3b/bluebase/resetpassword/verify-email.html"));
//                startActivity(intent);
            }
        });
    }

    public void setLocationPermission(){
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    public void showMsgDialog(String msg){
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();
    }

    public boolean isEmpty(){
        if(et_Email.getText().length() == 0 || et_Password.getText().length() == 0)
             return true;
        else return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Request allowance
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            else {
                // Do something...
            }
        }
//        try {
//            JSONObject tempobj = new JSONObject(jsonController.CreateFakeJsonFile());
//            LatLng test = jsonController.getLatlng(tempobj);
//            if(test == null){
//                Toast.makeText(getBaseContext(),"test is null",Toast.LENGTH_LONG).show();
//            }else
//                Toast.makeText(getBaseContext(),test.toString(),Toast.LENGTH_LONG).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
//==========================================
    //로그인을 위한 http통신
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;



    // Triggers when LOGIN Button clicked
    public void checkLogin(String email,String password) {

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email,password);
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        //            ProgressDialog pdLoading = new ProgressDialog(getBaseContext());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
//                pdLoading.setMessage("\tLoading...");
//                pdLoading.setCancelable(false);
//                pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.URL_LOGIN).toString());

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

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

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
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            Const.setUserEmail(et_Password.getText().toString());
                            Const.setUserEmail(et_Email.getText().toString());

                            startActivity(intent);
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
                et_Password.setText("");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
//================================================================================
}