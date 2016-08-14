package kr.ac.kmu.cs.airpollution.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;

public class User_Setting_Fragment extends android.support.v4.app.Fragment {
    private View view = null;

    private TextView tv_Login_Email;
    private TextView tempView;
    private TextView tv_Google_Circle_Size;
    private TextView tv_Realtime_Chart_dataset;
    private TextView tv_UDOO_board_Receive_Timeset;
    private Button btn_Goto_First_Page;

    private int resultvalue = 0;

    private static User_Setting_Fragment Instance = new User_Setting_Fragment();

    //user information
    private static String user_email;

   // private User_Setting_Fragment(){super();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_fragment_user_setting,container,false);

        tv_Login_Email = (TextView)view.findViewById(R.id.tv_User_Email);
        tv_Google_Circle_Size = (TextView)view.findViewById(R.id.tv_google_Maps_Circle);
        tv_Realtime_Chart_dataset = (TextView)view.findViewById(R.id.tv_Realtime_Chart_rangeset);
        tv_UDOO_board_Receive_Timeset = (TextView)view.findViewById(R.id.tv_UDOO_board_Receive_Time_Set);

        tv_Google_Circle_Size.setText(Const.getCircleSize()+"");
        tv_Realtime_Chart_dataset.setText(Integer.toString(Const.getRealtimeDatasetRange()));
        tv_UDOO_board_Receive_Timeset.setText(Integer.toString(Const.getReceiveTimeFromUdoo()));

        btn_Goto_First_Page = (Button)view.findViewById(R.id.btn_go_to_first);
        btn_Goto_First_Page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setPager(0);
            }
        });

        tv_Google_Circle_Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlertMSG(tv_Google_Circle_Size,"The size of circle in google maps", "Input data",0);
            }
        });

        tv_Realtime_Chart_dataset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setAlertMSG(tv_Realtime_Chart_dataset,"The number of Air data in chart ", "Input data",1);
            }
        });

        setInfo();
        return view;
    }

    public void setAlertMSG(TextView Current_textView, String Title, String MSG, final int sel){
        tempView = Current_textView;
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

        alert.setTitle(Title);
        alert.setMessage(MSG);

        // Set an EditText view to get user input
        final EditText input = new EditText(alert.getContext());
        input.setInputType(0x00002002);
        input.setText(tempView.getText().toString());
        input.setGravity(Gravity.CENTER);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resultvalue = Integer.parseInt(input.getText().toString());

                switch (sel){
                    case 0:
                        Const.setCircleSize(resultvalue);
                        break;
                    case 1:
                        Const.setRealtimeDatasetRange(resultvalue);
                        break;
                }

                tempView.setText(String.valueOf(resultvalue));
                hideSoftKeyboard(input);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    view.getWindowToken(), 0);
        }
    }


    public static synchronized User_Setting_Fragment getInstance() {
        return Instance;
    }

    public static void setUserEmail(String email){
        user_email = email;
    }

    private void setInfo(){
        tv_Login_Email.setText(user_email);
    }
}
