package com.example.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;

import com.example.rcvc.R;

import java.util.ArrayList;
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
    private ArrayList<BluetoothDevice> pairedDevices;

    public BluetoothModel(Context context) {
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
    }

    /**
     * @return an ArrayList which contains all paired bluetooth devices, if there are no paired devices an error message pops up
     * and you get redirected to the bluetooth settings
     */
    public ArrayList<String> getPairedDevices() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                pairedDevices.add(device);
                names.add(device.getName());
            }
        } else {
            // error
        }
        return names;
    }

}
