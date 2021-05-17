package com.example.robotConnection;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public abstract class RobotConnectionModel {

    // All paired devices
    protected MutableLiveData<ArrayList<String>> pairedDeviceNames;

    abstract public void updatePairedDeviceNames();

    public abstract MutableLiveData<ArrayList<String>> getPairedDevicesName();
}
