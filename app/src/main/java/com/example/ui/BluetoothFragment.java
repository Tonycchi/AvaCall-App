package com.example.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
import com.example.robotConnection.PairedDevicesCustomAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BluetoothFragment extends RobotConnectionFragment {

    private static final String TAG = "BluetoothFragment";

    //TODO: decide what of this class can be transfered to RobotConnectionModel

    private AvaCallViewModel viewModel;

    private RecyclerView recycler;

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        recycler = view.findViewById(R.id.list_paired_devices);
        //update the list of devices
        MutableLiveData<ArrayList<String>> bluetoothDevicesName = viewModel.getPairedDevicesName();

        RecyclerView.Adapter bluetoothDeviceListAdapter = new PairedDevicesCustomAdapter(bluetoothDevicesName);

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter((RecyclerView.Adapter) bluetoothDeviceListAdapter);

        // Create the observer which updates the UI and fills the bluetoothDevicesList
        final Observer<ArrayList<String>> devicesObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newDevicesNameList) {
                // Update the UI
                ((RecyclerView.Adapter<?>) bluetoothDeviceListAdapter).notifyDataSetChanged();
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        bluetoothDevicesName.observe(getViewLifecycleOwner(), devicesObserver);

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
        //update the list of devices
        MutableLiveData<ArrayList<String>> bluetoothDevicesName = viewModel.getPairedDevicesName();

        RecyclerView.Adapter bluetoothDeviceListAdapter = new PairedDevicesCustomAdapter(bluetoothDevicesName);

        recycler.setAdapter((RecyclerView.Adapter) bluetoothDeviceListAdapter);

        // Create the observer which updates the UI and fills the bluetoothDevicesList
        final Observer<ArrayList<String>> devicesObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newDevicesNameList) {
                // Update the UI
                ((RecyclerView.Adapter<?>) bluetoothDeviceListAdapter).notifyDataSetChanged();
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        bluetoothDevicesName.observe(getViewLifecycleOwner(), devicesObserver);

        //if there is no device -> add placeholder into list
        if(bluetoothDevicesName.getValue() == null) {
            ArrayList<String> noDevicePlaceholder = new ArrayList<String>();
            noDevicePlaceholder.add(getResources().getString(R.string.no_bluetooth_device));
            bluetoothDevicesName.setValue(noDevicePlaceholder);
        }

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
