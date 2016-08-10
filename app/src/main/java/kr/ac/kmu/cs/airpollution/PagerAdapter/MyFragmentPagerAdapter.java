package kr.ac.kmu.cs.airpollution.PagerAdapter;





import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.ac.kmu.cs.airpollution.fragment.Google_Maps_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Heart_Rate_Chart_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.Realtime_Fragment;
import kr.ac.kmu.cs.airpollution.fragment.User_Setting_Fragment;

/**
 * Created by pabel on 2016-07-25.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager mgr) {
        super(mgr);
    }

    @Override
    public int getCount() {
        /** max value **/
        return(4);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return Realtime_Fragment.getInstance();

            case 1:
                return Heart_Rate_Chart_Fragment.getInstance();

            case 2:
                return Google_Maps_Fragment.getInstance();

            case 3:
                return User_Setting_Fragment.getInstance();

            default: break;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Current State";
            case 1:
                return "Heart Rate";
            case 2:
                return "Google Map";
            case 3:
                return "Setting";
        }

        return "";
    }
}
