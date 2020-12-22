package com.example.rcvc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private ListView myListView;
    private Button forward;
    private Button stop;
    private Button backward;
    private Button right;
    private Button left;

    private boolean testBool = false;

    private String startDirCom = "0D002A00800000A4000";
    private String endDirCom = "A6000";

    private String plus_100 = "8164";
    private String plus_50 = "8132";
    private String minus_50 = "81CE";
    private String plus_25 = "8119";
    private String minus_25 = "81E7";
    private String port_BC = "6";
    private String port_B = "2";
    private String port_C = "4";

    //start + port + power + end + port
    private String directCommandForward = startDirCom + port_BC + plus_50 + endDirCom + port_BC;
    private String directCommandBackward = startDirCom + port_BC + minus_50 + endDirCom + port_BC;
    private String directCommandRightPortB = startDirCom + port_B + minus_50 + endDirCom + port_B;
    private String directCommandRightPortC = startDirCom + port_C + plus_50 + endDirCom + port_C;
    private String directCommandLeftPortB = startDirCom + port_B + plus_50 + endDirCom + port_B;
    private String directCommandLeftPortC = startDirCom + port_C + minus_50 + endDirCom + port_C;
    private String directCommandStop = "09002A00000000A3000F00";

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
    private ArrayList<BluetoothDevice> pairedDevices = new ArrayList<BluetoothDevice>();

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
        stop = findViewById(R.id.button_stop);
        backward = findViewById(R.id.button_backward);
        right = findViewById(R.id.button_right);
        left = findViewById(R.id.button_left);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter2 = new IntentFilter(btAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter2);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = pairedDevices.get(position);
                connectionStatus.setText(getResources().getString(R.string.connection_status_true) + selectedDevice.getName());
                bluetooth.setText(getString(R.string.button_bluetooth_connected));
                btIsClicked = true;
                openRoom.setEnabled(true);
                Object o = myListView.getItemAtPosition(position);
                String str = (String) o;//As you are using Default String Adapter
                myListView.setVisibility(View.INVISIBLE);
                mDeviceUUIDs = selectedDevice.getUuids();
                mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                startBTConnection(selectedDevice, mDeviceUUIDs);
                setVisibilityControlButtons(true);
            }
        });
    }
    // Starts a connection between our device and the device we want to connect with
    public void startBTConnection(BluetoothDevice device, ParcelUuid[] uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,mDeviceUUIDs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //Create a BroadcastReceiver for ACTION_STATE_CHANGED changed.
    // Whenever Bluetooth is turned off while we are in a connection reset everything
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (String.valueOf(btAdapter.ACTION_STATE_CHANGED).equals(action)) {
                // Bluetooth Status has been turned off
                final int state = intent.getIntExtra(btAdapter.EXTRA_STATE, btAdapter.ERROR);
                if(state == btAdapter.STATE_OFF || state == btAdapter.STATE_TURNING_OFF){
                    resetConnection();
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
         resetConnection();
        } else {
            if (!btAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
            }
            if (btAdapter.isEnabled()) {

            ArrayList<String> names = getPairedDevices();
            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
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
        Intent intent = new Intent(this, JitsiActivity.class);
        startActivity(intent);
    }

    public void onClickStop(View v) {
        mBluetoothConnection.write(hexStringToByteArray(directCommandStop));
    }

    public void onClickForward(View v) {
        mBluetoothConnection.write(hexStringToByteArray(directCommandForward));
    }

    public void onClickBackward(View v) {
        mBluetoothConnection.write(hexStringToByteArray(directCommandBackward));
    }

    public void onClickRight(View v) {
        mBluetoothConnection.write(hexStringToByteArray(directCommandRightPortB));
        mBluetoothConnection.write(hexStringToByteArray(directCommandRightPortC));
    }

    public void onClickLeft(View v) {
        mBluetoothConnection.write(hexStringToByteArray(directCommandLeftPortB));
        mBluetoothConnection.write(hexStringToByteArray(directCommandLeftPortC));
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
        ArrayList<String> names = new ArrayList<String>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                pairedDevices.add(device);
                names.add(device.getName());
            }
        } else {
            showToast("Keine gekoppelten Geräte, koppel erst deinen EV3 über die Bluetoothoptionen");
        }
        return names;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // reset Connection and change Variables when we disconnect(via button or bluetooth)
    public void resetConnection(){
        mBluetoothConnection.write(hexStringToByteArray(directCommandStop));
        btIsClicked = false;
        jitsiIsClicked = false;
        bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
        openRoom.setEnabled(false);
        setEnableLinkAndRoom(false);
        connectionStatus.setText(getString(R.string.connection_status_false));
        setVisibilityControlButtons(false);
        mBluetoothConnection.cancel();
        selectedDevice = null;
        pairedDevices = new ArrayList<BluetoothDevice>();
    }

    public void setVisibilityControlButtons(boolean vis){
        if (vis) {
            forward.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            backward.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
        } else {
            forward.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.INVISIBLE);
            backward.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
            left.setVisibility(View.INVISIBLE);
        }
    }
}