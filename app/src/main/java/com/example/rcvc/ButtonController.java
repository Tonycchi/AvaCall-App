package com.example.rcvc;

import android.content.Context;

public class ButtonController {
    //TODO use DirectCommander

    public static final int STOP = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public static final int TURN_RIGHT = 3;
    public static final int TURN_LEFT = 4;

    private final DirectCommander COMMANDER;

    public ButtonController(Context context, BluetoothConnectionService b) {
        COMMANDER = new DirectCommander(context, b);

    }

    /**
     * sends the correct powers for the motors to the DirectCommander
     * @param command the command to move in a certain direction or stop
     */
    public void sendPowers(int command) {
        switch (command) {
            case STOP:
                COMMANDER.send(0.0f, 0.0f);
                break;
            case FORWARD:
                COMMANDER.send(1.0f, 1.0f);
                break;
            case BACKWARD:
                COMMANDER.send(-1.0f, -1.0f);
                break;
            case TURN_RIGHT:
                COMMANDER.send(-1.0f, 1.0f);
                break;
            case TURN_LEFT:
                COMMANDER.send(1.0f, -1.0f);
                break;
            default:
        }
    }
}