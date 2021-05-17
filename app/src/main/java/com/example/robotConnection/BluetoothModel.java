package com.example.robotConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.rcvc.R;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothModel extends RobotConnectionModel{

    private static final String TAG = "BluetoothModel";

    // Model for BluetoothFragment
    // bluetooth
    private BluetoothConnectionService bluetoothConnection;
    // Bluetooth adapter of our device
    private BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;

    public BluetoothModel() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void updatePairedDeviceNames(){
        if(pairedDeviceNames == null) {
            pairedDeviceNames = new MutableLiveData<ArrayList<String>>();
        }
        Log.d(TAG,"Update paired devices");

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> bluetoothNames = new ArrayList<String>();

        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                bluetoothNames.add(device.getName());
            }
            Log.d(TAG,"Found paired devices");

        } else {
            // TODO: hard coded String leider ka wie man das aus Resources holt
            bluetoothNames.add("Kein Gerät verbunden. Aktivieren Sie Bluetooth oder benutzen Sie \"Mit Roboter koppeln\"!");
            Log.d(TAG,"No Device found!");
        }

        pairedDeviceNames.setValue(bluetoothNames);
    }

    @Override
    public MutableLiveData<ArrayList<String>> getPairedDevicesName() {
        Log.d(TAG,"Get paired devices");
        updatePairedDeviceNames();
        return pairedDeviceNames;
    }

}
