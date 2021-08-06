package com.example.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

public class TestRobotModel {
    private static final String TAG = "TestRobotModel";
    MutableLiveData<String> motorStrength;

    public TestRobotModel(){
        motorStrength = new MutableLiveData<String>();
    }

    public MutableLiveData<String> getMotorStrength() {
        return motorStrength;
    }

    public void reveivedMessage(byte[] message) {
        Log.d(TAG,"received");
        String ok = message[4]==0x02 ? "ok" : "nicht ok";

        int strength = message[8]<<3;
        strength += message[7]<<2;
        strength += + message[6]<<1;
        strength += message[5];

        String displayStrengthMessage = "Port "+message[3]+" wird gesteuert von element "+message[4]+", ist "+ok+" und hat Stärke: "+strength;
        motorStrength.postValue(displayStrengthMessage);
    }
}
