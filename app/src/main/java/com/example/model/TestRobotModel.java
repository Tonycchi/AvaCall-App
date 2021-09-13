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
    private final MainModel mainModel;
    private static final String TAG = "TestRobotModel";
    MutableLiveData<String> motorStrength;
    MutableLiveData<Boolean> stall;
    private final int[] lastExpected;

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
    }

    public void receivedMotorStrengths(byte[] message) {
        StringBuilder sb = new StringBuilder();
        EV3Controller controller = (EV3Controller) mainModel.getController();
        int[] strength = {message[5], message[6], message[7], message[8]};

        for (int i = 0; i < controller.getControlElements().size(); i++) {
            Log.d(TAG, "Hallo display message");
            String newline = i != 0 ? "\n" : "";
            if (controller.getControlElements().get(i).getType().equals("joystick")) {
                int port1 = mapPortToIndex(controller.getControlElements().get(i).getPort()[0]);
                int port2 = mapPortToIndex(controller.getControlElements().get(i).getPort()[1]);
                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
                        + " an und hat die Stärke " + strength[port1] + ".");
                sb.append("\n" + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[1]
                        + " an und hat die Stärke " + strength[port2] + ".");
            } else {
                int port = mapPortToIndex(controller.getControlElements().get(i).getPort()[0]);
                sb.append(newline + "Element " + i + " steuert Port " + controller.getControlElements().get(i).getPort()[0]
                        + " an und hat die Stärke " + strength[port] + ".");
            }
        }
        motorStrength.postValue(sb.toString());
    }

    public int mapPortToIndex(int port) {
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
}
