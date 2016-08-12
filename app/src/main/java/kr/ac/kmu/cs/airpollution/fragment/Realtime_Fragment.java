package kr.ac.kmu.cs.airpollution.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import kr.ac.kmu.cs.airpollution.PagerAdapter.MyFragmentPagerAdapter;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;
import kr.ac.kmu.cs.airpollution.activity.Realtime_Chart_Activity;
import kr.ac.kmu.cs.airpollution.controller.jsonController;
import kr.ac.kmu.cs.airpollution.Buffer.realTimeHeartBuffer;


public class Realtime_Fragment extends Fragment {
    private static Realtime_Fragment Instance = new Realtime_Fragment();
    boolean HR_flag = false;
    private MainActivity.sendHRCallback HRCallback = new MainActivity.sendHRCallback() {
        @Override
        public void sendIntent(int heartRate) {
            if(HR_flag)
                HR_flag = false;
            else
                HR_flag = true;
            setheartcolor(HR_flag);
            setHeartColor(heartRate);
        }

        @Override
        public void setClear() {
            tv_hr.setText("N/A");
        }
    };
    // TextView value
    private TextView tv_time_val;
    private TextView tv_co_val;
    private TextView tv_no2_val;
    private TextView tv_so2_val;
    private TextView tv_pm_val;
    private TextView tv_o3_val;
    private TextView tv_temperature_val;
    private TextView tv_Current_State;
    private TextView tv_hr;

    //TextView title
    private TextView tv_co;
    private TextView tv_so2;
    private TextView tv_no2;
    private TextView tv_pm;
    private TextView tv_o3;

    //temporary save
    private TextView tv_temp;

    // Heart Image
    private ImageView iv_hr;

    // AQI Image
    private ImageView iv_current_AQI;

    //Button option
    private Button btn_option;
    private Button btn_goto_last;

    private View view = null;

//    private JSONObject jsonObj;

    private Realtime_Fragment(){
        super();
    }
    public static synchronized Realtime_Fragment getInstance(){
        return Instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_fragment_realtime_data,container,false);

