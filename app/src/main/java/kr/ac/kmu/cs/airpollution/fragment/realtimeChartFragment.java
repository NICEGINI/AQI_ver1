package kr.ac.kmu.cs.airpollution.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.Realtime_Chart_Activity;
import kr.ac.kmu.cs.airpollution.controller.jsonController;
import kr.ac.kmu.cs.airpollution.Buffer.realTimeBuffer;

public class realtimeChartFragment extends Fragment implements OnChartValueSelectedListener {
    private static String TAG = "realtimeChartFragment";
    private final String[] airdata = {"CO","SO2","NO2","PM2.5","O3"};
    private final String[] airDataJSON = {"co","so2","no2" ,"pm2.5","o3"};
    private Thread realTimeThread;
    private flagSet FS = new flagSet();
    public interface ICallback { // 콜백 인터페이스
        public void changePosition(LatLng position);
    }
    private static ICallback mCallback;
    public static void registerCallback(ICallback cb){
        mCallback = cb;
    }
    public realtimeChartFragment(){super();}

    // Check box id
    private CheckBox cb_co;
    private CheckBox cb_no2;
    private CheckBox cb_so2;
    private CheckBox cb_pm;
    private CheckBox cb_o3;

    // Linechart Id
    private LineChart mChart;

    // get Fragment_realtime_chart
    private View view = null;

