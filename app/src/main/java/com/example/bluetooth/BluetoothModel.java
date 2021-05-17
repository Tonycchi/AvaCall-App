package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;

import androidx.lifecycle.MutableLiveData;

import com.example.bluetooth.BluetoothConnectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothModel {

    private Context context;

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
    private MutableLiveData<List<BluetoothDevice>> pairedDevices;

    public BluetoothModel(Context context) {
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * update the list of paired Devices
     */
    public void updatePairedDevices() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

        if (devices.size() <= 0) {
          //TODO: define behaviour if there is no device
        }

        pairedDevices.postValue(new ArrayList<>(devices));
    }

}
