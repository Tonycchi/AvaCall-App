package com.example.model.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.data.ConnectedDevice;
import com.example.data.ConnectedDeviceDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BluetoothModel extends RobotConnectionModel {

    private static final String TAG = "BluetoothModel";

    // Model for BluetoothFragment
    // Bluetooth adapter of our device
    private BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;
    //bluetooth
    private BluetoothConnectionService bluetoothConnectionService;

    private ConnectedDeviceDAO connectedDeviceDAO;

    public BluetoothModel(ConnectedDeviceDAO connectedDeviceDAO) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothConnectionService = new BluetoothConnectionService();
        this.connectedDeviceDAO = connectedDeviceDAO;
    }

    private void updatePairedDevice() {
        if (pairedDevices == null) {
            pairedDevices = new MutableLiveData<>();
        }

        // devices bonded to system
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        // will be shown to user
        ArrayList<Device> shownDevices = new ArrayList<>();

        if (bondedDevices.size() > 0) {
            // previously connected addresses from database
            List<String> dbAddresses = connectedDeviceDAO.getSortedAddresses();

            // easy access to bonded devices by address
            HashMap<String, Device> devicesByAddress = new HashMap<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                for (BluetoothDevice d : bondedDevices) {
                    devicesByAddress.put(d.getAddress(), new Device(d, d.getAlias()));
                }
            } else {
                for (BluetoothDevice d : bondedDevices) {
                    devicesByAddress.put(d.getAddress(), new Device(d, d.getName()));
                }
            }

            // addresses that are both in db and system are added in order of last connection
            // see ConnectedDevice.getSortedAddresses Query
            for (String address : dbAddresses) {
                Device d;
                if ((d = devicesByAddress.remove(address)) != null) {
                    shownDevices.add(d);
                }
            }

            // other bonded devices from system
            shownDevices.addAll(devicesByAddress.values());

        } else {
            // TODO: something
            Log.d(TAG, "No Device found!");
        }

        pairedDevices.setValue(shownDevices);
    }

    @Override
    public MutableLiveData<ArrayList<Device>> getPairedDevices() {
        Log.d(TAG, "Get paired devices");
        updatePairedDevice();
        return pairedDevices;
    }

    @Override
    public MutableLiveData<Integer> getConnectionStatus() {
        return bluetoothConnectionService.getConnectionStatus();
    }

    @Override
    public void startConnection(Device device) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice) device.getParcelable();
        ParcelUuid[] uuids = bluetoothDevice.getUuids();

        Log.d(TAG, "connect: " + bluetoothDevice.getAddress());
        connectedDeviceDAO.insertAll(new ConnectedDevice(bluetoothDevice.getAddress(), System.currentTimeMillis()));

        bluetoothConnectionService.startClient(bluetoothDevice, uuids);
    }

    public BluetoothConnectionService getService() {
        return bluetoothConnectionService;
    }

    @Override
    public void connectingCanceled() {
        bluetoothConnectionService.connectingCanceled();
    }

}
