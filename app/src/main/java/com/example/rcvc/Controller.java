package com.example.rcvc;

public abstract class Controller {

    final DirectCommander COMMANDER;

    public Controller(BluetoothConnectionService b, int maxPower) {
        COMMANDER = new DirectCommander(b, maxPower);
    }

    /**
     * self explanatory
     *
     * @param a,b    needed parameters to compute motor strength
     */
    public abstract void sendPowers(int a, int b);
}