    private boolean[] flag = {false,false,false,false,false};
    public boolean co,no2,so2,pm,o3;
    private boolean isRunning = true;
    public static String strBase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_realtime_chart,container,false);

        // setting check_box event listnener
        cb_co = (CheckBox)view.findViewById(R.id.cb_co);
        cb_co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_co.isChecked()){
                    FS.flag_co = true;
                   // Log.d(TAG,"TRUE");

                }else {
                    FS.flag_co = false;
                   // Log.d(TAG,"FALSE");
                }
               // Log.d(TAG,"CO");
            }
        });

        cb_so2 = (CheckBox)view.findViewById(R.id.cb_so2);
        cb_so2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb_so2.isChecked()){
                    FS.flag_so2 = true;
                    //Log.d(TAG,"TRUE");

                }else {
                    FS.flag_so2 = false;
                    //Log.d(TAG,"FALSE");
                }
                //Log.d(TAG,cb_so2.isChecked()+"");
            }
        });
        cb_no2 = (CheckBox)view.findViewById(R.id.cb_no2);
        cb_no2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG,cb_no2.isChecked()+"");
                if(cb_no2.isChecked()){
                    FS.flag_no2 = true;
                    //Log.d(TAG,"TRUE");

                }else {
                    FS.flag_no2 = false;
                    //Log.d(TAG,"FALSE");
                }
            }
        });
        cb_pm = (CheckBox)view.findViewById(R.id.cb_pm);
        cb_pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG,"PM");
                if(cb_pm.isChecked()){
                    FS.flag_pm = true;
                    //Log.d(TAG,"TRUE");

                }else {
                    FS.flag_pm = false;
                   // Log.d(TAG,"FALSE");
                }
            }
        });
        cb_o3 = (CheckBox)view.findViewById(R.id.cb_o3);
        cb_o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.d(TAG,"O3");
                if(cb_o3.isChecked()){
                    FS.flag_o3 = true;
                    //Log.d(TAG,"TRUE");

                }else {
                    FS.flag_o3 = false;
                    //Log.d(TAG,"FALSE");
                }
            }
        });

        mChart = (LineChart)view.findViewById(R.id.realtimeChart);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);
        //값을 클릭하면 그곳으로 이동합니다.
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
               int index = Math.round(e.getX());
               LatLng test = jsonController.getLatlng(realTimeBuffer.indexOf(index));
                if(test == null){
                    Toast.makeText(getActivity(),"test is null",Toast.LENGTH_LONG).show();
                }
                else{
                    mCallback.changePosition(test); // 콜백을 이용해 지도이동
                    Toast.makeText(getActivity(),test.toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        mChart.getLegend().setWordWrapEnabled(true);

        // modify the legend ...
        l.setFormSize(10f); // set the size of the
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        mChart.getLegend().setYOffset(20);
       // mChart.setExtraBottomOffset(10);
        l.setForm(Legend.LegendForm.LINE);
       // l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
//        xl.setTextColor(Color.WHITE);
//        xl.setDrawGridLines(false);
//        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setDrawLabels(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(500f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        if(Realtime_Chart_Activity.getBaseSeleted()!= null) {
            setCheckBox(Realtime_Chart_Activity.getBaseSeleted());
            //Log.d(TAG,"init View");
        }
        //

        setChart();
        mChart.setVisibleXRangeMaximum(30);
        realTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentLength = realTimeBuffer.getLength();

                while (isRunning){
                    if(currentLength < realTimeBuffer.getLength()){
                        currentLength = realTimeBuffer.getLength();

                        // co , so2, no2, pm, o3
                        mChart.post(new Runnable() {
                            @Override
                            public void run() {
                                setChart();
                                if(FS.flag_co) {
                                    cb_co.setChecked(true);

                                }
                                else cb_co.setChecked(false);

                                if(FS.flag_no2) {
                                    cb_no2.setChecked(true);
                                }
                                else cb_no2.setChecked(false);

                                if(FS.flag_so2) {
                                    cb_so2.setChecked(true);

                                }
                                else cb_so2.setChecked(false);

                                if(FS.flag_o3) {
                                    cb_o3.setChecked(true);

                                }
                                else cb_o3.setChecked(false);

                                if(FS.flag_pm) {
                                    cb_pm.setChecked(true);

                                }
                                else cb_pm.setChecked(false);
                            }
                        });

                        Log.e(TAG,"chart Thread is running");
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        realTimeThread.start();
        return view;
    }

    @Override
    public void onDestroyView() {
        stopThread();
        super.onDestroyView();
    }

    public void stopThread(){
        isRunning = false;

    }

    //indicate what air is selected
    public static void setStrBase(String string){
        strBase = string;
    }


    public void setChart(){
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (int setNum = 0; setNum < 5; setNum++) {

            ArrayList<Entry> values = new ArrayList<Entry>();

            for (int i = 0; i < realTimeBuffer.getAirDataBuffer().size(); i++) {
                double temp_num;
                int val = 0;
                try {
                    String temp = realTimeBuffer.getAirDataBuffer().get(i).getString(airDataJSON[setNum]);
                    temp_num = Double.parseDouble(temp);
                    val = (int)Math.round(temp_num);
                    //val = Integer.parseInt(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                values.add(new Entry(i, val));
            }

            LineDataSet d = new LineDataSet(values, airdata[setNum]);

            d.setLineWidth(2.5f);
            d.setCircleRadius(4f);

            d.setColor(Color.parseColor(mColors[setNum]));
            d.setVisible(isChecked(setNum));
            d.setDrawValues(isChecked(setNum));

            dataSets.add(d);
        }

        //((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
//        ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
//        ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(dataSets);

        mChart.setData(data);
        mChart.setVisibleXRangeMaximum(15);
        mChart.moveViewToX(data.getEntryCount());
        mChart.invalidate();
    }

    // store color
    private final String[] mColors = new String[] {
            "#ff000f", //red - CO
            "#ff9c00", //orange - SO2
            "#00e620", // green - NO2
            "#004ce6", //blue - PM2.5
            "#00ffe4" // cyan - O3
    };

    //check selected air box
    public void setCheckBox(String str){
        switch (str){
            case  "CO" :
                cb_co.setChecked(true);
                FS.flag_co = true;
                cb_so2.setChecked(false);
                cb_no2.setChecked(false);
                cb_pm.setChecked(false);
                cb_o3.setChecked(false);
                Log.e(TAG,"setCheckBox");
                break;
            case  "SO2" :
                cb_co.setChecked(false);
                cb_so2.setChecked(true);
                FS.flag_so2 = true;
                cb_no2.setChecked(false);
                cb_pm.setChecked(false);
                cb_o3.setChecked(false);
                Log.e(TAG,"setCheckBox");
                break;
            case  "NO2" :
                cb_co.setChecked(false);
                cb_so2.setChecked(false);
                cb_no2.setChecked(true);
                FS.flag_no2 = true;
                cb_pm.setChecked(false);
                cb_o3.setChecked(false);
                Log.e(TAG,"setCheckBox");
                break;
            case  "PM2.5" :
                cb_co.setChecked(false);
                cb_so2.setChecked(false);
                cb_no2.setChecked(false);
                cb_pm.setChecked(true);
                FS.flag_pm = true;
                cb_o3.setChecked(false);
                Log.e(TAG,"setCheckBox");
                break;
            case  "O3" :
                cb_co.setChecked(false);
                cb_so2.setChecked(false);
                cb_no2.setChecked(false);
                cb_pm.setChecked(false);
                cb_o3.setChecked(true);
                FS.flag_o3 = true;
                Log.e(TAG,"setCheckBox");
                break;
        }
    }

    // checked?
    public boolean isChecked(int val){
        switch (val){
            case 0:
                if(cb_co.isChecked())
                    return true;
                else return false;

            case 1:
                if(cb_so2.isChecked())
                    return true;
                else return false;
            case 2:

            if(cb_no2.isChecked())
                return true;
            else return false;
            case 3:

            if(cb_pm.isChecked())
                return true;
            else return false;
            case 4:

            if(cb_o3.isChecked())
                return true;
            else return false;

        }
        return true;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    public static class flagSet {
        private static boolean flag_co = false;
        private static boolean flag_no2= false;
        private static boolean flag_so2= false;
        private static boolean flag_pm= false;
        private static boolean flag_o3= false;

        public static void setFalse(){
            flag_co = false;
            flag_no2= false;
            flag_so2= false;
            flag_pm= false;
            flag_o3= false;

        }
        public static void setFlag(boolean co,boolean no2,boolean so2,boolean pm,boolean o3){
            flag_co = co;
            flag_no2 = no2;
            flag_so2 = so2;
            flag_pm = pm;
            flag_o3 = o3;

        }
    }
}

// seting flag

