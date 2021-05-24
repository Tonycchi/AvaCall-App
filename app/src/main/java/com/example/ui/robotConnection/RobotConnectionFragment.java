package com.example.ui.robotConnection;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MainViewModel;
import com.example.model.robotConnection.Device;
import com.example.rcvc.R;
import com.example.ui.HostedFragment;

import java.util.ArrayList;

public abstract class RobotConnectionFragment extends HostedFragment {

    private static final String TAG = "RobotConnectionFragment";

    protected final MainViewModel viewModel;
    protected PairedDevicesItem deviceListItem;
    protected RecyclerView deviceList;

    // Observer to check if amount of paired Devices has been changed
    public final Observer<ArrayList<Device>> devicesObserver = new Observer<ArrayList<Device>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<Device> newDevicesList) {
            // Update the UI
            (deviceListItem).notifyDataSetChanged();
        }
    };

    public RobotConnectionFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        deviceListItem = new PairedDevicesItem(viewModel.getPairedDevices(), this);

        Log.d(TAG, "onCreate");

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        deviceList = view.findViewById(R.id.list_paired_devices);

        deviceList.setHasFixedSize(true);
        deviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceList.setAdapter(deviceListItem);
    }

    public abstract void onClickDevice(Device device);
}