        // set TextView variable
        setViewValues(view);
        registerForContextMenu(btn_option);
        MainActivity.registerHRCallback(HRCallback);
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_option.showContextMenu();

            }
        });

        btn_goto_last = (Button)view.findViewById(R.id.btn_go_to_last);
        btn_goto_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setPager(4);
            }
        });

        iv_hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setPager(1);
            }
        });

        return view;
    }

    public void setViewValues(View view){
        tv_time_val = (TextView)view.findViewById(R.id.tv_time_val);
        tv_co_val = (TextView)view.findViewById(R.id.tv_co_val);
        tv_no2_val = (TextView)view.findViewById(R.id.tv_no2_val);
        tv_so2_val = (TextView)view.findViewById(R.id.tv_so2_val);
        tv_pm_val = (TextView)view.findViewById(R.id.tv_pm2_5_val);
        tv_o3_val = (TextView)view.findViewById(R.id.tv_o3_val);
        tv_Current_State = (TextView)view.findViewById(R.id.tv_Currnet_State);
        tv_hr = (TextView)view.findViewById(R.id.tv_hr);
        tv_temperature_val = (TextView)view.findViewById(R.id.tv_temp_val);

        tv_Current_State.setTypeface(null, Typeface.BOLD);

        tv_co = (TextView)view.findViewById(R.id.tv_co);
        tv_so2 = (TextView)view.findViewById(R.id.tv_so2);
        tv_no2 = (TextView)view.findViewById(R.id.tv_no2);
        tv_pm = (TextView)view.findViewById(R.id.tv_pm2_5);
        tv_o3 = (TextView)view.findViewById(R.id.tv_o3);

        iv_current_AQI = (ImageView)view.findViewById(R.id.iv_current_AQI);
        iv_hr = (ImageView)view.findViewById(R.id.iv_hr);

        btn_option = (Button)view.findViewById(R.id.btn_option);
    }

    //setting color about Heart Rate
    public void setHeartColor(int hr){
        if(hr < 61) {
            if(hr<10)
                tv_hr.setText("00" + String.valueOf(hr));
            else
                tv_hr.setText("0" + String.valueOf(hr));
            iv_hr.setColorFilter((Color.rgb(127,17,0)));
        }
        else if(hr < 91) {
            if(hr<10)
                tv_hr.setText("00" + String.valueOf(hr));
            else
                tv_hr.setText("0" + String.valueOf(hr));
            iv_hr.setColorFilter((Color.rgb(255,170,170)));
        }
        else if(hr < 141){
            if(hr < 100)
                tv_hr.setText("0" + String.valueOf(hr));
            else
                tv_hr.setText(String.valueOf(hr));
            iv_hr.setColorFilter(Color.rgb(249,88,88));
        }
        else if(hr < 180){
            tv_hr.setText(String.valueOf(hr));
            iv_hr.setColorFilter(Color.rgb(255,0,0));
        }
        else {
            tv_hr.setText(String.valueOf(hr));
            iv_hr.setColorFilter(Color.rgb(127,84,78));
        }

        realTimeHeartBuffer.Insert_Heart_Data(hr);
        Heart_Rate_Chart_Fragment.setChart();
    }

    // setting almost air table
    public void set_view(String temp)
    {
    //    {"CO": 0.3, "NO2": 0.0, "O3": 0.0, "PM": 5.2, "SO2": 0.0, "temperature": 47, "timestamp": 1470909980}
        double co, no2, so2, pm, o3;
        int temperature;
        int hr;
        String AQI, color;

        try
        {
        //    jsonController jsonController = new jsonController(temp, true);
          //  JSONObject obj = jsonController.getAirdata().getJSONObject(0);
            JSONObject parser = new JSONObject(temp);

      //      Calendar cal = Calendar.getInstance();


            Calendar tt = Calendar.getInstance();
            tt.setTimeInMillis(parser.getLong("timestamp")*1000);
            TimeZone timeZone = tt.getTimeZone();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH: mm: ss");
            simpleDateFormat.setTimeZone(timeZone);
            String localTime = simpleDateFormat.format(new Date()); // I assume your timestamp is in seconds and you're converting to milliseconds?
            //시간 변경
            tv_time_val.setText(localTime);

//            hr =  (int) (Math.random() * (180 - 30)+30);
//            setHeartColor(hr, tv_hr, iv_hr);
            //User_Setting_Fragment.setUserHeartRate(hr);

            co = Double.parseDouble(parser.getString("CO"));
            color = setColor(co);
            //tv_co_val.setTextColor(Color.parseColor(color));
            AQI = setAQI(co,"CO", parser);
            tv_co_val.setText(AQI);


            so2 =Double.parseDouble(parser.getString("SO2"));
            color = setColor(so2);
            //tv_so2_val.setTextColor(Color.parseColor(color));
            AQI = setAQI(so2,"SO2", parser);
            tv_so2_val.setText(AQI);

            no2 = Double.parseDouble(parser.getString("NO2"));
            color = setColor(no2);
            //tv_no2_val.setTextColor(Color.parseColor(color));
            AQI = setAQI(no2,"NO2", parser);
            tv_no2_val.setText(AQI);

            pm = Double.parseDouble(parser.getString("PM"));
            color = setColor(pm);
           // tv_pm_val.setTextColor(Color.parseColor(color));
            AQI = setAQI(pm,"PM", parser);

            //addition part////
            //User_Setting_Fragment.setUserCurrentAQI(AQI);
            tv_Current_State.setText(setCurrentState(pm));
            tv_Current_State.setTextColor(Color.parseColor(color));
            /////////////////
            tv_pm_val.setText(AQI);

            o3 = Double.parseDouble(parser.getString("O3"));
            color = setColor(o3);
            //tv_o3_val.setTextColor(Color.parseColor(color));
            AQI = setAQI(o3,"O3", parser);
            tv_o3_val.setText(AQI);

            iv_current_AQI.setImageResource(setEmoticon(pm));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //원본 (페이크)
//    public void set_view(String temp)
//    {
//        //    {"CO": 0.3, "NO2": 0.0, "O3": 0.0, "PM": 5.2, "SO2": 0.0, "temperature": 47, "timestamp": 1470909980}
//        double co, no2, so2, pm, o3;
//        int temperature;
//        int hr;
//        String AQI, color;
//
//        try
//        {
//            jsonController jsonController = new jsonController(temp, true);
//            JSONObject obj = jsonController.getAirdata().getJSONObject(0);
//
//            Calendar cal = Calendar.getInstance();
//            TimeZone timeZone = cal.getTimeZone();
//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH: mm: ss");
//            simpleDateFormat.setTimeZone(timeZone);
//            String localTime = simpleDateFormat.format(new Date()); // I assume your timestamp is in seconds and you're converting to milliseconds?
//
//            tv_time_val.setText(localTime);
//
////            hr =  (int) (Math.random() * (180 - 30)+30);
////            setHeartColor(hr, tv_hr, iv_hr);
//            //User_Setting_Fragment.setUserHeartRate(hr);
//
//            co = Double.parseDouble(obj.getString("co"));
//            color = setColor(co);
//            //tv_co_val.setTextColor(Color.parseColor(color));
//            AQI = setAQI(co,"co", obj);
//            tv_co_val.setText(AQI);
//
//
//            so2 =Double.parseDouble(obj.getString("so2"));
//            color = setColor(so2);
//            //tv_so2_val.setTextColor(Color.parseColor(color));
//            AQI = setAQI(so2,"so2", obj);
//            tv_so2_val.setText(AQI);
//
//            no2 = Double.parseDouble(obj.getString("no2"));
//            color = setColor(no2);
//            //tv_no2_val.setTextColor(Color.parseColor(color));
//            AQI = setAQI(no2,"no2", obj);
//            tv_no2_val.setText(AQI);
//
//            pm = Double.parseDouble(obj.getString("pm2.5"));
//            color = setColor(pm);
//            // tv_pm_val.setTextColor(Color.parseColor(color));
//            AQI = setAQI(pm,"pm2.5", obj);
//
//            //addition part////
//            //User_Setting_Fragment.setUserCurrentAQI(AQI);
//            tv_Current_State.setText(setCurrentState(pm));
//            tv_Current_State.setTextColor(Color.parseColor(color));
//            /////////////////
//            tv_pm_val.setText(AQI);
//
//            o3 = Double.parseDouble(obj.getString("o3"));
//            color = setColor(o3);
//            //tv_o3_val.setTextColor(Color.parseColor(color));
//            AQI = setAQI(o3,"o3", obj);
//            tv_o3_val.setText(AQI);
//
//            iv_current_AQI.setImageResource(setEmoticon(pm));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    // setting current state
    public String setCurrentState(double num){
      return  (num < 51) ? "Good" : (num < 101) ? "Moderrate" : (num < 151) ? "Unhealthy for sensitive groups" : (num < 200) ? "Unhealthy" :
                (num < 301) ? "Very unhealthy" : (num < 500) ? "Hazardous" : "Hazardous";
    }

    // setting air AQI data
    public String setAQI(double num, String air, JSONObject obj){
        String AQI="";
        try {
            AQI = (num < 10) ? "00"+obj.getString(air) : (num < 100) ? "0"+obj.getString(air) : obj.getString(air);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return AQI;
    }

    // setting air color
    public String setColor(double num){
        return (num < 51) ? "#00e400" : (num < 101) ? "#d3d327" : (num < 151) ? "#ff7e00" : (num < 200) ? "#ff0000" :
                (num < 301) ? "#8f3f97" : (num < 500) ? "#7e0023" : "#7e0023";
    }

    // setting emoticon depending on each values.
    public int setEmoticon(double num){
        return (num < 51) ? R.drawable.good : (num < 101) ? R.drawable.moderate : (num < 151) ? R.drawable.unhealthy_for_sp : (num < 200) ? R.drawable.unhealthy :
                (num < 301) ? R.drawable.very_unhealthy : (num < 500) ? R.drawable.hazardous : R.drawable.hazardous;
    }

    // setting heart color depeding on flag.
    public void setheartcolor(boolean flag){
        if(flag)
            iv_hr.setImageResource(R.drawable.small_heart);
        else
            iv_hr.setImageResource(R.drawable.large_heart);
    }

 //setCheckBox
 //public String current_air;

    // Create Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Toast.makeText(getContext(),"Please, select option",Toast.LENGTH_SHORT).show();
        //Log.d("test","onCreateContextMenu");
        menu.setHeaderTitle("Option");
        menu.add(0,1,100, "Air data Graph");
        menu.add(0,2,100, "Air Data History");
        menu.add(0,3,100, "etc...");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //Event for selected items.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1: // select option1
                Toast.makeText(view.getContext(), "Option select.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),Realtime_Chart_Activity.class);

                startActivity(intent);
                realtimeChartFragment.flagSet.setFalse();
                //Realtime_Chart_Activity.setSeleted(current_air);
                return true;

            case 2: // select option2

                return true;

            case 3: // select option3

                return true;
        }
        return super.onContextItemSelected(item);
    }
}
