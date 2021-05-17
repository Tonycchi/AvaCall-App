package com.example.model.robotConnection;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public abstract class RobotConnectionModel {

    // All paired devices
    protected MutableLiveData<ArrayList<Device>> pairedDevices;

    public abstract MutableLiveData<ArrayList<Device>> getPairedDevices();

    public abstract MutableLiveData<Integer> getConnectionStatus();

    public abstract void startConnection(Device device);
}
