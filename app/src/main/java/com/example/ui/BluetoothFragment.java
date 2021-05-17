package com.example.ui;

import android.os.Bundle;
import android.transition.TransitionInflater;
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

import java.util.ArrayList;

public class BluetoothFragment extends RobotConnectionFragment {

    private AvaCallViewModel viewModel;

    public BluetoothFragment() {
        super(R.layout.bluetooth_connection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AvaCallViewModel.class);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        RecyclerView recycler = view.findViewById(R.id.list_paired_devices);
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
        viewModel.updatePairedDevicesName();

        Button buttonFirstConnection = (Button) view.findViewById(R.id.button_first_connection);
        buttonFirstConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFirstBluetoothConnection();
            }
        });

        getActivity().setTitle(R.string.title_bluetooth);
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
