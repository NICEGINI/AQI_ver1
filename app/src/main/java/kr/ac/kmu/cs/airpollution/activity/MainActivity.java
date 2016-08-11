package kr.ac.kmu.cs.airpollution.activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import kr.ac.kmu.cs.airpollution.Buffer.locBuffer;
import kr.ac.kmu.cs.airpollution.Buffer.realTimeHeartBuffer;
import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.PagerAdapter.MyFragmentPagerAdapter;
import kr.ac.kmu.cs.airpollution.R;
import kr.ac.kmu.cs.airpollution.ble.BluetoothLeService;
import kr.ac.kmu.cs.airpollution.ble.DeviceScanActivity;
import kr.ac.kmu.cs.airpollution.bluetooth.DeviceConnector;
import kr.ac.kmu.cs.airpollution.bluetooth.DeviceData;
import kr.ac.kmu.cs.airpollution.bluetooth.DeviceListActivity;
import kr.ac.kmu.cs.airpollution.controller.httpController;
import kr.ac.kmu.cs.airpollution.fragment.Realtime_Fragment;
import kr.ac.kmu.cs.airpollution.service.realtimeService;
import kr.ac.kmu.cs.airpollution.ble.BluetoothLeService;

public class MainActivity extends AppCompatActivity {
    public static LocationManager locationManager;
    private static final String SAVED_PENDING_REQUEST_ENABLE_BT = "PENDING_REQUEST_ENABLE_BT";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //current connection state
    private static String MSG_NOT_CONNECTED;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;
    private BluetoothAdapter btAdapter;

    // do not resend request to enable Bluetooth
    // if there is a request already in progress
    // See: https://code.google.com/p/android/issues/detail?id=24931#c1
    boolean pendingRequestEnableBt = false; // bluetooth enable boolean

    private String SensorName = "sensordata.json";
    private String jsonFile = "";
    private String temp;

    //adapters
    private static DeviceConnector connector;
    private Intent rService;
    public MyFragmentPagerAdapter MFPA;
    private Realtime_Fragment RFag;
    private BroadcastReceiver receiver; // 제이슨 데이터 받아옴

    // private final String no_bluetooth = getString(R.string.no_bt_support);

    private static Switch sw_BT, sw_BLE;
    public static boolean isPolarOn(){
        return sw_BLE.isChecked();
    }
    private Button btn_Question_Mark;

    private ImageView iv_hr;
    private static ViewPager pager;
    //====================================================================
    //ble 영역
    //private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // private TextView mConnectionState;
    //private TextView mDataField;
    private String mDeviceName = "Polar";
    private String mDeviceAddress = "";

    private BluetoothLeService mBluetoothLeService;

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private boolean ble_service_isbind = false;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("BLE", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.d("ble connect", "ble connect");
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    //리시버를 통해 받는영역 ㅇㅇ
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //데이터 받는부분임

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //  mConnected = false;
                realtimeService.setHeartConnect(false);
                sw_BLE.setChecked(false);
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                sw_BLE.setChecked(true);
                //데이터를 받는부분
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Toast.makeText(getBaseContext(), "display entered..", Toast.LENGTH_SHORT).show();
        sw_BLE.setChecked(true);
        if (gattServices != null) {
            for (int i = 0; i < gattServices.size(); i++) {
                if (gattServices.get(i).getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")) != null) {

                    final BluetoothGattCharacteristic characteristic =
                            gattServices.get(i).getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));

                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                    }
                    Log.d("test5", "ok no" + i);
                    long epoch = System.currentTimeMillis()/1000;
                    String recTime = Long.toString(epoch);
                    new httpController(MainActivity.this).reqConnect(Const.getUserEmail(),recTime,mDeviceAddress,1);
                    Log.d("ble","REQCONNECT HEART");
                }
            }
        }
    }
//    private void clearUI() {
//        mDataField.setText(R.string.no_data);
//    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
    }


