package com.example.rcvc;

public class ButtonController extends Controller {
    //TODO use DirectCommander

    private final String TAG = "ButtonController";
    private DirectCommander directCommander;

    public static final int STOP = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public static final int TURN_RIGHT = 3;
    public static final int TURN_LEFT = 4;

    public ButtonController(BluetoothConnectionService b) {
        super(b);
        directCommander = new DirectCommander(b, 50);
    }

    public void sendPowers(int command, int dummy) {
        switch (command) {
            case STOP:
                directCommander.send(0.0f, 0.0f);
                break;
            case FORWARD:
                directCommander.send(1.0f, 1.0f);
                break;
            case BACKWARD:
                directCommander.send(-1.0f, -1.0f);
                break;
            case TURN_RIGHT:
                directCommander.send(-1.0f, 1.0f);
                break;
            case TURN_LEFT:
                directCommander.send(1.0f, -1.0f);
                break;
            default:
        }
    }

}