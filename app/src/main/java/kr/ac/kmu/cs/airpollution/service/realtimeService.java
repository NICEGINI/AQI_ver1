package kr.ac.kmu.cs.airpollution.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.activity.Question_Activity;
import kr.ac.kmu.cs.airpollution.database.airDatabaseOpenHelper;
import kr.ac.kmu.cs.airpollution.fragment.Realtime_Fragment;
import kr.ac.kmu.cs.airpollution.controller.jsonController;
import kr.ac.kmu.cs.airpollution.Buffer.realTimeBuffer;


/**
 * Created by pabel on 2016-07-25.
 */
//블루투스를 통한 데이터 리시버가 필요함.
public class realtimeService extends Service {
    private boolean HR_flag=true;
    static boolean flag = true; // 서비스 스레드 동작

    private Thread service_Thread;
    private Thread loc_Thread;
    private Realtime_Fragment RF;
    private SQLiteDatabase db;
    private String DB_Name = "airdata.db";
    private int dbVersion = 1;
    private String TagSQL = "SQLite";
    private airDatabaseOpenHelper helper;
    private String temp;

    boolean isRunningRealtimeChart = false;

    public realtimeService(){
        super();
        //Toast.makeText(this,"스레드 fdsfdsfs시작됨",Toast.LENGTH_SHORT).show();
        RF = Realtime_Fragment.getInstance();
        /*helper = new airDatabaseOpenHelper(getBaseContext(),DB_Name,null,dbVersion);
        try{
            db = helper.getWritableDatabase();
        }
        catch (SQLException e){
            e.printStackTrace();
            Log.e(TagSQL,"Database can't open");
        }*/
        //Toast.makeText(getBaseContext(),"fdas",Toast.LENGTH_SHORT).show();
        flag = true; // start
        if(loc_Thread == null){
            loc_Thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (flag){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
        if(service_Thread == null){
            service_Thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(flag){
                            /*final String temp = jsonController.CreateFakeJsonFile();
                            Intent intent = new Intent();
                            intent.setAction("TEST.INTENT");
                            intent.putExtra("DATA",temp);
                            sendBroadcast(intent);
                            Log.d("Service Test","Good1");*/
                            Thread.sleep(Const.getReceiveTimeFromUdoo()*1000 - 100);

                            if((Realtime_Fragment.getInstance().getView() != null) && Question_Activity.isFlag()){
                                Realtime_Fragment.getInstance().getView().post(new Runnable() {
                                    @Override
                                    public void run() {


                                        temp = jsonController.CreateFakeJsonFile();
                                        Realtime_Fragment.getInstance().set_view(temp);
//
                                        //Log.d("Service Test","Good2");
                                        realTimeBuffer.insertData(jsonController.getAirDate(temp));
                                    }
                                });

                            }else {
                                while((Realtime_Fragment.getInstance().getView() == null) && !Question_Activity.isFlag()) Thread.sleep(200);
                                //뷰가 없거나 도움말이 떠 있을때 정지합니다.
                            }
                            //i++;
                            // Toast.makeText(getApplicationContext(),"잘돌아감",Toast.LENGTH_SHORT).show();
                            //Log.d("Service Test","Good");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            service_Thread.start();
            Log.d("Service Test","service Thread go...");
        }
        //Toast.makeText(this,"스레드 시작됨",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    static public void TurnDown(){
        flag = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

