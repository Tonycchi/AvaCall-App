package com.example.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.model.robotConnection.BluetoothModel;
import com.example.model.robotConnection.Device;
import com.example.model.robotConnection.RobotConnectionModel;

import java.util.ArrayList;

public class AvaCallModel {

    private Context context;

    private RobotConnectionModel robotConnectionModel;

    // Model for ModelSelectionFragment
    // TODO modelle abspeichern?

    // Model for EditControlsFragment
    // TODO Liste von eigener controller klasse???????

    // Model for VideoConnectionFragment
    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;

    public AvaCallModel() {
        robotConnectionModel = new BluetoothModel();
    }

    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        return robotConnectionModel.getPairedDevices();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return robotConnectionModel.getConnectionStatus();
    }

    public void startConnection(Device device) {
        robotConnectionModel.startConnection(device);
    }
}
