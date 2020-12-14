package com.example.rcvc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class JitsiActivity extends AppCompatActivity {

    private FloatingActionButton endCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jitsi);

        endCall = findViewById(R.id.button_end_call);
    }

    public void onClickEndCall(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}