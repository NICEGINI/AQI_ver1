package kr.ac.kmu.cs.airpollution.PagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.ac.kmu.cs.airpollution.fragment.Heart_Rate_Chart_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Question_AQI_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Question_GoogleMaps_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Question_HeartChart_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Question_Option_Fragment;

/**
 * Created by KCS on 2016-08-06.
 */
public class QuestionFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public QuestionFragmentPagerAdapter(FragmentManager mgr) {
        super(mgr);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Question_AQI_Fragment.getInstance();
            case 1:
                return Question_HeartChart_Fragment.getInstance();
            case 2:
                return Question_GoogleMaps_Fragment.getInstance();
            case 3:
                return Question_Option_Fragment.getInstance();

            default: break;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "AQI?";
            case 1:
                return "Heart Rate";
            case 2:
                return "Google Maps?";
            case 3:
                return "Chart Option";
        }
        return "";
    }
}
