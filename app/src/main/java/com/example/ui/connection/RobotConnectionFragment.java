package com.example.ui.connection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.model.connection.Device;
import com.example.rcvc.R;
import com.example.ui.HostedFragment;

import java.util.ArrayList;

public abstract class RobotConnectionFragment extends HostedFragment {

    private static final String TAG = "RobotConnectionFragment";

    protected MainViewModel viewModel;
    protected PairedDevicesItem deviceListItem;
    protected RecyclerView deviceList;

    //dialog while connecting to device
    protected ProgressDialog progressDialog;

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

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
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

    protected void showProgressDialog(){
        Log.d(TAG, "show ProgressDialog");
        //initprogress dialog
        progressDialog = ProgressDialog.show(this.getContext(), getResources().getString(R.string.connecting_title),
                getResources().getString(R.string.connecting_bluetooth_wait), false, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        viewModel.connectingCanceled();
                    }
                });
    }

    protected void hideProgessDialog(){
        try {
            progressDialog.dismiss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public abstract void onClickDevice(Device device);
}
