package kr.ac.kmu.cs.airpollution.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;

import java.lang.ref.Reference;
import java.util.ArrayList;

import kr.ac.kmu.cs.airpollution.Buffer.realTimeBuffer;
import kr.ac.kmu.cs.airpollution.Buffer.realTimeHeartBuffer;
import kr.ac.kmu.cs.airpollution.R;

/**
 * Created by KCS on 2016-08-08.
 */
public class Heart_Rate_Chart_Fragment extends Fragment {
    private static Heart_Rate_Chart_Fragment Instance = new Heart_Rate_Chart_Fragment();
    private View view;
    private Thread Realtime_Heart_Therad;
    private boolean isRunning = true;
    private static LineChart mChart;
    private static int Heart_rate;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_fragment_heartchart,container,false);

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

//        mChart.getXAxis().setDrawGridLines(true);
//        mChart.getAxisRight().setDrawGridLines(true);
//        mChart.getAxisLeft().setDrawGridLines(true);
//        mChart.getXAxis().setDrawAxisLine(true);
//        mChart.getAxisRight().setDrawAxisLine(true);
//        mChart.getAxisLeft().setDrawAxisLine(true);

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
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(180f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        return view;
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

        d.setLineWidth(2.5f);
        d.setCircleRadius(4f);

        d.setColor(Color.RED);
        d.setVisible(true);
        d.setDrawValues(true);

        dataSets.add(d);

        LineData data = new LineData(dataSets);

        mChart.setData(data);
        mChart.setVisibleXRangeMaximum(10);
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
