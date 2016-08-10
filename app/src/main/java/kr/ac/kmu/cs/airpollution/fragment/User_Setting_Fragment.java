package kr.ac.kmu.cs.airpollution.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.activity.MainActivity;

public class User_Setting_Fragment extends android.support.v4.app.Fragment {
    private View view = null;

    private TextView tv_Login_Email;
    private EditText et_Google_Circle_Size;
    private Button btn_Goto_First_Page;

    private static User_Setting_Fragment Instance = new User_Setting_Fragment();

    //user information
    private static String user_email;

   // private User_Setting_Fragment(){super();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pager_fragment_user_setting,container,false);

        tv_Login_Email = (TextView)view.findViewById(R.id.tv_User_Email);
        et_Google_Circle_Size = (EditText)view.findViewById(R.id.et_Google_Circle_Size);
        et_Google_Circle_Size.setText(Const.getCircleSize()+"");
        et_Google_Circle_Size.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(getContext(),et_Google_Circle_Size.getText().toString(),Toast.LENGTH_SHORT).show();
                Const.setCircleSize(Integer.parseInt(et_Google_Circle_Size.getText().toString()));
                hideSoftKeyboard(et_Google_Circle_Size);
                return false;
            }
        });
        btn_Goto_First_Page = (Button)view.findViewById(R.id.btn_go_to_first);
        btn_Goto_First_Page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setPager(0);
            }
        });

        et_Google_Circle_Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                alert.setTitle("Google Circle Size");
                alert.setMessage("Please, input number");

                // Set an EditText view to get user input
                final EditText input = new EditText(alert.getContext());
                input.setText(et_Google_Circle_Size.getText().toString());
                input.setGravity(Gravity.CENTER);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        et_Google_Circle_Size.setText(input.getText().toString());
                        Const.setCircleSize(Integer.parseInt(et_Google_Circle_Size.getText().toString()));
                        hideSoftKeyboard(input);
                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
            }
        });
        setInfo();
        return view;
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
