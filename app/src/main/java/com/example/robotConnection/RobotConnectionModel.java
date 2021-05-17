package com.example.robotConnection;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public abstract class RobotConnectionModel {

    // All paired devices
    protected MutableLiveData<ArrayList<Device>> pairedDevices;

    public abstract MutableLiveData<ArrayList<Device>> getPairedDevices();
}
