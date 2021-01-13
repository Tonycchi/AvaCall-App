package com.example.rcvc;

public abstract class Controller {

    private final BluetoothConnectionService B;

    public Controller(BluetoothConnectionService b) {
        B = b;
    }

    public abstract void sendPowers(int a, int b);

}
