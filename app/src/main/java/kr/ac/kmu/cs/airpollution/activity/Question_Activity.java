package kr.ac.kmu.cs.airpollution.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import kr.ac.kmu.cs.airpollution.PagerAdapter.QuestionFragmentPagerAdapter;
import kr.ac.kmu.cs.airpollution.R;

/**
 * Created by KCS on 2016-08-06.
 */
public class Question_Activity extends FragmentActivity {
    public QuestionFragmentPagerAdapter QFPA;
    private ViewPager pager;

    public static boolean isFlag() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        Question_Activity.flag = flag;
    }

    private static boolean flag = true; // true = off false = on

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        pager = (ViewPager)findViewById(R.id.pager_question);

        QFPA = new QuestionFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(QFPA);
        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs_question);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(pager);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Question_Activity.setFlag(true);
    }
}
