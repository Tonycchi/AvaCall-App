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

    public void reveivedMessage(String message) {
        Log.d(TAG,"received:"+message);
        String[] messageParts = message.split(":");
        String ok = messageParts[4] == "02" ? "ok" : "nicht ok";
        int strength = Integer.parseInt(messageParts[8]) >> 3;
        strength += Integer.parseInt(messageParts[7])<<2;
        strength += + Integer.parseInt(messageParts[6])<<1;
        strength += Integer.parseInt(messageParts[5]);
        String displayStrength = "Port "+messageParts[2]+" wird gesteuert von element "+messageParts[3]+", ist "+ok+" und hat Stärke: "+strength;
        motorStrength.postValue(message);
    }
}
