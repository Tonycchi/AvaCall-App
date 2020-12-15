package com.example.rcvc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;

public class BluetoothConnectionService {

    BluetoothAdapter btAdapter;
    Context mContext;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread {
        //private final BluetoothServerSocket mmServerSocket;

        private AcceptThread() {
            BluetoothServerSocket tmp = null;
        }
    }
}
