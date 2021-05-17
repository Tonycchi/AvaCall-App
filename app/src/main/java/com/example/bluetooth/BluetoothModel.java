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
    private MutableLiveData<List<String>> pairedDevices;

    public BluetoothModel(Context context) {
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * @return an ArrayList which contains all paired bluetooth devices, if there are no paired devices an error message pops up
     * and you get redirected to the bluetooth settings
     */
    public MutableLiveData<List<String>> getPairedDevices() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {

                names.add(device.getName());
            }
        } else {
            // error
        }

        pairedDevices.postValue(names);
        return pairedDevices;
    }

}
