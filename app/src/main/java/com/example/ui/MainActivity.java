package com.example.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.data.URLSettings;
import com.example.model.SessionData;
import com.example.model.WebClient;
import com.example.model.connection.BluetoothConnectionService;
import com.example.model.controls.EV3;
import com.example.rcvc.R;

import org.jitsi.meet.sdk.JitsiMeetActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import io.github.controlwear.virtual.joystick.android.JoystickView;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    // gui state booleans
    private boolean bluetoothIsConnected;
    private boolean controllerVisible;
    private boolean directionalButtonsVisible;

    // gui view elements
    private Button buttonBluetooth;
    private Button buttonShareLink;
    private Button buttonSwitchToRoom;
    private Button buttonMoveForward;
    private Button buttonMoveBackward;
    private Button buttonTurnRight;
    private Button buttonTurnLeft;
    private Button buttonToggleController;
    private TextView textViewBluetoothConnectionStatus;
    private TextView textViewServerConnectionStatus;
    private ListView listViewDevices;
    private JoystickView joystick;

    private Toast toast;

    // web & jitsi
    private URLSettings urlSettings;
    private WebClient wc;
    private SessionData session;

    // bluetooth
    private BluetoothConnectionService bluetoothConnection;
    private boolean startedConnection;
    // Bluetooth adapter of our device
    private BluetoothAdapter bluetoothAdapter;
    // Device we want to connect with
    private BluetoothDevice selectedDevice;
    // The UUIDs of the device we want to connect with
    private ParcelUuid[] deviceUUIDs;
    // All paired devices
    private ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    // Connected robot
    private EV3 controller;


    // lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeUI();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_main);

        initializeUI();
    }

    /**
     * On destroy, all receivers will be unregistered
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetConnection();
        try {
            unregisterReceiver(receiverConnection);
            unregisterReceiver(receiverNegativeButton);
        } catch (Exception e) {
            //Log.d(TAG, "Receiver not registered, could not unregister");
        }
    }

    /**
     * On resume, update all the setting changes
     */
    @Override
    protected void onResume() {
        super.onResume();
//        urlFactory = new URLFactory(this);
        controller = new EV3(this, bluetoothConnection);
    }

    /**
     * initialize UI + misc
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initializeUI() {
//        urlFactory = new URLFactory(this);

        // get all buttons
        buttonBluetooth = findViewById(R.id.button_bluetooth);
        buttonShareLink = findViewById(R.id.button_share_link);
        buttonSwitchToRoom = findViewById(R.id.button_switch_to_room);
        textViewBluetoothConnectionStatus = findViewById(R.id.connection_status);
        textViewServerConnectionStatus = findViewById(R.id.server_connection_status);
        listViewDevices = findViewById(R.id.list_paired_devices);
        buttonMoveForward = findViewById(R.id.button_forward);
        buttonMoveBackward = findViewById(R.id.button_backward);
        buttonTurnRight = findViewById(R.id.button_right);
        buttonTurnLeft = findViewById(R.id.button_left);
        buttonToggleController = findViewById(R.id.button_toggle_controller);
        joystick = findViewById(R.id.joystick);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Custom IntentFilter for catching Intent from ConnectedThread if connection is lost
        IntentFilter connectionFilter = new IntentFilter(getString(R.string.action_check_connection));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverConnection, connectionFilter);

        // Custom IntentFilter for catching action on closing negativeButton of ErrorDialogFragment
        IntentFilter negativeButtonFilter = new IntentFilter(getString(R.string.action_negative_button));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverNegativeButton, negativeButtonFilter);

        joystick.setOnMoveListener((angle, strength) -> controller.send(angle, strength));

        buttonMoveForward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving forward is send to ev3
                controller.send(90, 100);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                controller.send(0, 0);
            }

            return true;
        });

        buttonMoveBackward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct command for moving backward is send to ev3
                controller.send(270, 100);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                controller.send(0, 0);
            }

            return true;
        });

        buttonTurnRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the right are send to ev3
                controller.send(0, 100);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                controller.send(0, 0);
            }

            return true;
        });

        buttonTurnLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //when button is being pressed down, direct commands for turning to the left are send to ev3
                controller.send(180, 100);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //when button is being released, direct command for stopping is send to ev3
                controller.send(0, 0);
            }

            return true;
        });

        // Try to start bluetooth connection with paired device that was clicked
        listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            selectedDevice = pairedDevices.get(position);
            deviceUUIDs = selectedDevice.getUuids();
            //bluetoothConnection = new BluetoothConnectionService(this);
            startBTConnection(selectedDevice, deviceUUIDs);
        });

        // if there is an existing connection, show buttons accordingly
        //REPLACED:
        //if (bluetoothConnection != null && bluetoothConnection.getConnectionStatus() == 1) {
        if (bluetoothConnection != null){
            bluetoothIsConnected = true;
            buttonBluetooth.setText(getString(R.string.button_bluetooth_connected));
            buttonShareLink.setEnabled(true);
            controllerVisible = false;
            if (directionalButtonsVisible) {
                buttonToggleController.setText(R.string.button_switch_to_joystick);
            }
            buttonToggleController.setVisibility(View.VISIBLE);
            showController();
            textViewBluetoothConnectionStatus.setText(String.format(getResources().getString(R.string.connection_status_true), selectedDevice.getName()));
        }

        if (session != null) {
            buttonSwitchToRoom.setEnabled(true);
            textViewServerConnectionStatus.setText(String.format(getResources().getString(R.string.server_connection_status_true), session.getID()));
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
     * Create a BroadcastReceiver that catches Intent in ConnectedThread and runs onConnection
     */
    private final BroadcastReceiver receiverConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (bluetoothConnection != null) {
                onConnection();
            }
        }
    };

    /**
     * Create a BroadcastReceiver that catches intents from errorDialogFragment
     */
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
     * @param message The message to pop up at the bottom of the screen
     */
    public void showToast(String message) {
        if(toast ==null)
            toast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );
        toast.setText(message);
        toast.show();
    }

    /**
     * Shows an ErrorDialogFragment on screen
     * @param errorStringValue the errorMessage that gets shown
     */
    public void showErrorDialogFragment(int errorStringValue) {
        Bundle bundle = new Bundle();
        // first put id of error message in bundle using defined key
        bundle.putInt(ErrorDialogFragment.MSG_KEY, errorStringValue);
        ErrorDialogFragment error = new ErrorDialogFragment(this, getString(errorStringValue));
        // then pass bundle to dialog and show
        error.setArguments(bundle);
        error.show(this.getSupportFragmentManager(), TAG);
    }

    /**
     * If bluetooth is disabled, this button will enable it, if bluetooth is enabled and this button is clicked, it will show all paired devices.
     * If this button is clicked while we have a connection, it will reset the connection
     */
    public void onClickBluetooth(View v) {
        if (bluetoothIsConnected) {
            resetConnection();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
            }
            if (bluetoothAdapter.isEnabled()) {
                ArrayList<String> names = getPairedDevices();
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                listViewDevices.setAdapter(listAdapter);
                listViewDevices.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Starts a connection to the server and opens an intent to share the invite link
     */
    public void onClickShareLink(View v) {
        //first create room
        boolean connectionError = false;
        if (session == null) {
            String jitsi = urlSettings.getJitsi_https();
            try {
                wc = new WebClient(new URI(urlSettings.getHost_wss()), urlSettings.getJitsi_plain(), controller);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            wc.connect();
            //continue with share link when ws is connected

            long startTime = System.currentTimeMillis();
            connectionError = false;
            //check if a timeout occurs while connecting to server
            while(!wc.isReady()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= 5000) {
                    connectionError = true;
                    break;
                }
            }

            if (!connectionError) {
                String id = wc.getId();
                session = new SessionData(jitsi, urlSettings.getHost_https(), id);
                textViewServerConnectionStatus.setText(String.format(getResources().getString(R.string.server_connection_status_true), session.getID()));
                buttonSwitchToRoom.setEnabled(true);
            } else {
                showErrorDialogFragment(R.string.server_connection_error);
            }
        }
        if (!connectionError) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.room_link), session.getShareURL());
                clipboard.setPrimaryClip(clip);
                showToast(getString(R.string.toast_link_copied));
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, session.getShareURL());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        }
    }

    /**
     * Opens the jitsi room with the options created before and switches to a new window with the jitsi room
     */
    public void onClickSwitchToRoom(View v) {
        wc.setReceiveCommands();
        JitsiMeetActivity.launch(this, session.getOptions());
    }

    /**
     * Toggles between buttonController and joystickController
     */
    public void onClickToggleController(View v) {
        if (directionalButtonsVisible) {
            setVisibilityButtons(View.INVISIBLE);
            joystick.setVisibility(View.VISIBLE);
            buttonToggleController.setText(R.string.button_switch_to_buttons);
        } else {
            joystick.setVisibility(View.INVISIBLE);
            setVisibilityButtons(View.VISIBLE);
            buttonToggleController.setText(R.string.button_switch_to_joystick);
        }
        directionalButtonsVisible = !directionalButtonsVisible;
    }

    /**
     * Starts a connection between our device and the device we want to connect with
     *
     * @param device the device to connect with
     * @param uuid   the uuids of the device
     */
    public void startBTConnection(BluetoothDevice device, ParcelUuid[] uuid) {
        //Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        bluetoothConnection.startClient(device, uuid);
        startedConnection = true;
        controller = new EV3(this, bluetoothConnection);
    }

    /**
     * Checks if the connection is valid and changes variables and buttons on screen accordingly
     */
    public void onConnection() {
        if (controller != null) {
            controller.send(0, 0);
        }
        //REPLACED:
        //switch (bluetoothConnection.getConnectionStatus()) {
        switch (0) {
            case 1: // Connection was successful
                //Log.d(TAG, "Connected: " + this);
                textViewBluetoothConnectionStatus.setText(String.format(getResources().getString(R.string.connection_status_true), selectedDevice.getName()));
                buttonBluetooth.setText(getString(R.string.button_bluetooth_connected));
                bluetoothIsConnected = true;
                buttonShareLink.setEnabled(true);
                listViewDevices.setVisibility(View.INVISIBLE);
                showController();
                break;
            case 2: // Could not connect
                showToast(getString(R.string.bluetooth_connection_init_error));
                resetConnection();
                listViewDevices.setVisibility(View.INVISIBLE);
                break;
            case 3: // Connection lost
                //Log.d(TAG, "Disconnected: " + this);
                showToast(getString(R.string.bluetooth_connection_lost));
                resetConnection();
                break;
            default: // connectionStatus was not set yet
                break;
        }
    }

    /**
     * @return an ArrayList which contains all paired bluetooth devices, if there are no paired devices an error message pops up
     * and you get redirected to the bluetooth settings
     */
    public ArrayList<String> getPairedDevices() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        if (devices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : devices) {
                pairedDevices.add(device);
                names.add(device.getName());
            }
        } else {
            showErrorDialogFragment(R.string.error_no_paired_devices);
        }
        return names;
    }

    /**
     * Makes the controller visible or invisible
     */
    public void showController() {
        if (!controllerVisible) {
            buttonToggleController.setVisibility(View.VISIBLE);
            if (directionalButtonsVisible) {
                setVisibilityButtons(View.VISIBLE);
            } else {
                joystick.setVisibility(View.VISIBLE);
            }
        } else {
            setVisibilityButtons(View.INVISIBLE);
            joystick.setVisibility(View.INVISIBLE);
            buttonToggleController.setVisibility(View.INVISIBLE);
            directionalButtonsVisible = false;
            buttonToggleController.setText(R.string.button_switch_to_buttons);
        }
        controllerVisible = !controllerVisible;
    }

    /**
     * Reset connection and change variables when we disconnect (via button or bluetooth)
     */
    public void resetConnection() {
        if (startedConnection) {
            startedConnection = false;
            //stops the EV3 if connection gets lost
            controller.send(0, 0);
            bluetoothIsConnected = false;
            bluetoothConnection.cancel();
            selectedDevice = null;
            pairedDevices = new ArrayList<>();
            deviceUUIDs = null;
            session = null;
            controllerVisible = true;
            showController();
            buttonShareLink.setEnabled(false);
            buttonSwitchToRoom.setEnabled(false);
            buttonBluetooth.setText(getString(R.string.button_bluetooth_disconnected));
            textViewBluetoothConnectionStatus.setText(getString(R.string.connection_status_false));
            textViewServerConnectionStatus.setText(getString(R.string.server_connection_status_false));
            if (wc != null) {
                wc.close();
            }
        }
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

}