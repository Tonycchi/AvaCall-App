package com.example.model.robotConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.Annotation;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.Constants;
import com.example.data.ConnectedDevice;
import com.example.data.ConnectedDeviceDAO;
import com.example.model.MainModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * {@code BluetoothModel} is the MVVM-model for selecting and starting a bluetooth connection.
 */
public class BluetoothModel extends RobotConnectionModel {

    private static final String TAG = "BluetoothModel";

    // adds dummy device
    private static final boolean ADD_DUMMY = !Constants.USER_RELEASE;

    // Model for BluetoothFragment
    // Bluetooth adapter of our device
    private final BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice bluetoothDevice;
    //bluetooth
    private final BluetoothConnectionService bluetoothConnectionService;

    private final ConnectedDeviceDAO connectedDeviceDAO;

    private ConnectionService testService;

    public BluetoothModel(ConnectedDeviceDAO connectedDeviceDAO, Handshake<byte[]> byteArrayHandshake, MainModel mainModel) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothConnectionService = new BluetoothConnectionService(byteArrayHandshake, mainModel);
        this.connectedDeviceDAO = connectedDeviceDAO;
    }

    /**
     * updates list of paired bluetooth devices <br>
     * previously used (in app) devices first, most recently on top <br>
     * previously unused devices follow
     */
    private void updatePairedDevice() {
        if (pairedDevices == null) {
            pairedDevices = new MutableLiveData<>();
        }

        // bonded to system
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

            // add dummy device that does nothing (skip bluetooth connection)
            if (ADD_DUMMY)
                shownDevices.add(new Device(new Annotation("test", "test"), "SKIP BLUETOOTH"));

        } else {
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
        // DUMMY was picked
        if (device.getParcelable() instanceof Annotation) {
            testService = bytes -> {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X ", b));
                }
                Log.d("INPUTS", sb.toString());
            };
            return;
        }

        bluetoothDevice = (BluetoothDevice) device.getParcelable();
        ParcelUuid[] uuids = bluetoothDevice.getUuids();

        Log.d(TAG, "connect: " + bluetoothDevice.getAddress());

        bluetoothConnectionService.startClient(bluetoothDevice, uuids);
    }

    public ConnectionService getService() {
        // DUMMY was picked
        if (testService != null) {
            return testService;
        }

        return bluetoothConnectionService;
    }

    @Override
    public void deviceAccepted() {
        connectedDeviceDAO.insertAll(new ConnectedDevice(bluetoothDevice.getAddress(), System.currentTimeMillis()));
    }

    @Override
    public void cancelConnection() {
        bluetoothConnectionService.cancel();
    }
}
