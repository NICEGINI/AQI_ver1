package kr.ac.kmu.cs.airpollution.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import kr.ac.kmu.cs.airpollution.R;

/**
 * Created by PYOJIHYE on 2016-07-25.
 */
public class IntroActivity extends Activity {
    Handler h;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        h=new Handler();
        h.postDelayed(irun,2000);
    }

    Runnable irun=new Runnable(){
        public void run(){
            Intent i=new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    };
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        h.removeCallbacks(irun);
    }
}