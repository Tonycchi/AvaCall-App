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

        // the power of the ports from the global memorey
        int strength1 = message[5];
        int strength2 = message[6];
        // ports are combined in the message counter first byte
        int port1 = (message[2] & 0xf0) >>> 4;
        int port2 = (message[2] & 0x0f);
        // id of the element is in the message counter second byte
        int id1 = message[3];
        int id2 = message[3];
        Log.d(TAG,message[5]+":"+message[6]+":"+message[7]+":"+message[8]+"->Stärke:"+strength1);

        String displayStrengthMessage = "Port "+port1+" wird gesteuert von element "+id1 +", ist " +
                ok+" und hat Stärke: "+strength1+"\nPort "+port2+" wird gesteuert von element "+id2
                +", ist " +ok+" und hat Stärke: "+strength2;
        motorStrength.postValue(displayStrengthMessage);
    }
}
