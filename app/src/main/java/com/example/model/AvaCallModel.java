package com.example.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.ArrayList;

public class AvaCallModel {

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

    // Model for VideoConnectionFragment
    private URLFactory urlFactory;
    private WebClient wc;
    private SessionData session;
}
