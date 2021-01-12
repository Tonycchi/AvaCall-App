package com.example.rcvc;

public class AnalogController {

    private BluetoothConnectionService b;

    public AnalogController(BluetoothConnectionService b) {
        this.b = b;
    }

    /**
     * self explanatory
     *
     * @param angle analog axes
     * @param strength
     */
    public void input(int angle, int strength) {
        float[] outputs = computePowers(angle, strength);
        //0 is r, 1 is l
        DirectCommander.send(outputs[0], outputs[1], b);
    }

    /**
     * analog input to two motor speeds
     * TODO replace base with actual max speed, dont know what it is
     *
     * @param angle, strength analog axes
     * @return speeds for four motors in byte array length 4, compatible with DirectCommander.java
     */

    private float[] computePowers(int angle, int strength) {
        float r = 0.0f;
        float l = 0.0f;

        // written under assumption that Motor A right, Motor D left *//*
        float[] o = new float[2];
        o[0] = r;
        o[1] = l;
        return o;
    }


    /*private float[] computePowers(float x, float y) {
        float r = 0.0f, l = 0.0f;
        float scale = 1.0f;

        r = scale * (y + x);
        l = scale * (y - x);

        if (r > 1.0f) r = 1.0f;
        if (l > 1.0f) l = 1.0f;

        // written under assumption that Motor A right, Motor D left *//*
        float[] o = new float[2];
        o[0] = r;
        o[1] = l;
        return o;

    }*/
}