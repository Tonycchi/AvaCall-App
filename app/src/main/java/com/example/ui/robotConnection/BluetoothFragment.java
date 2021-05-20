package com.example.ui.robotConnection;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AvaCallViewModel;
import com.example.rcvc.R;
import com.example.model.robotConnection.Device;
import com.example.ui.HostActivity;
import com.example.ui.ModelSelectionFragment;

import java.util.ArrayList;

public class BluetoothFragment extends RobotConnectionFragment {

    private static final String TAG = "BluetoothFragment";

    //TODO: decide what of this class can be transfered to RobotConnectionModel

    private AvaCallViewModel viewModel;

    //dialog while connecting to device
    private ProgressDialog progressDialog;

    private RecyclerView recycler;

    private PairedDevicesCustomAdapter bluetoothDeviceListAdapter;

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    // Observer to check if amount of paired Devices has been changed
    public final Observer<ArrayList<Device>> devicesObserver = new Observer<ArrayList<Device>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<Device> newDevicesList) {
            // Update the UI
            (bluetoothDeviceListAdapter).notifyDataSetChanged();
        }
    };

    public void connectionStatusChanged(Integer newConnectionStatus){
        //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost
        switch(newConnectionStatus){
            case 0:
                Log.d(TAG, "Case 0: Not tested!");
                showProgressDialog();
                break;

            case 1:
                Log.d(TAG, "Case 1: Is connected!");
                hideProgessDialog();
                switchToNextFragment();
                break;

            case 2:
                Log.d(TAG, "Case 2: Could not connect!");
                ((HostActivity)getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_init_error));
                hideProgessDialog();
                break;

            case 3:
                Log.d(TAG, "Case 3: Connection lost!");
                ((HostActivity)getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_lost));
                break;

            default:
                Log.d(TAG, "Default: Something strange or nothing(Case -1)");
                showProgressDialog();
                break;
        }
    }

    protected void showProgressDialog(){
        Log.d(TAG, "show ProgressDialog");
        //initprogress dialog
        progressDialog = ProgressDialog.show(this.getContext(), getResources().getString(R.string.connecting_bluetooth_title),
                getResources().getString(R.string.connecting_bluetooth_wait), false, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onClickBluetoothConnectionCanceled();
                    }
                });
    }

    private void onClickBluetoothConnectionCanceled(){
        viewModel.connectingCanceled();
    }

    protected void hideProgessDialog(){
        //dismiss the progressdialog when connection is established
        try {
            progressDialog.dismiss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);
        Log.d(TAG, "onCreate");
        bluetoothDeviceListAdapter = new PairedDevicesCustomAdapter(viewModel.getPairedDevices(), this);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    private void showEnableBluetooth(){
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBluetoothIntent);
        Log.d(TAG,"Bluetooth is disabled!");
    }

    public void onClickDevice(Device device){
        if(device.getParcelable() != null) {
            viewModel.startConnection(device);
        }else{
            onClickFirstBluetoothConnection();
        }
    }

    private void setPlaceholder(){
        ArrayList<Device> noDevicePlaceholder = new ArrayList<Device>();
        String noDevicePlaceholderText = getResources().getString(R.string.no_bluetooth_device);
        noDevicePlaceholder.add(new Device(noDevicePlaceholderText));
        Log.d(TAG, "setPlaceholder");
        viewModel.getPairedDevices().setValue(noDevicePlaceholder);
    }

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
                    pairedDevices.observe(getViewLifecycleOwner(), devicesObserver);
                    recycler.setAdapter(bluetoothDeviceListAdapter);

                    //if there is no device -> add placeholder into list
                    if(pairedDevices.getValue().size() == 0) {
                        setPlaceholder();
                    }
                }

            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.list_paired_devices);

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(bluetoothDeviceListAdapter);

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

    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
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
        //TODO: uncomment
        /*Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);*/

        //TODO: delete
        switchToNextFragment();
    }


}