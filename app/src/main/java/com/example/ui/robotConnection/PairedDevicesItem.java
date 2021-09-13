package com.example.ui.robotConnection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.robotConnection.Device;
import com.example.rcvc.R;

import java.util.ArrayList;

public class PairedDevicesItem extends RecyclerView.Adapter<PairedDevicesItem.ViewHolder> {

    private final MutableLiveData<ArrayList<Device>> devices;
    private final RobotConnectionFragment robotConnectionFragment;

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
                    robotConnectionFragment.onClickDevice(device);
                }
            });
        }

        public void setDeviceView(Device device){
            deviceButton.setText(device.getName());
            this.device = device;
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param devices ArrayList<String> containing the data to populate views to be used
     * by RecyclerView.
     */
    public PairedDevicesItem(MutableLiveData<ArrayList<Device>> devices, RobotConnectionFragment robotConnectionFragment) {
        this.devices = devices;
        this.robotConnectionFragment = robotConnectionFragment;
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
