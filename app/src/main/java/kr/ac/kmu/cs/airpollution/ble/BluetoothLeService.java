/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.ac.kmu.cs.airpollution.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import kr.ac.kmu.cs.airpollution.Const_rr_data;
import kr.ac.kmu.cs.airpollution.fragment.Heart_Rate_Chart_Fragment;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    public final static UUID UUID_BATTERY_SERVICE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.BATTERY_SERVICE_UUID);
    ;
    public final static UUID UUID_BATTERY_LEVEL_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.BATTERY_LEVEL_UUID);



    boolean servicediscovered = false;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    // GATT이벤트를 위해 구현된 콜백 메소드 , 발견된 서비스나 커넥션 교체에 사용됨.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }
        // 서
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                servicediscovered = true;
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };
    // 인텐트를 브로드 캐스트합니다.
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    // 브로드캐스트해줌 액션 , 특성
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        // 하트 레이터 메져 함. 브로드캐스트 업데이트.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            boolean rrEnabled=false;
            int flag = characteristic.getProperties();
            int format = -1;
            int heartRate;
            int offset=1;
            int pnnCount=0;
            int pnnPercentage=0;

            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
                heartRate = characteristic.getIntValue(format, offset);
                offset=offset+2;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
                heartRate = characteristic.getIntValue(format, offset);
                offset=offset+1;
            }

            //Two energy bytes
            if ((flag & 0x80) != 0){
                offset=offset+2;
                //Log.w(TAG, "## energy bytes present.");
            }

            if ((flag & 0x10) != 0) {
                rrEnabled=true;
                //Log.w(TAG, "One or more RR-Interval values are present. offset: "+offset+" valSize: "+valSize);
            } else {
                rrEnabled=false;
                //Log.w(TAG, "No RR-Interval values");
            }

            int rrX = 50;

            //Parse RR value
            //http://stackoverflow.com/questions/20334864/android-bluetooth-le-how-to-get-rr-interval
            //http://stackoverflow.com/questions/17422218/bluetooth-low-energy-how-to-parse-r-r-interval-value
            int [] rrValue = new int[3]; //in 1/1024 seconds
            //if(rrEnabled && (offset==(valSize-3))){
            int rr_count=0;
            if(rrEnabled){
                rr_count = ((characteristic.getValue()).length - offset) / 2;
                for (int i = 0; i < rr_count; i++){
                    rrValue[i] = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    offset += 2;

                    Const_rr_data.total_HR++;
                    //Log.w(TAG, "*** rrValue: "+rrValue+" 1024: "+(1000*rrValue)/1024+" rrX: "+rrX);

                    rrValue[i]=(rrValue[i]*1000)/1024;	//ms
                    if(Math.abs(Const_rr_data.pre_RR - rrValue[i])>rrX){
                        Const_rr_data.count_nnF++;
                        //Log.e(TAG, "*** rrValue: "+rrValue+" totalpNNx: "+bioHarnessSessionData.totalpNNx+" totalNN: "+bioHarnessSessionData.totalNN+" pNNvalue: "+pnnValue+" rrX: "+rrX+" rr_count: "+rr_count);
                        //if(beatPeriod>0){
                        //	bioHarnessSessionData.updateBeat(beatPeriod, new Integer(1));
                        //}
                    }
                    pnnCount = Const_rr_data.count_nnF;
                    pnnPercentage = (int)((100*Const_rr_data.count_nnF)/Const_rr_data.total_HR);
                    Const_rr_data.pre_RR = rrValue[i];
                }
            }

            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            //인텐트에 하트레이트 밸류값 집어 넣어서 보내줌 ㅇㅇ
            //intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            int bat = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0);
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate)+","+String.valueOf(pnnCount)+","+String.valueOf(pnnPercentage)+","+String.valueOf(bat));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public void getBattery() {

        if (mBluetoothGatt == null || !servicediscovered) {
            Log.e(TAG, "lost connection");
            return;
        }

        BluetoothGattService batteryService = mBluetoothGatt.getService(UUID_BATTERY_SERVICE_MEASUREMENT);
        if(batteryService == null) {
            Log.e(TAG, "Battery service not found!");
            return;
        }

        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(UUID_BATTERY_LEVEL_MEASUREMENT);
        if(batteryLevel == null) {
            Log.e(TAG, "Battery level not found!");
            return;
        }

        mBluetoothGatt.readCharacteristic(batteryLevel);
       // Log.w(TAG, "batteryLevel = " + mBluetoothGatt.readCharacteristic(batteryLevel));

        //int bl = batteryLevel.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0);
        //Log.w(TAG, "Battery level found: "+bl);
        //broadcastUpdate(ACTION_BATTERY_DATA_AVAILABLE, bl+"");
    }

    //바인딩.
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    // 블루투스 어뎁터 초기화.
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    // 연결
    public boolean connect(final String address) {
        //어뎁터가 널이고 맥이 넣이면 실패
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        //블루투스 디바이스 어드레스 널이 아니고 어드레스가 같고 gatt가 널이 아니면 .
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                //커넥트로 바꾼다.
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        //디바이스 정보 받아옴.
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            //디바이스가 널이면 실패.
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        //gatt 에 콜백을 등록해줌.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        // 맥 주소 스트링 넣어줌
        mBluetoothDeviceAddress = address;
        //연결 정보 넣어줌
        mConnectionState = STATE_CONNECTING;
        //성공
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        //디스 커넥트함
        servicediscovered = false;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        //닫음
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        //특성 읽어옴.
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        //초기화 실패패
       if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //특성 알림 인에이블
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        //하트 레이트 ㅇㅇ
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    //서비스 목록 받아옴
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
