/*
 * Copyright (C) 2009 The Android Open Source Project
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

package kr.ac.kmu.cs.airpollution.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.kmu.cs.airpollution.Const;
import kr.ac.kmu.cs.airpollution.controller.httpController;


public class DeviceConnector {
    private static final String TAG = "DeviceConnector";
    private static final boolean D = false;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device

    private int mState;

    private final BluetoothAdapter btAdapter;
    private final BluetoothDevice connectedDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private final String deviceName;

    /*
    이 영역은 블루투스 영역에서 메인 액티비티의 메소드 호출을 위한 영역임
     */
    //==========================================================
    public interface bluetoothCallback { // 인터페이스를 통해 메인 액티비티로 호출한 함수 지정
        public void reqConnect(String msg);
    }
    private bluetoothCallback btCallback; // 등록할 콜백 객체
    public void registerBluetoothCallback(bluetoothCallback bcb){
        btCallback = bcb; // 등록시켜주는 메소드드
    }
    //======================================================================

    // ==========================================================================


    public DeviceConnector(DeviceData deviceData, Handler handler) {
        mHandler = handler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedDevice = btAdapter.getRemoteDevice(deviceData.getAddress());
        deviceName = (deviceData.getName() == null) ? deviceData.getAddress() : deviceData.getName();
        mState = STATE_NONE;
    }
    // ==========================================================================


    /**
     * Запрос на соединение с устойством
     */
    public synchronized void connect() {
        if (D) Log.d(TAG, "connect to: " + connectedDevice);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                if (D) Log.d(TAG, "cancel mConnectThread");
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            if (D) Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(connectedDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    // ==========================================================================

    /**
     * Завершение соединения
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null) {
            if (D) Log.d(TAG, "cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            if (D) Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }
    // ==========================================================================


    /**
     * Установка внутреннего состояния устройства
     *
     * @param state - состояние
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        //안씀 mHandler.obtainMessage(DeviceControlActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    // ==========================================================================


    /**
     * Получение состояния устройства
     */
    public synchronized int getState() {
        return mState;
    }
    // ==========================================================================


    public synchronized void connected(BluetoothSocket socket) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            if (D) Log.d(TAG, "cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            if (D) Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_CONNECTED);

        // Send the name of the connected device back to the UI Activity
        //Message msg = mHandler.obtainMessage(DeviceControlActivity.MESSAGE_DEVICE_NAME, deviceName);
       // mHandler.sendMessage(msg);

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }
    // ==========================================================================


    public void write(byte[] data) {
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        // Perform the write unsynchronized
        if (data.length == 1) r.write(data[0]);
        else r.writeData(data);
    }
    // ==========================================================================


    private void connectionFailed() {
        if (D) Log.d(TAG, "connectionFailed");

        // Send a failure message back to the Activity
       // Message msg = mHandler.obtainMessage(DeviceControlActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        //msg.setData(bundle);
       // mHandler.sendMessage(msg);
        setState(STATE_NONE);
    }
    // ==========================================================================


    private void connectionLost() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(DeviceControlActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        setState(STATE_NONE);
    }
    // ==========================================================================


    /**
     * Класс потока для соединения с BT-устройством
     */
    // ==========================================================================
    private class ConnectThread extends Thread {
        private static final String TAG = "ConnectThread";
        private static final boolean D = false;

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            if (D) Log.d(TAG, "create ConnectThread");
            mmDevice = device;
            mmSocket = BluetoothUtils.createRfcommSocket(mmDevice);
        }
        // ==========================================================================

        /**
         * Основной рабочий метод для соединения с устройством.
         * При успешном соединении передаёт управление другому потоку
         */
        public void run() {
            if (D) Log.d(TAG, "ConnectThread run");
            btAdapter.cancelDiscovery();
            if (mmSocket == null) {
                if (D) Log.d(TAG, "unable to connect to device, socket isn't created");
                connectionFailed();
                return;
            }

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    if (D) Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (DeviceConnector.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }
        // ==========================================================================


        /**
         * Отмена соединения
         */
        public void cancel() {
            if (D) Log.d(TAG, "ConnectThread cancel");

            if (mmSocket == null) {
                if (D) Log.d(TAG, "unable to close null socket");
                return;
            }
            try {
                mmSocket.close();
            } catch (IOException e) {
                if (D) Log.e(TAG, "close() of connect socket failed", e);
            }
        }
        // ==========================================================================
    }
    // ==========================================================================


    /**
     * Класс потока для обмена данными с BT-устройством
     */
    //conneted 쓰레드를 통해 데이터를 주고 받는다.
    // ==========================================================================
    private class ConnectedThread extends Thread {
        private static final String TAG = "ConnectedThread";
        private static final boolean D = false;

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            if (D) Log.d(TAG, "create ConnectedThread");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                if (D) Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.d(TAG,"SOCKET YES");
            long epoch = System.currentTimeMillis()/1000;
            String recTime = Long.toString(epoch);
            writeData(sendControlJSON("connect","request",recTime).getBytes());
            Log.d(TAG,"yes");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Const.setUdooMac(connectedDevice.getAddress());
         Log.d(TAG,"send start");
//            temp = "start";
//            writeData(temp.getBytes());
//            Log.d(TAG,"good");

        }
        // ==========================================================================

        /**
         * Основной рабочий метод - ждёт входящих команд от потока
         */
        //이부분에서 처리
        public void run() {
            boolean isStart = false;
            if (D) Log.i(TAG, "ConnectedThread run");
            byte[] buffer = new byte[512]; // 버퍼 단위로 읽음
            int bytes; // 읽은 데이터를 넣음
            StringBuilder readMessage = new StringBuilder(); //스트링 빌더를 통해 연결해줌
            boolean connect = false;
            while (true) {
                try {
                    // считываю входящие данные из потока и собираю в строку ответа
                    bytes = mmInStream.read(buffer);
                    String readed = new String(buffer, 0, bytes);
                    Log.d("bt","읽음");
                    JSONObject parser = null;
                    if(Const.getUdooConnectId() != "" && !isStart){
                        writeData(sendControlJSON("start","request","").getBytes());
                    }
                    //if(!connect){ // 아직 연결안됨.
                     //   readControlJSON(readed); // 제이슨을 읽어서 맞는 컨트롤을 함.

                    //}
                    Log.d("DeviceConnector",readed);
                    //Intent intent = new Intent("bluetooth");
                    //intent.putExtra("data",readed);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("data",readed);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    //블루투스 핸들러로 보내줌.
                   readMessage.append(readed);

                    // маркер конца команды - вернуть ответ в главный поток
                    if (readed.contains("\n")) {
                       // mHandler.obtainMessage(DeviceControlActivity.MESSAGE_READ, bytes, -1, readMessage.toString()).sendToTarget();
                        //readMessage.setLength(0);
                    }

                } catch (IOException e) {
                    if (D) Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
        // ==========================================================================
        public String sendControlJSON(String Control,String Type,String Value){
            JSONObject temp = new JSONObject();
            try {
                temp.put("control",Control);
                temp.put("type",Type);
                temp.put("value",Value);
//
//            String JSON = "{\n" +
//                    "    \"control\":"+Control+",\n" +
//                    "    \"type\":"+Type+"\n" +
//                    "    \"value\":"+Value+"\n" +
//                    "}";
                return temp.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
           return "";
        }
        public void readControlJSON(String JSON) {
            //컨트롤 제이슨을 받았을때 처리해야 될 목록.
            JSONObject jsonObject = null;
            String control, value, type;
            try {
                jsonObject = new JSONObject(JSON);
                control = jsonObject.getString("control");
                type = jsonObject.getString("type");
                value = jsonObject.getString("value");

                switch (control) { // control의 값을 보고 맞는 처리를 합니다.
                    case "connect": // 커넥트 메세지
                        break;
                    case "start": // 스타트 메세지
                        break;
                    case "stop": // 스탑 메세지
                        break;
                    default: // 아무값도 없을때
                        break;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        /**
         * Записать кусок данных в устройство
         */
        public void writeData(byte[] chunk) {

            try {
                mmOutStream.write(chunk);
                mmOutStream.flush();
                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(DeviceControlActivity.MESSAGE_WRITE, -1, -1, chunk).sendToTarget();
            } catch (IOException e) {
                if (D) Log.e(TAG, "Exception during write", e);
            }
        }
        // ==========================================================================


        /**
         * Записать байт
         */
        public void write(byte command) {
            byte[] buffer = new byte[1];
            buffer[0] = command;

            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(DeviceControlActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                if (D) Log.e(TAG, "Exception during write", e);
            }
        }
        // ==========================================================================


        /**
         * Отмена - закрытие сокета
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                if (D) Log.e(TAG, "close() of connect socket failed", e);
            }
        }
        // ==========================================================================
    }





    // ==========================================================================

    public String parseJSON(String JSON){

        return "";
    }
}
