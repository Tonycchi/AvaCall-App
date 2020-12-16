package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
<<<<<<< HEAD
=======
import android.bluetooth.BluetoothHeadset;
>>>>>>> 400cea7ea637f2f2179ddc9e53134e64adf14cda
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private boolean btIsClicked = false;
    private boolean jitsiIsClicked = false;
    private Button bluetooth;
    private Button openRoom;
    private Button shareLink;
    private Button switchToRoom;
    private TextView connectionStatus;
<<<<<<< HEAD
    private String deviceName = "RALLLE";
    private static final String TAG = "MainActivity";
=======
>>>>>>> 400cea7ea637f2f2179ddc9e53134e64adf14cda

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private BluetoothAdapter btAdapter;

    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //When discovery finds a device
            if (action.equals(btAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = findViewById(R.id.button_bluetooth);
        openRoom = findViewById(R.id.button_open_room);
        shareLink = findViewById(R.id.button_share_link);
        switchToRoom = findViewById(R.id.button_switch_to_room);
        connectionStatus = findViewById(R.id.connection_status);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(btAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(receiver1, filter);

        IntentFilter filter2 = new IntentFilter(btAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver2, filter2);

        boolean test = btAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
        if(test){
            showToast("sex");
        } else {
            showToast("sex2");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver1);
        unregisterReceiver(receiver2);
    }

    //Create a BroadcastReceiver for ACTION_STATE_CHANGED changed.
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (String.valueOf(btAdapter.ACTION_STATE_CHANGED).equals(action)) {
                // Bluetooth Status has been changed
                final int state = intent.getIntExtra(btAdapter.EXTRA_STATE, btAdapter.ERROR);
                if(state == btAdapter.STATE_OFF || state == btAdapter.STATE_TURNING_OFF){
                    btIsClicked = false;
                    jitsiIsClicked = false;
                    bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
                    openRoom.setEnabled(false);
                    setEnableLinkAndRoom(false);
                    connectionStatus.setText(getResources().getString(R.string.connection_status_false));
                }
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_CONNECTION_STATE_Changed.
    private final BroadcastReceiver receiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final int state = intent.getIntExtra(btAdapter.EXTRA_CONNECTION_STATE, btAdapter.ERROR);
            if (String.valueOf(btAdapter.ACTION_CONNECTION_STATE_CHANGED).equals(action)) {
                if(state == btAdapter.STATE_CONNECTED) {
                    // Connection Status has been changed and the device is connected with another device
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    connectionStatus.setText(getResources().getString(R.string.connection_status_true) + device.getName());
                    bluetooth.setText(getString(R.string.button_bluetooth_connected));
                    btIsClicked = true;
                    openRoom.setEnabled(true);
                }
            }
        }
    };

    /**
     * @param v
     * If we don't have a bluetooth connection this button enables the openRoom button on click. If we do have a bluetooth connection this button disables all the other buttons.
     */
    public void onClickBluetooth(View v) {
        if(btIsClicked){
         btIsClicked = false;
         jitsiIsClicked = false;
         bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
         openRoom.setEnabled(false);
         setEnableLinkAndRoom(false);
         connectionStatus.setText(getString(R.string.connection_status_false));
        } else {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        }
      /*  if (!btAdapter.isEnabled()) {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
            btIsClicked = true;
            bluetooth.setText(getString(R.string.button_bluetooth_connected));

            checkIfBTEnabled();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            checkForPairedDevices(pairedDevices);

            openRoom.setEnabled(true);
           // connectionStatus.setText(getResources().getString(R.string.connection_status_true) + deviceName);
        } else {
            btIsClicked = false;
            jitsiIsClicked = false;
            bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
            openRoom.setEnabled(false);
            setEnableLinkAndRoom(false);
            connectionStatus.setText(getResources().getString(R.string.connection_status_false));
            deviceName = "";
        }*/
    }

    /**
     * @param v
     * On click of the openRoom button we open a jitsi room and enable the shareLink and switchToRoom button.
     */
    public void onClickOpenRoom(View v) {
        if (!jitsiIsClicked) {
            jitsiIsClicked = true;
            setEnableLinkAndRoom(true);
            showToast("Raum geöffnet");
        } else {
            jitsiIsClicked = false;
            setEnableLinkAndRoom(false);
            showToast("Raum geschlossen");
        }
    }

    /**
     * @param v
     * The link for the jitsi room gets copied to the clipboard
     */
    public void onClickShareLink(View v) {
        showToast("Link kopiert");
    }

    /**
     * @param v
     * Switches to the next activity with the open jitsi room.
     */
    public void onClickSwitchToRoom(View v) {
        Intent intent = new Intent(this, JitsiActivity.class);
        startActivity(intent);
    }

    /**
     * @param message The message to pop up at the bottom of the screen
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param enabled The boolean to decide if we want to enable or disable the buttons
     * Enables or disables the shareLink and switchToRoom button since they are only used together.
     */
    private void setEnableLinkAndRoom(boolean enabled) {
        shareLink.setEnabled(enabled);
        switchToRoom.setEnabled(enabled);
    }
<<<<<<< HEAD

    public void checkIfBTEnabled() {
        if (!btAdapter.isEnabled()) {
            showToast("Turning On Bluetooth");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            showToast("Bluetooth already On");
        }
    }
    public void checkForPairedDevices(Set<BluetoothDevice> pairedDevices) {
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        } else {
            showToast("No paired devices");
        }
    }

    public void onClickOnOff(View v) {
        Log.d(TAG, "onClickOnOff: enabling/disabling bluetooth");
        if (btAdapter == null) {
            Log.d(TAG, "OnOffBT: Does not have BT capabilities.");
        }
        if (!btAdapter.isEnabled()) {
            Intent onBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(onBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);
        }
        if (btAdapter.isEnabled()) {
            btAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);
        }
    }
=======
>>>>>>> 400cea7ea637f2f2179ddc9e53134e64adf14cda
}