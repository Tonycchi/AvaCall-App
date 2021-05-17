package com.example.robotConnection;

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

    private MutableLiveData<ArrayList<String>> bluetoothDevicesName;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button deviceView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            deviceView = view.findViewById(R.id.button_bluetooth_device);
            deviceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickDevice();
                }
            });
        }

        public Button getDeviceView() {
            return deviceView;
        }
    }

    public void onClickDevice(){
        /*selectedDevice = pairedDevices.get(position);
        deviceUUIDs = selectedDevice.getUuids();
        bluetoothConnection = new BluetoothConnectionService(this);
        startBTConnection(selectedDevice, deviceUUIDs);
        bluetoothConnection.startClient(device, uuid);*/
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param bluetoothDevicesName ArrayList<String> containing the data to populate views to be used
     * by RecyclerView.
     */
    public PairedDevicesCustomAdapter(MutableLiveData<ArrayList<String>> bluetoothDevicesName) {
        this.bluetoothDevicesName = bluetoothDevicesName;
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

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getDeviceView().setText(bluetoothDevicesName.getValue().get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(bluetoothDevicesName.getValue() != null)
            return bluetoothDevicesName.getValue().size();
        else
            return 0;
    }
}
