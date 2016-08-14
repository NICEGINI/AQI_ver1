package kr.ac.kmu.cs.airpollution.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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

import kr.ac.kmu.cs.airpollution.controller.sendCSV;
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
    int i = 1;
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

                    }
                }
                //=================================================================
                //커넥션부분
//                        String email = "shineleaver@gmail.com";
//                        String devMAC = "A1:B2:C3:D4:E5:F6";
//                        long epoch = System.currentTimeMillis()/1000;
//                        String recTime = Long.toString(epoch);
//                        new httpController(LoginActivity.this).reqConnect(email,recTime,devMAC);
                //=============================================================
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

    }
}