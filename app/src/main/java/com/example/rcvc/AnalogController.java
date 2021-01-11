package com.example.rcvc;

public class AnalogController {

    private BluetoothConnectionService B;

    public AnalogController(BluetoothConnectionService bluetoothConnectionService) {
        this.B = bluetoothConnectionService;
    }

    /**
     * self explanatory
     *
     * @param x,y analog axes
     */
    public void input(float x, float y) {
        DirectCommander.send(computePowers(x, y).powers, B);
    }

    /**
     * analog input to two motor speeds
     * TODO replace base with actual max speed, dont know what it is
     *
     * @param x,y analog axes
     * @return speeds for four motors in byte array length 4, compatible with DirectCommander.java
     */
    private MotorInstructions computePowers(float x, float y) {
        int base = 40; // max speed
        float r = 0.0f, l = 0.0f;
        float scale = 1.0f;

        r = scale * (y + x);
        l = scale * (y - x);

        if (r > 1.0f) r = 1.0f;
        if (l > 1.0f) l = 1.0f;

        /* written under assumption that Motor A right, Motor D left */
        byte[] o = new byte[4];
        o[0] = (byte) (r * base);
        o[1] = 0;
        o[2] = 0;
        o[3] = (byte) (l * base);

        return new MotorInstructions(o);
    }
}
