package com.example.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.model.robot.Controller;
import com.example.model.robot.ev3.EV3ControlElement;
import com.example.model.robot.ev3.EV3Controller;
import com.facebook.infer.annotation.Mutable;

import java.util.ArrayList;
import java.util.HashSet;

public class TestRobotModel {
    private MainModel mainModel;
    private static final String TAG = "TestRobotModel";
    MutableLiveData<String> motorStrength;
    MutableLiveData<Boolean> stall;
    private int[] lastExpected;

    public TestRobotModel(MainModel mainModel) {
        motorStrength = new MutableLiveData<String>();
        this.mainModel = mainModel;
        stall = new MutableLiveData<Boolean>();
        this.lastExpected = new int[2];
    }

    public MutableLiveData<String> getMotorStrength() {
        return motorStrength;
    }

    public MutableLiveData<Boolean> getStall() {
        return stall;
    }

    public boolean detectStall(int expectedStrength, int actualStrength) {
        if (Math.abs(expectedStrength) > Math.abs(actualStrength)) {
            return Math.abs(expectedStrength - actualStrength) > Math.abs(expectedStrength * 0.7);
        }
        return false;
    }

    private boolean detectBigChange(int expectedStrength1, int expectedStrength2) {
        int delta1 = Math.abs((int) (lastExpected[0] * 0.15));
        int delta2 = Math.abs((int) (lastExpected[1] * 0.15));
        return (Math.abs(lastExpected[0] - expectedStrength1) > delta1 || Math.abs(lastExpected[1] - expectedStrength2) > delta2);
    }

    public void checkStall(byte[] message) {
            EV3Controller controller = (EV3Controller) mainModel.getController();
            Boolean stallDetected = false;
            int lastUsed = controller.getLastUsedId();
            int expectedStrength1 = message[2];
            int expectedStrength2 = message[3];
            int actualStrength1 = message[5];
            int actualStrength2 = message[6];
            Log.d(TAG, "ICH BIN AKTUELL 1: " + actualStrength1 + " ICH BIN SOLL 1: " + expectedStrength1);
            Log.d(TAG, "ICH BIN AKTUELL 2: " + actualStrength2 + " ICH BIN SOLL 2: " + expectedStrength2);
//            if (expectedStrength1 >= lastExpected[0] && expectedStrength2 >= lastExpected[1]) {
            if (!detectBigChange(expectedStrength1, expectedStrength2) && !(Math.abs(expectedStrength1) < 5 && expectedStrength1 != 0 && actualStrength1 == 0) && !(Math.abs(expectedStrength2) < 5 && expectedStrength2 != 0 && actualStrength2 == 0)) {
                stallDetected = detectStall(expectedStrength1, actualStrength1) ||
                        detectStall(expectedStrength2, actualStrength2);
            }
            if (stallDetected && controller.getInputFromWebClient()) {
                mainModel.sendStallDetected(controller.getControlElements().get(lastUsed).getType(), lastUsed);
            } else if (!stallDetected && controller.getInputFromWebClient()) {
                mainModel.sendStallEnded(controller.getControlElements().get(lastUsed).getType(), lastUsed);
            }
            stall.postValue(stallDetected);
            lastExpected[0] = expectedStrength1;
            lastExpected[1] = expectedStrength2;

 //       Log.d(TAG, "received");
 //       String ok = message[4] == 0x02 ? "ok" : "nicht ok";

//        int strength = message[8]<<3;
//        strength += message[7]<<2;
//        strength += message[6]<<1;
//        strength += message[5];
        //       int strength1 = message[5];
        //       int strength2 = message[6];
        //      int strength3 = message[7];
        //       int strength4 = message[8];
        //       int id1 = (message[2] & 0xf0) >>> 4;
        //       int id2 = message[2] & 0x0f;
        //       int id3 = (message[3] & 0xf0) >>> 4;
        //       int id4 = message[3] & 0x0f;
//            StringBuilder sb = new StringBuilder();
//            for(int i = 0; i < controller.getControlElements().size(); i++){
//                Log.d(TAG, "Hallo display message");
//                String newline = i != 0 ? "\n" : "";
//                int strength = controller.getUsedId() == i ? actualStrength1 : 0;
//                if(controller.getControlElements().get(i).getType().equals("joystick")) {
//                    int strength2 = controller.getUsedId() == i ? actualStrength2 : 0;
//                    sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
//                    + " an und hat die Stärke " + strength + ".");
//                    sb.append("\n" + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[1]
//                            + " an und hat die Stärke " + strength2 + ".");
//                } else {
//                    sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
//                    + " an und hat die Stärke " + strength + ".");
//                }
//            }
////        String displayStrengthMessage = "Port "+port1+" wird gesteuert von element "+id1 +", ist " +
//            //            ok+" und hat Stärke: "+strength1+"\nPort "+port2+" wird gesteuert von element "+id2
//            //               +", ist " +ok+" und hat Stärke: "+strength2;
//            String displayStrengthMessage = sb.toString();
//            motorStrength.postValue(displayStrengthMessage);
    }

