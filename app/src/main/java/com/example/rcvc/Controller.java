package com.example.rcvc;

import android.content.Context;

public abstract class Controller {

    final DirectCommander COMMANDER;

    public Controller(Context context, BluetoothConnectionService b, int maxPower) {
        COMMANDER = new DirectCommander(context, b, maxPower);
    }

    /**
     * self explanatory
     *
     * @param a,b    needed parameters to compute motor strength
     */
    public abstract void sendPowers(int a, int b);
}
