package com.example.rcvc;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity {

    private boolean btIsClicked = false;
    //Declare all the xml objects
    private Button bluetooth;
    private Button openRoom;
    private Button shareLink;
    private Button switchToRoom;
    private TextView connectionStatus;
    private ListView myListView;
    private Button forward;
    private Button backward;
    private Button right;
    private Button left;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private static final String TAG = "MainActivity";

    BluetoothConnectionService mBluetoothConnection;

    // Bluetoothadapter of our device
    private BluetoothAdapter btAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] mDeviceUUIDs;
    // All paired devices
    private ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    //
    private RobotController robotController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = findViewById(R.id.button_bluetooth);
        openRoom = findViewById(R.id.button_open_room);
        shareLink = findViewById(R.id.button_share_link);
        switchToRoom = findViewById(R.id.button_switch_to_room);
        connectionStatus = findViewById(R.id.connection_status);
        myListView = findViewById(R.id.list_paired_devices);
        forward = findViewById(R.id.button_forward);
        backward = findViewById(R.id.button_backward);
        right = findViewById(R.id.button_right);
        left = findViewById(R.id.button_left);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiverActionStateChanged, filter2);

        forward.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving forward is send to ev3
                robotController.sendCommands(RobotController.FORWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                robotController.sendCommands(RobotController.STOP);
            }

            return true;
        });

        backward.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving backward is send to ev3
                robotController.sendCommands(RobotController.BACKWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                robotController.sendCommands(RobotController.STOP);
            }

            return true;
        });

        right.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the right are send to ev3
                robotController.sendCommands(RobotController.TURN_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                robotController.sendCommands(RobotController.STOP);
            }

            return true;
        });

        left.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the left are send to ev3
                robotController.sendCommands(RobotController.TURN_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                robotController.sendCommands(RobotController.STOP);
            }

            return true;
        });


        myListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedDevice = pairedDevices.get(position);
            connectionStatus.setText(String.format(getResources().getString(R.string.connection_status_true), selectedDevice.getName()));
            bluetooth.setText(getString(R.string.button_bluetooth_connected));
            btIsClicked = true;
            openRoom.setEnabled(true);
            Object o = myListView.getItemAtPosition(position);
            String str = (String) o; //As you are using Default String Adapter
            myListView.setVisibility(View.INVISIBLE);
            mDeviceUUIDs = selectedDevice.getUuids();
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            startBTConnection(selectedDevice, mDeviceUUIDs);
            setVisibilityControlButtons(true);
        });
    }

    // Starts a connection between our device and the device we want to connect with
    public void startBTConnection(BluetoothDevice device, ParcelUuid[] uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,mDeviceUUIDs);
        robotController = new RobotController(mBluetoothConnection);
    }

    //On destroy, all receivers will be unregistered
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverActionStateChanged);
    }

    //Create a BroadcastReceiver for ACTION_STATE_CHANGED changed.
    // Whenever Bluetooth is turned off while we are in a connection reset everything
    private final BroadcastReceiver receiverActionStateChanged = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth Status has been turned off
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF){
                    resetConnection();
                }
            }
        }
    };

    /**
     * @param v
     * If bluetooth is disabled, this button will enable it, if bluetooth is is enabled and this button is clicked, it will show all paired devices.
     * If this button is clicked while we have a connection, it will reset the connection
     */
    public void onClickBluetooth(View v) {
        if(btIsClicked){
         resetConnection();
        } else {
            if (!btAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
            }
            if (btAdapter.isEnabled()) {

            ArrayList<String> names = getPairedDevices();
            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
            myListView.setAdapter(listAdapter);
            myListView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * @param v
     * On click of the openRoom button we open a jitsi room and enable the shareLink and switchToRoom button.
     */
    public void onClickOpenRoom(View v) {
//        if (!jitsiIsClicked) {
//            jitsiIsClicked = true;
//            setEnableLinkAndRoom(true);
//            showToast("Raum geöffnet");
//        } else {
//            jitsiIsClicked = false;
//            setEnableLinkAndRoom(false);
//            showToast("Raum geschlossen");
//        }
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

    public ArrayList<String> getPairedDevices(){
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                pairedDevices.add(device);
                names.add(device.getName());
            }
        } else {
            showToast("Keine gekoppelten Geräte, koppel erst deinen EV3 über die Bluetoothoptionen");
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        }
        return names;
    }

    // reset Connection and change Variables when we disconnect(via button or bluetooth)
    public void resetConnection(){
        robotController.sendCommands(RobotController.STOP);
        btIsClicked = false;
        bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
        openRoom.setEnabled(false);
        setEnableLinkAndRoom(false);
        connectionStatus.setText(getString(R.string.connection_status_false));
        setVisibilityControlButtons(false);
        mBluetoothConnection.cancel();
        selectedDevice = null;
        pairedDevices = new ArrayList<>();
        mDeviceUUIDs = null;
    }

    /**
     * Set the visibility of the control buttons according to the given param
     * @param vis the visibility that the control buttons will the get set to
     */
    public void setVisibilityControlButtons(boolean vis){
        if (vis) {
            forward.setVisibility(View.VISIBLE);
            backward.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
        } else {
            forward.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
        }
    }
}