package com.example.robotConnection;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rcvc.R;
import com.facebook.infer.annotation.Mutable;

import java.util.ArrayList;

public class PairedDevicesCustomAdapter extends RecyclerView.Adapter<PairedDevicesCustomAdapter.ViewHolder> {

    private MutableLiveData<ArrayList<Device>> devices;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button deviceButton;
        private Device device;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            deviceButton = view.findViewById(R.id.button_bluetooth_device);
            deviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickDevice(device);
                }
            });
        }

        public void setDeviceView(Device device){
            deviceButton.setText(device.getName());
            this.device = device;
        }

    }

    public void onClickDevice(Device device){
        /*deviceUUIDs = device.getParcelable().getUuids();
        bluetoothConnection = new BluetoothConnectionService(this);
        startBTConnection(selectedDevice, deviceUUIDs);
        bluetoothConnection.startClient(device, uuid);*/
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param devices ArrayList<String> containing the data to populate views to be used
     * by RecyclerView.
     */
    public PairedDevicesCustomAdapter(MutableLiveData<ArrayList<Device>> devices) {
        this.devices = devices;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and set the device
        viewHolder.setDeviceView(devices.getValue().get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(devices.getValue() != null)
            return devices.getValue().size();
        else
            return 0;
    }
}
