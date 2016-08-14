package kr.ac.kmu.cs.airpollution.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import kr.ac.kmu.cs.airpollution.Buffer.realTimeHeartBuffer;
import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.Const_rr_data;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;

/**
 * Created by KCS on 2016-08-08.
 */
public class Heart_Rate_Chart_Fragment extends Fragment {
    private static Heart_Rate_Chart_Fragment Instance = new Heart_Rate_Chart_Fragment();
    private View view;
    private static LineChart mChart;

    private static final int OFFSET = 5;

    private TextView tv_percent_NNF;
    private TextView tv_NNF;
    private TextView tv_NN;
    private TextView tv_bat;

    private ImageView iv_bat;

    private MainActivity.sendNNCallback NNCallback = new MainActivity.sendNNCallback() {
        @Override
        public void sendIntent(int count_nn, int pnnpercent) {
            tv_bat.post(new Runnable() {
                @Override
                public void run() {
                    iv_bat.setImageResource(setbattery());
                    tv_bat.setText(String.valueOf(Const.getBattery())+"%");
                }
            });
           //
            tv_NN.setText(String.valueOf(Const_rr_data.total_HR));
            tv_NNF.setText(String.valueOf(count_nn));
            tv_percent_NNF.setText(String.valueOf(pnnpercent)+"%");
        }

        @Override
        public void sendbattery(int battery) {
            tv_bat.setText(String.valueOf(battery));
        }

        @Override
        public void setClear() {
            iv_bat.setImageResource(R.drawable.battery_empty);
            Const_rr_data.init();
            mChart.clear();
            initChart();
            realTimeHeartBuffer.removeAllheartdata();

            tv_bat.setText("");
            tv_NN.setText("N/A");
            tv_NNF.setText("N/A");
            tv_percent_NNF.setText("N/A");
        }
    };

    public int setbattery(){
        return (Const.getBattery() < 11) ? R.drawable.battery_empty :  (Const.getBattery() < 26) ? R.drawable.battery_25_percent :
                (Const.getBattery() < 51) ? R.drawable.battery_50_percent :  (Const.getBattery() < 76) ? R.drawable.battery_75_percent :
                (Const.getBattery() < 100) ? R.drawable.full_battery : R.drawable.full_battery;

    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_fragment_heartchart,container,false);

        MainActivity.registerNNCallback(NNCallback);
        tv_percent_NNF = (TextView)view.findViewById(R.id.tv_pNN_val);
        tv_NNF = (TextView)view.findViewById(R.id.tv_NNFif_val);
        tv_NN = (TextView)view.findViewById(R.id.tv_NN_val);
        tv_bat = (TextView)view.findViewById(R.id.tv_bat);

        iv_bat = (ImageView)view.findViewById(R.id.iv_bat);

        initChart();

        return view;
    }
    public void initChart(){
        mChart = (LineChart)view.findViewById(R.id.lc_heart_rate);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);


        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);
        //값을 클릭하면 그곳으로 이동합니다.

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
        leftAxis.setTextSize(15);
        leftAxis.setTextColor(Color.BLACK);
//        leftAxis.setAxisMaxValue(180f);
//        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public static void setChart(){
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        ArrayList<Entry> values = new ArrayList<Entry>();

        int val = 0;
        for (int index = 0; index < realTimeHeartBuffer.getLength(); index++) {
            val = realTimeHeartBuffer.indexof(index);
            values.add(new Entry(index, val));
        }

        LineDataSet d = new LineDataSet(values,"Heart Rate");

        d.setLineWidth(3f);
        d.setHighLightColor(Color.BLACK);
        d.setCircleRadius(4f);
        d.setCircleColor(Color.RED);
        d.setDrawCircleHole(false);

        d.setColor(Color.WHITE);
        d.setVisible(true);
        d.setDrawValues(true);

        dataSets.add(d);

        LineData data = new LineData(dataSets);
        data.setValueTextSize(10);

        // Heart_rate = setChartRange(val);

        mChart.setData(data);
        mChart.setVisibleXRangeMaximum(5);
        mChart.getAxisLeft().setAxisMaxValue(val+OFFSET);
        mChart.getAxisLeft().setAxisMinValue(val-OFFSET);

        //mChart.setVisibleYRangeMinimum(20f,YAxis.AxisDependency.RIGHT);
        //mChart.setVisibleYRangeMaximum(val_max,YAxis.AxisDependency.RIGHT);
        //mChart.setVisibleYRange(0,280,YAxis.AxisDependency.RIGHT);
        mChart.moveViewToX(data.getEntryCount());
        //mChart.moveViewTo(data.getEntryCount(), mChart.getY(),YAxis.AxisDependency.RIGHT);

        mChart.invalidate();
    }

    public static synchronized Heart_Rate_Chart_Fragment getInstance() {
        return Instance;
    }
}
