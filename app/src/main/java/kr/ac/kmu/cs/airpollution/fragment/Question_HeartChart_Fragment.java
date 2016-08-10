package kr.ac.kmu.cs.airpollution.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.kmu.cs.airpollution.R;

/**
 * Created by KCS on 2016-08-08.
 */
public class Question_HeartChart_Fragment extends Fragment {
    private static Question_HeartChart_Fragment Instance = new Question_HeartChart_Fragment();
    private View view;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_question_fragment_heartchart,container,false);

        return view;
    }

    public static synchronized Question_HeartChart_Fragment getInstance() {
        return Instance;
    }
}
