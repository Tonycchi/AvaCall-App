package com.example.rcvc;

public class ButtonController extends Controller {
    //TODO use DirectCommander

    public static final int STOP = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public static final int TURN_RIGHT = 3;
    public static final int TURN_LEFT = 4;

    public ButtonController(BluetoothConnectionService b) {
        super(b, 50);
    }

    @Override
    public void sendPowers(int command, int dummy) {
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