package com.example.rcvc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionService {

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "Platzhalter";
    private static final UUID MY_UUID = UUID.fromString("e77cea6d-e98b-4a13-b01f-e9c22f4caffe");

    BluetoothAdapter btAdapter;
    Context mContext;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        private AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(appName, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG,"Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            //Keep listening until exception occurs or a socket is returned.
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
            }

            if (socket != null) {
                //manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
