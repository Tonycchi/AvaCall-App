package com.example.rcvc;

public abstract class Controller {

    final BluetoothConnectionService B;

    public Controller(BluetoothConnectionService b) {
        this.B = b;
    }

    public abstract void sendPowers(int a, int b);

}
