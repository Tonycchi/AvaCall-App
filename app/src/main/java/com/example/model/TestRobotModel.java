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

    public void receivedMessage(byte[] message) {
        Log.d(TAG,"received");
        String ok = message[4]==0x02 ? "ok" : "nicht ok";

//        int strength = message[8]<<3;
//        strength += message[7]<<2;
//        strength += message[6]<<1;
//        strength += message[5];
        int strength1 = message[5];
        int strength2 = message[6];
        int port1 = (message[2] & 0xf0) >>> 4;
        int port2 = (message[2] & 0x0f);
        int id1 = message[3];
        int id2 = message[3];
        Log.d(TAG,message[5]+":"+message[6]+":"+message[7]+":"+message[8]+"->Stärke:"+strength1);

        String displayStrengthMessage = "Port "+port1+" wird gesteuert von element "+id1 +", ist " +
                ok+" und hat Stärke: "+strength1+"\nPort "+port2+" wird gesteuert von element "+id2
                +", ist " +ok+" und hat Stärke: "+strength2;
        motorStrength.postValue(displayStrengthMessage);
    }
}
