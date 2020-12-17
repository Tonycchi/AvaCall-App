package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private class RoomLink {
        private char[] chars = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72,
                73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100,
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117,
                118, 119, 120, 121};
        public String id, url;

        // TODO: maybe pass url options to here
        public RoomLink(int length) {
            id = randomLinkString(length);
            url = "https://meet.jit.si/" + id /*+ "#" + "&config.prejoinPageEnabled=true"*/;
        }

        private String randomLinkString(int length) {
            Random random = new Random();
            char[] out = new char[length];
            for (int i = 0; i < length; i++) {
                int rnd = random.nextInt(chars.length);
                out[i] = chars[rnd];
            }
            return new String(out);
        }
    }

    private boolean btIsClicked = false;
    private boolean jitsiIsClicked = false;
    private Button bluetooth;
    private Button openRoom;
    private Button shareLink;
    private Button switchToRoom;
    private TextView connectionStatus;
    private String deviceName = "RALLLE";

    private RoomLink room;
    private int roomLinkLength = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            connectionStatus.setText(getResources().getString(R.string.connection_status_true) + deviceName);
        } else {
            btIsClicked = false;
            jitsiIsClicked = false;
            bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
            openRoom.setEnabled(false);
            setEnableLinkAndRoom(false);
            connectionStatus.setText(getResources().getString(R.string.connection_status_false));
        }
    }

    /**
     * @param v
     * On click of the openRoom button we open a jitsi room and enable the shareLink and
     * switchToRoom button.
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
        if (room == null) {
            room = new RoomLink(roomLinkLength);
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Jitsi Room Link", room.url);
        clipboard.setPrimaryClip(clip);

        showToast("Link kopiert");
    }

    /**
     * @param v
     * Switches to the next activity with the open jitsi room.
     */
    public void onClickSwitchToRoom(View v) {
        if (room == null) {
            room = new RoomLink(roomLinkLength);
        }
        Intent intent = new Intent(this, JitsiActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra("ROOM_ID", room.id);
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
}