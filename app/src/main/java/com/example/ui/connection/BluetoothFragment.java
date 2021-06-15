package com.example.ui.connection;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.example.model.connection.Device;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.ModelSelectionFragment;

import java.util.ArrayList;

public class BluetoothFragment extends RobotConnectionFragment {

    private static final String TAG = "BluetoothFragment";

    // Broadcastreceiver to detect whether bluetooth was turned on or off and do code on detection
    private final BroadcastReceiver bluetoothStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // if Bluetooth is turned on or turned off
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                // state is either bluetooth turned on or off
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                // if state is bluetooth turned off
                if(state == BluetoothAdapter.STATE_OFF) {
                    // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                    showEnableBluetooth();

                    // if state equals Bluetooth turned on
                } else if(state == BluetoothAdapter.STATE_ON){
                    Log.d(TAG, "Bluetooth state_on");
                    // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                    MutableLiveData<ArrayList<Device>> pairedDevices = viewModel.getPairedDevices();

                    //if there is no device -> add placeholder into list
                    if(pairedDevices.getValue().size() == 0) {
                        setPlaceholder();
                    }
                }

            }
        }
    };

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonFirstConnection = (Button) view.findViewById(R.id.button_first_connection);
        buttonFirstConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFirstBluetoothConnection();
            }
        });

        getActivity().setTitle(R.string.title_bluetooth);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.cancelConnection();

        //bluetooth is disabled
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            showEnableBluetooth();
        }

        // Tells Broadcastreceiver to wait for certain events in this case Bluetooth Action State Changed(On or Off)
        IntentFilter bluetoothStateChange = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(bluetoothStateChangeReceiver, bluetoothStateChange);

        Log.d(TAG, "onResume");
        MutableLiveData<ArrayList<Device>> pairedDevices = viewModel.getPairedDevices();
        pairedDevices.observe(getViewLifecycleOwner(), devicesObserver);

        //if there is no device -> add placeholder into list
        if(pairedDevices.getValue().size() == 0) {
            setPlaceholder();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public void onClickDevice(Device device){
        if(device.getParcelable() != null) {
            viewModel.startConnection(device);
        }else{
            onClickFirstBluetoothConnection();
        }
    }

    @Override
    public void connectionStatusChanged(Integer newConnectionStatus){
        //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost, 4 connection is accepted = correct device, 5 connection is not accepted = wrong device
        switch(newConnectionStatus){
            case 0:
                Log.d(TAG, "Case 0: Not tested!");
                showProgressDialog();
                break;

            case 1:
                Log.d(TAG, "Case 1: Is connected!");
                changeProgressDialog();
                break;

            case 2:
                Log.d(TAG, "Case 2: Could not connect!");
                hideProgressDialog();
                ((HostActivity)getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_init_error));
                break;

            case 3:
                Log.d(TAG, "Case 3: Connection lost!");
                hideProgressDialog();
                ((HostActivity)getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_lost));
                break;

            case 4:
                Log.d(TAG, "Case 4: Device is accepted!");
                hideProgressDialog();
                viewModel.deviceAccepted();
                switchToNextFragment();
                break;

            case 5:
                Log.d(TAG, "Case 5: Device is not accepted!");
                hideProgressDialog();
                ((HostActivity)getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_wrong_device));
                break;

            default:
                Log.d(TAG, "Default: Something strange or nothing(Case -1)");
                showProgressDialog();
                break;
        }
    }


    private void showEnableBluetooth(){
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBluetoothIntent);
        Log.d(TAG,"Bluetooth is disabled!");
    }

    private void setPlaceholder(){
        ArrayList<Device> noDevicePlaceholder = new ArrayList<Device>();
        String noDevicePlaceholderText = getResources().getString(R.string.no_bluetooth_device);
        noDevicePlaceholder.add(new Device(noDevicePlaceholderText));
        Log.d(TAG, "setPlaceholder");
        viewModel.getPairedDevices().setValue(noDevicePlaceholder);
    }

    private void switchToNextFragment(){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ModelSelectionFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickFirstBluetoothConnection(){
       Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

}