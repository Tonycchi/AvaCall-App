package com.example.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AvaCallViewModel;
import com.example.rcvc.R;
import com.example.robotConnection.PairedDevicesCustomAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BluetoothFragment extends RobotConnectionFragment {

    private static final String TAG = "BluetoothFragment";

    //TODO: decide what of this class can be transfered to RobotConnectionModel

    private AvaCallViewModel viewModel;

    private RecyclerView recycler;

    private PairedDevicesCustomAdapter bluetoothDeviceListAdapter;

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    // Observer to check if amount of paired Devices has been changed
    public final Observer<ArrayList<String>> devicesObserver = new Observer<ArrayList<String>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<String> newDevicesNameList) {
            // Update the UI
            (bluetoothDeviceListAdapter).notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);
        bluetoothDeviceListAdapter = new PairedDevicesCustomAdapter(viewModel.getPairedDevicesName());

        //bluetooth is disabled
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetoothIntent);
            Log.d(TAG,"Bluetooth is disabled!");
        }

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
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
                    viewModel.getPairedDevicesName().observe(getViewLifecycleOwner(), devicesObserver);
                    recycler.setAdapter(bluetoothDeviceListAdapter);
                    // if state equals Bluetooth turned on
                } else if(state == BluetoothAdapter.STATE_ON){
                    // update pairedDevices
                    viewModel.updatePairedDevicesName();

                    //if there is no device -> add placeholder into list
                    if(viewModel.getPairedDevicesName().getValue().size() == 0) {
                        ArrayList<String> noDevicePlaceholder = new ArrayList<String>();
                        noDevicePlaceholder.add(getResources().getString(R.string.no_bluetooth_device));
                        viewModel.getPairedDevicesName().setValue(noDevicePlaceholder);
                    }
                    // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                    viewModel.getPairedDevicesName().observe(getViewLifecycleOwner(), devicesObserver);
                    recycler.setAdapter(bluetoothDeviceListAdapter);
                }

            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.list_paired_devices);
        //update the list of devices
        viewModel.updatePairedDevicesName();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(bluetoothDeviceListAdapter);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPairedDevicesName().observe(getViewLifecycleOwner(), devicesObserver);

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

        // Tells Broadcastreceiver to wait for certain events in this case Bluetooth Action State Changed(On or Off)
        IntentFilter bluetoothStateChange = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(bluetoothStateChangeReceiver, bluetoothStateChange);

        viewModel.getPairedDevicesName().observe(getViewLifecycleOwner(), devicesObserver);
    }

    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    private void onClickFirstBluetoothConnection(){
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ModelSelectionFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

    }


}