package com.example.model.robotConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.model.LocalDatabase;

import java.util.ArrayList;
import java.util.List;
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
    //bluetooth
    private BluetoothConnectionService bluetoothConnectionService;

    private ConnectedDeviceDAO connectedDeviceDAO;

    public BluetoothModel(ConnectedDeviceDAO connectedDeviceDAO) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothConnectionService = new BluetoothConnectionService();
        this.connectedDeviceDAO = connectedDeviceDAO;
    }


    private void updatePairedDevice(){
        if(pairedDevices == null) {
            pairedDevices = new MutableLiveData<ArrayList<Device>>();
        }

        //the devices that are stored from the system
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

        //the devices that are stored in localDatabase are the Devices that where used by this app previously
        List<ConnectedDevice> connectedDevices = connectedDeviceDAO.getSortedDevices();

        //the list that is shown to the user
        ArrayList<Device> bluetoothDevices = new ArrayList<Device>();

        if (devices.size() > 0) {
            //add the previously used devices to the list and remove those from the set
            for(ConnectedDevice connectedDevice : connectedDevices) {
                for (BluetoothDevice device : devices) {
                    if(device.getAddress().equals(connectedDevice.getAddress())){ //the address of a device in the set is equal to the connected device
                        if(devices.remove(device)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                bluetoothDevices.add(new Device(device, device.getAlias())); //alias is the local name of the device
                            } else {
                                bluetoothDevices.add(new Device(device, device.getName())); //the name of the device
                            }
                        }
                    }
                }
            }
            // add all remaining devices to the list
            for (BluetoothDevice device : devices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bluetoothDevices.add(new Device(device, device.getAlias())); //alias is the local name of the device
                }else{
                    bluetoothDevices.add(new Device(device, device.getName())); //the name of the device
                }
                Log.d(TAG,"Device name: "+device.getName()+" type: "+device.getType()+" class: "+device.getClass()+" bondstage: "+device.getBondState());
            }

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

    @Override
    public MutableLiveData<Integer> getConnectionStatus() {
        return bluetoothConnectionService.getConnectionStatus();
    }

    @Override
    public void startConnection(Device device) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice)device.getParcelable();
        ParcelUuid[] uuids = bluetoothDevice.getUuids();

        bluetoothConnectionService.startClient(bluetoothDevice, uuids);
    }

    @Override
    public void connectingCanceled() {
        bluetoothConnectionService.connectingCanceled();
    }

}
