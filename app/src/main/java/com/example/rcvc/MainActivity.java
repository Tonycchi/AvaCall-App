package com.example.rcvc;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jitsi.meet.sdk.JitsiMeetActivity;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;

import io.github.controlwear.virtual.joystick.android.JoystickView;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity{

    // settings
    SharedPreferences sharedPreferences;

    // zum Testen von nicht implementierten Funktionen
    private boolean btIsClicked;
    private boolean showController;
    private boolean toggleController; //false is buttons, true is joystick
    //Declare all the xml objects
    private Button buttonBluetooth;
    private Button buttonOpenRoom;
    private Button buttonShareLink;
    private Button buttonSwitchToRoom;
    private Button buttonMoveForward;
    private Button buttonMoveBackward;
    private Button buttonTurnRight;
    private Button buttonTurnLeft;
    private Button buttonShowController;
    private Button buttonToggleController;
    private TextView textViewConnectionStatus;
    private ListView listViewDevices;
    private JoystickView joystick;

    private WebClient wc;

    private Toast toast;

    private JitsiRoom room;
    private TrimmedURL hostURL, jitsiURL;
    private boolean hostReady;

    private static final String TAG = "MainActivity";

    BluetoothConnectionService bluetoothConnection;
    private boolean startedConnection;

    // Bluetooth adapter of our device
    private BluetoothAdapter btAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;
    // All paired devices
    private ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    // Connected Robots
    private ButtonController buttonController;
    private AnalogController analogController;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        InitialzieUI();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }

        InitialzieUI();
    }

    public void InitialzieUI() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            hostURL = new TrimmedURL(sharedPreferences.getString("host_url", ""));
            jitsiURL = new TrimmedURL(sharedPreferences.getString("jitsi_url", ""));
            hostReady = true;
        } catch (MalformedURLException e) {
            Bundle bundle = new Bundle();
            // first put id of error message in bundle using defined key
            bundle.putInt(ErrorDialogFragment.MSG_KEY, R.string.error_malformed_url);
            ErrorDialogFragment error = new ErrorDialogFragment(MainActivity.this, getString(R.string.error_malformed_url));
            // then pass bundle to dialog and show
            error.setArguments(bundle);
            error.show(this.getSupportFragmentManager(), TAG);
            hostReady = false;
        }

        // get all buttons
        buttonBluetooth = findViewById(R.id.button_bluetooth);
        buttonOpenRoom = findViewById(R.id.button_open_room);
        buttonShareLink = findViewById(R.id.button_share_link);
        buttonSwitchToRoom = findViewById(R.id.button_switch_to_room);
        textViewConnectionStatus = findViewById(R.id.connection_status);
        listViewDevices = findViewById(R.id.list_paired_devices);
        buttonMoveForward = findViewById(R.id.button_forward);
        buttonMoveBackward = findViewById(R.id.button_backward);
        buttonTurnRight = findViewById(R.id.button_right);
        buttonTurnLeft = findViewById(R.id.button_left);
        buttonShowController = findViewById(R.id.button_show_controller);
        buttonToggleController = findViewById(R.id.button_toggle_controller);
        joystick = findViewById(R.id.joystick);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //bluetooth filter for catching state changes of bluetooth connection (on/off)
        IntentFilter btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverActionStateChanged, btFilter);

        // Custom IntentFilter for catching Intent from ConnectedThread if connection is lost
        IntentFilter connectionFilter = new IntentFilter(getString(R.string.action_check_connection));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverConnection, connectionFilter);

        // Custom IntentFilter for catching action on closing negativeButton of ErrorDialogFragment
        IntentFilter negativeButtonFilter = new IntentFilter(getString(R.string.action_negative_button));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverNegativeButton, negativeButtonFilter);

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                analogController.sendPowers(angle, strength);
                Log.d(TAG, "joystick values: " + angle + " " + strength);
            }
        });

        buttonMoveForward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving forward is send to ev3
                buttonController.sendPowers(ButtonController.FORWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                buttonController.sendPowers(ButtonController.STOP);
            }

            return true;
        });

        buttonMoveBackward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving backward is send to ev3
                buttonController.sendPowers(ButtonController.BACKWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                buttonController.sendPowers(ButtonController.STOP);
            }

            return true;
        });

        buttonTurnRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the right are send to ev3
                buttonController.sendPowers(ButtonController.TURN_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                buttonController.sendPowers(ButtonController.STOP);
            }

            return true;
        });

        buttonTurnLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the left are send to ev3
                buttonController.sendPowers(ButtonController.TURN_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                buttonController.sendPowers(ButtonController.STOP);
            }

            return true;
        });


        // Try to start bluetooth connection with paired device that was clicked
        listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            selectedDevice = pairedDevices.get(position);
            deviceUUIDs = selectedDevice.getUuids();
            bluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            startBTConnection(selectedDevice, deviceUUIDs);
        });

        setAllButtonsUsable(); //TODO bei release rausnehmen

        if (bluetoothConnection != null) {
            buttonOpenRoom.setEnabled(true);
            buttonShowController.setVisibility(View.VISIBLE);
            textViewConnectionStatus.setText(String.format(getResources().getString(R.string.connection_status_true), selectedDevice.getName()));
        }
        showController = false;
        toggleController = false;

        if (room != null) {
            setEnableLinkAndRoom(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // replace with switch if more menu items introduced
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts a connection between our device and the device we want to connect with
     *
     * @param device the device to connect with
     * @param uuid   the uuids of the device
     */
    public void startBTConnection(BluetoothDevice device, ParcelUuid[] uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        bluetoothConnection.startClient(device, uuid);
        startedConnection = true;
        buttonController = new ButtonController(this, bluetoothConnection);
        analogController = new AnalogController(this, bluetoothConnection);
    }

    /**
     * Checks if the connection is valid and changes variables and buttons on screen accordingly
     */
    public void onConnection() {
        buttonController.sendPowers(ButtonController.STOP);
        switch (bluetoothConnection.getConnectionStatus()) {
            case 1: // Connection was successful
                textViewConnectionStatus.setText(String.format(getResources().getString(R.string.connection_status_true), selectedDevice.getName()));
                buttonBluetooth.setText(getString(R.string.button_bluetooth_connected));
                btIsClicked = true;
                buttonOpenRoom.setEnabled(true);
                listViewDevices.setVisibility(View.INVISIBLE);
                buttonShowController.setVisibility(View.VISIBLE);
                break;
            case 2: // Could not connect
                showToast(getString(R.string.connection_init_error));
                resetConnection();
                listViewDevices.setVisibility(View.INVISIBLE);
                break;
            case 3: // Connection lost
                showToast(getString(R.string.connection_lost));
                resetConnection();
                break;
            default: // connectionStatus was not set yet
                break;
        }
    }

    /**
     * On destroy, all receivers will be unregistered
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            resetConnection();
        }
        try {
            unregisterReceiver(receiverActionStateChanged);
            unregisterReceiver(receiverConnection);
            unregisterReceiver(receiverNegativeButton);
        } catch (Exception e) {
            Log.e(TAG, "Receiver not registered, could not unregister");
        }
    }

    /**
     * Create a BroadcastReceiver that catches Intent in ConnectedThread and runs onConnection
     */
    private final BroadcastReceiver receiverConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onConnection();
        }
    };

    private final BroadcastReceiver receiverNegativeButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String intentMessage = bundle.getString("intent message");
            if (intentMessage.equals(getString(R.string.error_no_paired_devices))) {
                // if there are no paired devices, open bluetooth settings
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
            }
        }
    };

    /**
     * Create a BroadcastReceiver for ACTION_STATE_CHANGED changes
     * Whenever Bluetooth is turned off while we are in a connection, reset everything
     */
    private final BroadcastReceiver receiverActionStateChanged = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth Status has been turned off
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    resetConnection();
                }
            }
        }
    };

    /**
     * If bluetooth is disabled, this button will enable it, if bluetooth is is enabled and this button is clicked, it will show all paired devices.
     * If this button is clicked while we have a connection, it will reset the connection
     */
    public void onClickBluetooth(View v) {
        if (btIsClicked) {
            resetConnection();
        } else {
            if (!btAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
            }
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "btAdapterEnabled");

                ArrayList<String> names = getPairedDevices();
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                listViewDevices.setAdapter(listAdapter);
                listViewDevices.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * On click of the openRoom button we create a jitsi room with some options and enable the shareLink and
     * switchToRoom button.
     */
    public void onClickOpenRoom(View v) throws URISyntaxException {
        if (room == null && hostReady) {
            room = new JitsiRoom(jitsiURL.url);
            wc = new WebClient(new URI("wss://" + "mintclub.org:" + sharedPreferences.getString("host_port", "22222")), hostURL.url + "/" + room.id, jitsiURL.url, analogController);
            wc.connect();
        } else if (!hostReady) {
            Bundle bundle = new Bundle();
            // first put id of error message in bundle using defined key
            bundle.putInt(ErrorDialogFragment.MSG_KEY, R.string.error_malformed_url);
            ErrorDialogFragment error = new ErrorDialogFragment(MainActivity.this, getString(R.string.error_malformed_url));
            // then pass bundle to dialog and show
            error.setArguments(bundle);
            error.show(this.getSupportFragmentManager(), TAG);
        }

        setEnableLinkAndRoom(true);
        showToast(getString(R.string.toast_room_opened));
    }

    /**
     * The link for the jitsi room gets copied to the clipboard
     */
    public void onClickShareLink(View v) {
        buttonController = new ButtonController(this, bluetoothConnection);
        analogController = new AnalogController(this, bluetoothConnection);
        if (room == null) {
            showToast(getString(R.string.toast_no_open_room));
        } else {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.jitsi_room_link), hostURL.url + "/" + room.id);
            clipboard.setPrimaryClip(clip);

            showToast(getString(R.string.toast_link_copied));
        }
    }

    /**
     * Opens the jitsi room with the options created before and switches to a new window with the jitsi room
     */
    public void onClickSwitchToRoom(View v) {
        JitsiMeetActivity.launch(this, room.options);
    }

    /**
     * @param message The message to pop up at the bottom of the screen
     */
    public void showToast(String message) {
        if(toast ==null)
            toast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );
        toast.setText(message);
        toast.show();
    }

    /**
     * @param enabled The boolean to decide if we want to enable or disable the buttons
     *                Enables or disables the shareLink and switchToRoom button since they are only used together.
     */
    private void setEnableLinkAndRoom(boolean enabled) {
        buttonShareLink.setEnabled(enabled);
        buttonSwitchToRoom.setEnabled(enabled);
    }

    /**
     * @return an ArrayList which contains all paired bluetooth devices, if there are no paired devices an error message pops up
     * and you get redirected to the bluetooth settings
     */
    public ArrayList<String> getPairedDevices() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                pairedDevices.add(device);
                names.add(device.getName());
            }
        } else {
            Bundle bundle = new Bundle();
            // first put id of error message in bundle using defined key
            bundle.putInt(ErrorDialogFragment.MSG_KEY, R.string.error_no_paired_devices);
            ErrorDialogFragment error = new ErrorDialogFragment(MainActivity.this, getString(R.string.error_no_paired_devices));
            // then pass bundle to dialog and show
            error.setArguments(bundle);
            error.show(this.getSupportFragmentManager(), TAG);
        }
        return names;
    }

    /**
     * reset connection and change variables when we disconnect (via button or bluetooth)
     */
    public void resetConnection() {
        if (startedConnection) {
            startedConnection = false;
            buttonController.sendPowers(ButtonController.STOP);
            btIsClicked = false;
            buttonBluetooth.setText(getString(R.string.button_bluetooth_disconnected));
            buttonOpenRoom.setEnabled(false);
            setEnableLinkAndRoom(false);
            textViewConnectionStatus.setText(getString(R.string.connection_status_false));
            showController = true;
            showController();
            buttonShowController.setVisibility(View.INVISIBLE);
            bluetoothConnection.cancel();
            bluetoothConnection = null;
            selectedDevice = null;
            pairedDevices = new ArrayList<>();
            deviceUUIDs = null;
            room = null;
            if (wc != null) {
                wc.close();
            }
        }
    }

    /**
     * calls the showController method
     */
    public void onClickShowController(View v) {
        showController();
    }

    /**
     * makes the controller visible or invisible
     */
    public void showController() {
        Log.d(TAG, String.valueOf(showController));
        if (!showController) {
            buttonToggleController.setVisibility(View.VISIBLE);
            if (!toggleController) {
                setVisibilityButtons(View.VISIBLE);
            } else {
                setVisibilityJoystick(View.VISIBLE);
            }
            buttonShowController.setText(R.string.button_controller_disable);
        } else {
            setVisibilityButtons(View.INVISIBLE);
            setVisibilityJoystick(View.INVISIBLE);
            buttonToggleController.setVisibility(View.INVISIBLE);
            buttonShowController.setText(R.string.button_controller_enable);
            toggleController = false;
            buttonToggleController.setText(R.string.button_switch_to_joystick);
        }
        showController = !showController;
    }

    /**
     * toggles between buttonController and joystickController
     */
    public void onClickToggleController(View v) {
        if (!toggleController) {
            setVisibilityButtons(View.INVISIBLE);
            setVisibilityJoystick(View.VISIBLE);
            buttonToggleController.setText(R.string.button_switch_to_buttons);
        } else {
            setVisibilityJoystick(View.INVISIBLE);
            setVisibilityButtons(View.VISIBLE);
            buttonToggleController.setText(R.string.button_switch_to_joystick);
        }
        toggleController = !toggleController;
    }

    /**
     * Set the visibility of the control buttons according to the given param
     *
     * @param visibility the visibility that the control buttons will the get set to
     */
    public void setVisibilityButtons(int visibility) {
            buttonMoveForward.setVisibility(visibility);
            buttonMoveBackward.setVisibility(visibility);
            buttonTurnRight.setVisibility(visibility);
            buttonTurnLeft.setVisibility(visibility);
    }

    public void setVisibilityJoystick(int visibility) {
            joystick.setVisibility(visibility);
    }

    private void setAllButtonsUsable() { //TODO später rausnehmen, nur zum testen
        buttonBluetooth.setEnabled(true);
        buttonOpenRoom.setEnabled(true);
        buttonShareLink.setEnabled(true);
        buttonSwitchToRoom.setEnabled(true);
        buttonShowController.setVisibility(View.VISIBLE);
    }
}