package com.example.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.model.robotConnection.BluetoothModel;
import com.example.model.robotConnection.Device;
import com.example.model.robotConnection.RobotConnectionModel;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class AvaCallModel {

    private Context context;

    private RobotConnectionModel robotConnectionModel;

    private VideoConnectionModel videoConnectionModel;

    // Model for ModelSelectionFragment
    // TODO modelle abspeichern?

    // Model for EditControlsFragment
    // TODO Liste von eigener controller klasse???????

    // Model for VideoConnectionFragment
    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;

    public AvaCallModel() {
        videoConnectionModel = new VideoConnectionModel();
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

    public VideoConnectionModel getVideoConnectionModel() {
        return this.videoConnectionModel;
    }

    public void invitePartner() {
        videoConnectionModel.invitePartner();
    }

    public MutableLiveData<String> getInviteLink() {
        return videoConnectionModel.getInviteLink();
    }

    public SessionData getSession() {
        return videoConnectionModel.getSession();
    }

    public void connectingCanceled() {
        robotConnectionModel.connectingCanceled();
    }
}
