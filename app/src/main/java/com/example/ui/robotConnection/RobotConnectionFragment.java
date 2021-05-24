package com.example.ui.robotConnection;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    protected PairedDevicesCustomAdapter deviceListAdapter;
    protected MainViewModel viewModel;
    protected RecyclerView recycler;

    // Observer to check if amount of paired Devices has been changed
    public final Observer<ArrayList<Device>> devicesObserver = new Observer<ArrayList<Device>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<Device> newDevicesList) {
            // Update the UI
            (deviceListAdapter).notifyDataSetChanged();
        }
    };

    public RobotConnectionFragment(@LayoutRes int contentLayoutId){
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        deviceListAdapter = new PairedDevicesCustomAdapter(viewModel.getPairedDevices(), this);

        Log.d(TAG, "onCreate");

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.list_paired_devices);

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(deviceListAdapter);
    }

    public abstract void onClickDevice(Device device);
}
