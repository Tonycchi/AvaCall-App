package com.example.rcvc;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.bluetooth_title);
        setContentView(R.layout.bluetooth_connection);
    }

}
