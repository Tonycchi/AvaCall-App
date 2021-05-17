package com.example.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;

import com.example.AvaCallViewModel;
import com.example.robotConnection.BluetoothModel;
import com.example.robotConnection.RobotConnectionModel;

import java.util.ArrayList;
import java.util.List;

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

    public AvaCallModel(Context context) {
        this.context = context;
        robotConnectionModel = new BluetoothModel(context);
    }

    public void updatePairedDevices(){
        robotConnectionModel.updatePairedDevices();
    }
}
