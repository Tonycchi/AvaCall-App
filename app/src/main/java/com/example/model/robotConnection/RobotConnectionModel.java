package com.example.model.robotConnection;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

/**
 * Models selection and opening of connections to devices. Implementation dependent of connection
 * medium/protocol.
 */
public abstract class RobotConnectionModel {

    // All paired devices
    protected MutableLiveData<ArrayList<Device>> pairedDevices;

    public abstract MutableLiveData<ArrayList<Device>> getPairedDevices();

    public abstract MutableLiveData<Integer> getConnectionStatus();

    public abstract void startConnection(Device device);

    public abstract void deviceAccepted();

    public abstract ConnectionService getService();

    public abstract void cancelConnection();
}