//
//    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mConnectionState.setText(resourceId);
//            }
//        });
//    }

    private void displayData(String data) {
        if (data != null) {
            //Toast.makeText(getBaseContext(),data,Toast.LENGTH_SHORT).show();
            int hr = 0;
            try {
                hr = Integer.parseInt(data);
                realTimeHeartBuffer.Insert_Heart_Data(hr);
            } catch (Exception e) {
                Log.e("BLE", e.getMessage());
            }
            HRCallback.sendIntent(hr);
            NNCallback.sendIntent();
            //Log.d("Receive DATA",data);
            //mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private static IntentFilter makeGattUpdateIntentFilter() {
        Log.d("ble", "intentfilter");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    //======================================================================
    //받은 ble 정보를 realtime fragment로 전송한다.
    public interface sendHRCallback { // 인터페이스를 통해 메인 액티비티로 호출한 함수 지정
        public void sendIntent(int heartRate);
        public void setClear();
    }

    public interface sendNNCallback { // 인터페이스를 통해 메인 액티비티로 호출한 함수 지정
        public void sendIntent();
        public void setClear();
    }

    public static sendHRCallback HRCallback; // 등록할 콜백 객체

    public static void registerHRCallback(sendHRCallback HRC) {
        HRCallback = HRC; // 등록시켜주는 메소드드
    }

    public static sendNNCallback NNCallback; // 등록할 콜백 객체

    public static void registerNNCallback(sendNNCallback HRC) {
        NNCallback = HRC; // 등록시켜주는 메소드드
    }


    //=======================================================================
    //ble scan에서 함수를 호출할수있음.
    private DeviceScanActivity.bleCallback bleCallback = new DeviceScanActivity.bleCallback() {
        @Override
        public Intent sendIntent(Intent msg) {
            final Intent intent = msg;
            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            Log.d("ble callback", "ble callback send intent");
            // Sets up UI references.
            // ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

            //if(mBluetoothLeService != null) mServiceConnection = null;
            //연결 상태
            // mConnectionState = (TextView) findViewById(R.id.connection_state);
            //실시간으로 변하는 데이터 값
            //mDataField = (TextView) findViewById(R.id.data_value);
            Log.d("ble callback", "ble callback send intent");
            Toast.makeText(getBaseContext(), "setBLE...", Toast.LENGTH_SHORT).show();
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            Log.d("ble callback", "ble callback send intent");
            Intent gattServiceIntent = new Intent(getBaseContext(), BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//            displayGattServices(mBluetoothLeService.getSupportedGattServices());
            return null;
        }
    };
    //====================================================================
    //디바이스 커넥터에서 메인 액티비티의 함수를 호출할수 있게 해줍니다. 아래는 처리할 내용입니다.

    public interface bluetoothCallback { // 인터페이스를 통해 메인 액티비티 -> 커넥터로
        public void reqConnect(String msg);
    }
    private static bluetoothCallback btCallback; // 등록할 콜백 객체
    public static void registerBluetoothCallback(bluetoothCallback bcb){
        btCallback = bcb; // 등록시켜주는 메소드드
    }

    //==================================================================
    /*private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder Service) {
            kr.ac.kmu.cs.airpollution.realtimeService.realtimeServiceBinder binder = (kr.ac.kmu.cs.airpollution.realtimeService.realtimeServiceBinder) Service;
            RealTimeService = binder.getService();
            RealTimeService.registerCallback(mCallback);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private realtimeService.ICallback mCallback = new realtimeService.ICallback() {
        public void recvData() {
            // change UI
        }
    };*/
    //=====================================================================
    //블루투스 핸들러 ㅇㅇ
    boolean isStart = false;

    private final Handler bluetoothHandler = new Handler() {
        StringBuilder CSVBuilder = new StringBuilder();
        boolean csvStart = false;
        String temp;
        @Override
        public void handleMessage(Message msg) { //블루투스에서 데이터 들어오는곳
            //블루투스 핸들러 받은 데이터를 잘 처리한다.
            boolean isCSV = false;
            int i =0;
            String strData = msg.getData().getString("data");
            if(strData.contains("*") || strData.contains("+") || strData.contains("&")){
                isCSV = true;
                if(csvStart == false){
                    if(strData.contains("*")){
                        Log.d("CSV FILE","첫 파일 접속");
                        CSVBuilder.append(strData.substring(1));
                        csvStart = true;
                    }
                }else {
                    if (strData.contains("&")){
                        Log.d("CSV FILE","마지막 파일접속");
                        CSVBuilder.append(strData.substring(1));
                        temp = CSVBuilder.toString();

                        Log.d("temp",temp);
                        // 파일안에 문자열 쓰기

                        try {
                            Log.d("file add","파일 만들기 시작");
                            File file = new File(Environment.getExternalStorageDirectory() + "/Download/"+i+"ok.txt");
                            FileWriter fw = new FileWriter(file, true) ;
                            fw.write(temp);
                            fw.flush();

                            // 객체 닫기
                            fw.close();
                            Log.d("file add","파일 만듬");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Log.d("ok end add file",temp.length()+"");
                        CSVBuilder = new StringBuilder();
                        csvStart = false; //끝
                    }else {
                        Log.d("CSV FILE","파일 붙이기");
                        CSVBuilder.append(strData.substring(1));
                        //더하는 영역임
                    }
                }


                Log.d("strData","이자료는 csv임"+strData.length());
            }else if (strData.contains("control")){
                Log.d("strData","이자료는 제어 문임"+strData.length());
            }
            JSONObject parser = null;



            if(isStart && isCSV == false){
                //리얼타임데이터 받는곳
//                {"CO":0.3,"NO2":0,"O3":0,"PM":5.2,"SO2":0,"temperature":48,"timestamp":1470875712}
                try {
                    if(locBuffer.getCurrentLoc() != null){
                         JSONObject temp = new JSONObject(strData);
                        if(RFag.getView() != null){
                            RFag.set_view(temp.toString());
                        }
                        new httpController(MainActivity.this).sendRealtimeUdoo(Const.getUdooConnectId(),temp.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!isStart){
                Log.d("bt","접근됨");
                try {
                    parser = new JSONObject(strData);
                    String control = parser.getString("control");
                    String type = parser.getString("type");
                    String value = parser.getString("value");
                    if(control.equals("connect") && type.equals("response")){
                        long epoch = System.currentTimeMillis()/1000;
                        String recTime = Long.toString(epoch);
                        new httpController(MainActivity.this).reqConnect(Const.getUserEmail(),recTime,Const.getUdooMac(),0);



                    }
                    if(parser != null) Log.d("bt",control+" "+type+" "+value);
                    Log.d("bt","잘받아짐");


                        isStart = true;


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                isStart = true;
                isCSV = false;
            }
            Toast.makeText(getBaseContext(), strData, Toast.LENGTH_SHORT).show();
                /*Air_Data ar=(Air_Data)msg.getData().getSerializable("data");
            String vv=String.valueOf(ar.co)+","+String.valueOf(ar.co2)+","+String.valueOf(ar.no2)+","
                    +String.valueOf(ar.o3)+","+String.valueOf(ar.so2);
            Intent it = new Intent("Broad_data");
            it.putExtra("data",vv);
            //sendBroadcast(it);

            p_adapter.rm.Set_realtime(ar);

            p_adapter.rcm.Set_Realchart2(ar);
            try {
                p_adapter.rcm.Set_Realpie(ar);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            super.handleMessage(msg);

        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //디바이스리스트액티비티를 불러줌으로 결과값을 받아 처리한다.
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS); //
                    BluetoothDevice device = btAdapter.getRemoteDevice(address); //DeviceListActivity에서 얻어온 데이터
                    //if (super.isAdapterReady() && (connector == null)) setupConnector(device);
                    setupConnector(device);
                    sw_BT.setChecked(true); // 연결 완료
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                //super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    //Utils.log("BT not enabled");

                }
                break;
        }
    }

    private void setupConnector(BluetoothDevice connectedDevice) {
        //stopConnection();
        //블루투스와 연결하는 메소드
        // 디바이스커넥터 클래스인 커넥터에 디바이스데이터와 핸들러를 넣어줌으로 처리함

        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, bluetoothHandler);
            //connector.registerBluetoothCallback(mCallback);
            connector.connect();
        } catch (IllegalArgumentException e) {
            //Utils.log("setupConnector failed: " + e.getMessage());
        }
    }

    class myLocListener implements LocationListener {
        locBuffer buffer = locBuffer.getBuffer();
        @Override
        public void onLocationChanged(Location location) {
            buffer.setCurrentLoction(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLocListener myLocListener = new myLocListener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, myLocListener);

        // 3G,4G,WIFI 사용시
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, myLocListener);
        RFag = Realtime_Fragment.getInstance();
        pager = (ViewPager)findViewById(R.id.pager);
        MFPA = new MyFragmentPagerAdapter(getSupportFragmentManager());

        sw_BT = (Switch)this.findViewById(R.id.sw_BlueTooth);
        sw_BLE = (Switch)this.findViewById(R.id.sw_BLE);
        btn_Question_Mark = (Button)this.findViewById(R.id.btn_Question_Mark);
        iv_hr = (ImageView)this.findViewById(R.id.iv_hr);
        DeviceScanActivity.registerBluetoothCallback(bleCallback);
        sw_BLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sw_BLE.isChecked()){ //연결
                    sw_BLE.setChecked(false);
                    Intent intent = new Intent(getApplicationContext(),DeviceScanActivity.class);
                    startActivity(intent);

                }else { // 연결 ㄴㄴ
                    sw_BLE.setChecked(true);

                    unbindService(mServiceConnection);
                    HRCallback.setClear();
                    NNCallback.setClear();
                    Toast.makeText(getBaseContext(),"disconnect Polar..",Toast.LENGTH_SHORT).show();
                    realtimeService.setHeartConnect(false);
                    sw_BLE.setChecked(false);

                }
            }
        });
//        sw_BLE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                //DeviceScanActivity test = DeviceScanActivity.getInstance();
//                //누른 후라서 bool값 반대로 적용됨
//
//
//
//
////
//
//            }
//        });
        sw_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!btAdapter.isEnabled()){
                    //연결 상태아님
                    RequestBlueTooth();
                    realtimeService.setHeartConnect(false);
                    sw_BT.setChecked(false);
                }
                else {
                    //연결 상태임
                     if(sw_BT.isChecked() != false){
                         sw_BT.setChecked(false);
                         btAdapter = BluetoothAdapter.getDefaultAdapter();
//                         sw_BT.setChecked(true);
                         //디바이스리스트액티비티 클래스를 스타트 액티비티 포 리저트 형식으로 열어줌으로 결과를 받음
                         Intent serverIntent = new Intent(getBaseContext(), DeviceListActivity.class);
                         //Fragment fr=;
                         startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE );
                   }
                    else {
                         btAdapter = null;
                         sw_BT.setChecked(false);
                     }
                }
            }
        });

        //Question_Activity mark implement
        btn_Question_Mark.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(getBaseContext(), Question_Activity.class);
                  Question_Activity.setFlag(false);
                  startActivity(intent);
              }
        });

        pager.setAdapter(MFPA);
        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(pager);

        if (savedInstanceState != null) {
            //state번들이 널이아닐때 번들안에서 불린값을 불러옴
            pendingRequestEnableBt = savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }
        //블투어뎁터에 디폴트 어뎁터를 붙여줌
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            //블루어뎁터가 널이면 블투 지원안함
            final String no_bluetooth = getString(R.string.no_bt_support);
            showAlertDialog(no_bluetooth);
        }
        Log.d("test2","진입");
        rService = new Intent(this,realtimeService.class);
        startService(rService); // 서비스시작

        Toast.makeText(getBaseContext(),"서비스 시작",Toast.LENGTH_SHORT).show();

        //Toast.makeText(getApplicationContext(),"서비스 시작",Toast.LENGTH_SHORT).show();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Service Test","Received Intend");
                temp = intent.getStringExtra("DATA");
                //Toast.makeText(getBaseContext(),"tlqkf??",Toast.LENGTH_SHORT).show();
                //RFag.set_view(temp);
                if(RFag.getView() != null) {
                    Log.d("Service Test","exist Instance");
                    RFag.set_view(temp);
                    //여기서 부터 해야됨 프래그먼트 객체 존재함.
                    /*RFag.getView().post(new Runnable() {
                        @Override
                        public void run() {
                            RFag.set_view(temp);
                        }
                    });*/
                }
            }
        };
        IntentFilter filter = new IntentFilter(); // 필터 객체 생성
        filter.addAction("TEST.INTENT"); // 필터 등록
        registerReceiver(receiver,filter); // 리시버에 적용
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    public void RequestBlueTooth(){
        if (btAdapter == null) return ;
        // 블루투스 리퀘스트 보내는 부분
        if (!btAdapter.isEnabled()  ) {//&& !pendingRequestEnableBt
            pendingRequestEnableBt = true;
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        else {
            //연결 상태임
        }
    }

    void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder.setMessage(message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    boolean isAdapterReady() {
        return (btAdapter != null) && (btAdapter.isEnabled());
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //객체정보를 저장함
        //매개변수 번들에 불린값을 넣어줌 SAVED_PENDING_REQUEST_ENABLE_BT라는 스트링 부분에 불린값 넣음음
        outState.putBoolean(SAVED_PENDING_REQUEST_ENABLE_BT, pendingRequestEnableBt);
    }

    @Override
    protected void onDestroy() {
        kr.ac.kmu.cs.airpollution.service.realtimeService.TurnDown(); // 플래그를 통해 서비스의 스레드 종료해줌
        unregisterReceiver(receiver); // 등록했던 리시버 해제해줌
        stopService(rService); // 리얼타임 서비스 종료해줌.
        if(ble_service_isbind) { unbindService(mServiceConnection); ble_service_isbind = false;}

        super.onDestroy();

    }



    public static void setPager(int index){
        pager.setCurrentItem(index);
    }

public void showMsgDialog(String msg){
    android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();     //닫기
        }
    });
    alert.setMessage(msg);
    alert.show();
}
}

