package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jitsi.meet.sdk.JitsiMeetActivity;

public class MainActivity extends AppCompatActivity {

    // zum Testen von nicht implementierten Funktionen
    private boolean btIsClicked = false;
    private Button bluetooth;
    private Button openRoom;
    private Button shareLink;
    private Button switchToRoom;
    private TextView connectionStatus;
    private String deviceName = "RALLLE";

    private JitsiRoom room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get all buttons
        bluetooth = findViewById(R.id.button_bluetooth);

        openRoom = findViewById(R.id.button_open_room);

        shareLink = findViewById(R.id.button_share_link);

        switchToRoom = findViewById(R.id.button_switch_to_room);

        connectionStatus = findViewById(R.id.connection_status);
    }

    /**
     * @param v
     * If we don't have a bluetooth connection this button enables the openRoom button on click. If
     * we do have a bluetooth connection this button disables all the other buttons.
     */
    public void onClickBluetooth(View v) {
        if (!btIsClicked) {
            btIsClicked = true;
            bluetooth.setText(getString(R.string.button_bluetooth_connected));

            openRoom.setEnabled(true);
            connectionStatus.setText(String.format(
                    getResources().getString(R.string.connection_status_true), deviceName)
            );
        } else {
            btIsClicked = false;
            bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
            openRoom.setEnabled(false);
            setEnableLinkAndRoom(false);
            connectionStatus.setText(getResources().getString(R.string.connection_status_false));
        }
    }

    /**
     * @param v
     * On click of the openRoom button we create a jitsi room with some options and enable the shareLink and
     * switchToRoom button.
     */
    public void onClickOpenRoom(View v) {
        if (room == null) {
            room = new JitsiRoom();
        }

        setEnableLinkAndRoom(true);
        showToast(getString(R.string.toast_room_opened));
    }

    /**
     * @param v
     * The link for the jitsi room gets copied to the clipboard
     */
    public void onClickShareLink(View v) {
        if (room == null) {
            room = new JitsiRoom();
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Jitsi Room Link", room.url);
        clipboard.setPrimaryClip(clip);

        showToast(getString(R.string.toast_link_copied));
    }

    /**
     * @param v
     * Opens the jitsi room with the options created before and switches to a new window with the jitsi room
     */
    public void onClickSwitchToRoom(View v) {
        JitsiMeetActivity.launch(this, room.options);
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
}