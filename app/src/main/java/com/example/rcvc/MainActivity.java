package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean btIsClicked = false;
    private boolean jitsiIsClicked = false;
    Button bluetooth;
    Button openRoom;
    Button shareLink;
    Button switchToRoom;
    TextView connectionStatus;
    String deviceName = "RALLLE";

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

    public void onClickShareLink(View v) {
        showToast("Link kopiert");
    }

    public void onClickSwitchToRoom(View v) {
        Intent intent = new Intent(this, JitsiActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setEnableLinkAndRoom(boolean enabled) {
        shareLink.setEnabled(enabled);
        switchToRoom.setEnabled(enabled);
    }
}