    public void receivedMotorStrengths(byte[] message) {
        StringBuilder sb = new StringBuilder();
        EV3Controller controller = (EV3Controller) mainModel.getController();
        int[] strength = {message[5], message[6], message[7], message[8]};

        for (int i = 0; i < controller.getControlElements().size(); i++) {
            Log.d(TAG, "Hallo display message");
            String newline = i != 0 ? "\n" : "";
            if (controller.getControlElements().get(i).getType().equals("joystick")) {
                int port1 = helpmepls(controller.getControlElements().get(i).getPort()[0]);
                int port2 = helpmepls(controller.getControlElements().get(i).getPort()[1]);
                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
                        + " an und hat die Stärke " + strength[port1] + ".");
                sb.append("\n" + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[1]
                        + " an und hat die Stärke " + strength[port2] + ".");
            } else {
                int port = helpmepls(controller.getControlElements().get(i).getPort()[0]);
                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
                        + " an und hat die Stärke " + strength[port] + ".");
            }
        }
        motorStrength.postValue(sb.toString());
    }

    public int helpmepls(int port) {
        switch (port) {
            case (1):
                return 0;
            case (2):
                return 1;
            case (4):
                return 2;
            case (8):
                return 3;
        }
        return -1;
    }

//    public void setMessage() {
//        EV3Controller controller = (EV3Controller) mainModel.getController();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < controller.getControlElements().size(); i++) {
//            Log.d(TAG, "Hallo display message");
//            String newline = i != 0 ? "\n" : "";
//            if (controller.getControlElements().get(i).getType().equals("joystick")) {
//                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
//                        + " an und hat die Stärke " + 0 + ".");
//                sb.append("\n" + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[1]
//                        + " an und hat die Stärke " + 0 + ".");
//            } else {
//                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
//                        + " an und hat die Stärke " + 0 + ".");
//            }
//        }
//        motorStrength.postValue(sb.toString());
//    }
}

//    public void setPorts(String specs) {
//        ports = new HashSet<>();
//
//        // split into $controlElement$ = $element$:$attributes$
//        String[] tmp = specs.split("\\|");
//        // put into list with [0] = $element$, [1] = $attributes$
//        ArrayList<String[]> list = new ArrayList<>();
//        for (String t : tmp) {
//            list.add(t.split(":"));
//        }
//        for (String[] k : list) {
//            String[] attrs = k[1].split(";");
//            switch (k[0]) {
//                case "joystick":
//                    Log.d(TAG, "Joystick ports");
//                    String[] portsString = attrs[1].split(",");
//                    ports.add(Integer.parseInt(portsString[0]));
//                    ports.add(Integer.parseInt(portsString[1]));
//                    break;
//                case "slider":
//                case "button":
//                    Log.d(TAG, "Slider/Button ports");
//                    ports.add(Integer.parseInt(attrs[1]));
//                    break;
//                default:
//            }
//        }
//        Log.d(TAG, "SET PORTS: " + ports.size());
//    }

