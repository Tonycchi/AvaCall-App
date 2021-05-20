package com.example.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.ParcelUuid;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class AvaCallModel {

    private VideoConnectionModel videoConnectionModel;

    // Model for BluetoothFragment
    // bluetooth
    private BluetoothConnectionService bluetoothConnection;
    // Bluetooth adapter of our device
    private BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;
    // All paired devices
    private ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();

    // Model for ModelSelectionFragment
    // TODO modelle abspeichern?

    // Model for EditControlsFragment
    // TODO Liste von eigener controller klasse???????

    public AvaCallModel(SharedPreferences sharedPreferences) {
        videoConnectionModel = new VideoConnectionModel(sharedPreferences);
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
}
