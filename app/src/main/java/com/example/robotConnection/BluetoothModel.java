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
    // Bluetooth adapter of our device
    private BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;

    public BluetoothModel() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void updatePairedDevice(){
        if(pairedDevices == null) {
            pairedDevices = new MutableLiveData<ArrayList<Device>>();
        }

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        ArrayList<Device> bluetoothDevices = new ArrayList<Device>();

        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                bluetoothDevices.add(new Device(device, device.getName()));
            }
            Log.d(TAG,"Found paired devices");

        } else {
            // TODO: something
            Log.d(TAG,"No Device found!");
        }

        pairedDevices.setValue(bluetoothDevices);
    }

    @Override
    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        Log.d(TAG,"Get paired devices");
        updatePairedDevice();
        return pairedDevices;
    }

}
