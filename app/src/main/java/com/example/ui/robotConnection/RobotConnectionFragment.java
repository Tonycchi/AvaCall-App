package com.example.ui.robotConnection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MainViewModel;
import com.example.model.robotConnection.Device;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;
import com.example.ui.modelSelection.ModelSelectionFragment;

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
                getResources().getString(R.string.connecting_bluetooth_wait), false, true, dialog -> viewModel.cancelRobotConnection());
    }

    protected void changeProgressDialog(){
        Log.d(TAG, "change ProgressDialog");
        progressDialog.setTitle(getResources().getString(R.string.bluetooth_connection_check_device));
    }

    protected void hideProgressDialog(){
        try {
            progressDialog.dismiss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void robotConnectionStatusChanged(Integer newConnectionStatus) {
        //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost, 4 connection is accepted = correct device, 5 connection is not accepted = wrong device
        switch (newConnectionStatus) {
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
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_init_error));
                break;

            case 3:
                Log.d(TAG, "Case 3: Connection lost!");
                hideProgressDialog();
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_lost));
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
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.bluetooth_connection_wrong_device));
                break;

            default:
                Log.d(TAG, "Default: Something strange or nothing(Case -1) happend with the connection.");
                showProgressDialog();
                break;
        }
    }

    protected void switchToNextFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ModelSelectionFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    public abstract void onClickDevice(Device device);
}
