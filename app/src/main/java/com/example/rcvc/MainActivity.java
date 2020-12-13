package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private boolean btIsClicked = false;
    Button bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = findViewById(R.id.button_bluetooth);
    }

    public void onClickBluetooth(View v) {
        if (!btIsClicked) {
            btIsClicked = true;
            bluetooth.setText(getString(R.string.button_bluetooth_connected));
        } else {
            btIsClicked = false;
            bluetooth.setText(getString(R.string.button_bluetooth_disconnected));
        }
    }
}