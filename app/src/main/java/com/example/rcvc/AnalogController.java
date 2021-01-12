package com.example.rcvc;

import android.util.Log;

public class AnalogController extends Controller{

    private final String TAG = "AnalogController";

    BluetoothConnectionService b;

    public AnalogController(BluetoothConnectionService b) {
        super(b);
        this.b = b;
    }

    /**
     * self explanatory
     *
     * @param angle analog axes
     * @param strength
     */
    public void sendPowers(int angle, int strength) {
        //0 is r, 1 is l
        float[] outputs = computePowers(angle, strength);
        DirectCommander.send(outputs[0], outputs[1], b);
    }

    /**
     * analog input to two motor speeds
     *
     * @param angle, strength analog axes
     * @return speeds for four motors in byte array length 4, compatible with DirectCommander.java
     */

    public float[] computePowers(int angle, int strength) {
        float x = (float) Math.cos(angle)*(strength/100);
        float y = (float) (Math.sin(angle))*(strength/100);
        Log.d(TAG, "x and y values: " + x + " " + y);

        float r;
        float l;
        float scale = 1.0f;
        float[] o = new float[2];
        if (angle == 0 && strength != 0) {
            o[0] = -1.0f;
            o[1] = 1.0f;
            return o;
        }
        if (angle == 180 && strength != 0) {
            o[0] = 1.0f;
            o[1] = -1.0f;
            return o;
        }

        r = scale * (y + x);
        l = scale * (y - x);

        if (r > 1.0f) r = 1.0f;
        if (l > 1.0f) l = 1.0f;
        if (r < -1.0f) r = -1.0f;
        if (l < -1.0f) l = -1.0f;

        o[0] = r;
        o[1] = l;
        return o;
    }

}