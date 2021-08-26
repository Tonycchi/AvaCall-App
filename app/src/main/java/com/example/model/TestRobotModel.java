package com.example.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;

public class TestRobotModel {
    private static final String TAG = "TestRobotModel";
    MutableLiveData<String> motorStrength;
    HashSet<Integer> ports;

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
        int strength3 = message[7];
        int strength4 = message[8];
        int port1 = (message[2] & 0xf0) >>> 4;
        int port2 = (message[2] & 0x0f);
        int id1 = message[3];
        int id2 = message[3];
        Log.d(TAG,message[5]+":"+message[6]+":"+message[7]+":"+message[8]+"->Stärke:"+strength1);
        if (ports != null) {
            StringBuilder sb = new StringBuilder();
            if (ports.contains(1)) {
                sb.append("Port " + 1 + " wird gesteuert von element " + 0 + ", ist " + ok + " und hat Stärke: " + strength1 + "\n");
            }
            if (ports.contains(2)) {
                sb.append("Port " + 2 + " wird gesteuert von element " + 0 + ", ist " + ok + " und hat Stärke: " + strength2 + "\n");
            }
            if (ports.contains(4)) {
                sb.append("Port " + 4 + " wird gesteuert von element " + 0 + ", ist " + ok + " und hat Stärke: " + strength3 + "\n");
            }
            if (ports.contains(8)) {
                sb.append("Port " + 8 + " wird gesteuert von element " + 0 + ", ist " + ok + " und hat Stärke: " + strength4 + "\n");
            }
//        String displayStrengthMessage = "Port "+port1+" wird gesteuert von element "+id1 +", ist " +
//                ok+" und hat Stärke: "+strength1+"\nPort "+port2+" wird gesteuert von element "+id2
//                +", ist " +ok+" und hat Stärke: "+strength2;
            String displayStrengthMessage = sb.toString();
            motorStrength.postValue(displayStrengthMessage);
        }
    }

    public void setPorts(String specs) {
        ports = new HashSet<>();

        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();
        for (String t : tmp) {
            list.add(t.split(":"));
        }
        for (String[] k : list) {
            String[] attrs = k[1].split(";");
            switch (k[0]) {
                case "joystick":
                    Log.d(TAG, "Joystick ports");
                    String[] portsString = attrs[1].split(",");
                    ports.add(Integer.parseInt(portsString[0]));
                    ports.add(Integer.parseInt(portsString[1]));
                    break;
                case "slider":
                case "button":
                    Log.d(TAG, "Slider/Button ports");
                    ports.add(Integer.parseInt(attrs[1]));
                    break;
                default:
            }
        }
        Log.d(TAG, "SET PORTS: " + ports.size());
    }
}
