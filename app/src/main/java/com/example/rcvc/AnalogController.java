package com.example.rcvc;

import android.util.Log;

public class AnalogController extends Controller {

    private final String TAG = "AnalogController";
    private DirectCommander directCommander;

    public AnalogController(BluetoothConnectionService b) {
        super(b);
        directCommander = new DirectCommander(b, 75);
    }

    /**
     * self explanatory
     *
     * @param angle    analog axes
     * @param strength distance from origin
     */
    public void sendPowers(int angle, int strength) {
        //0 is r, 1 is l
        float[] outputs = computePowers(angle, strength);
        directCommander.send(outputs[0], outputs[1]);
    }

    /**
     * analog input to two motor speeds
     *
     * @param angle, strength analog axes
     * @return speeds for four motors in byte array length 4, compatible with DirectCommander.java
     */
    public float[] computePowers(int angle, int strength) {
        float factor = (float) strength / 100.0f;

        float[] o = new float[2];
        float r = 0.0f;
        float l = 0.0f;
        o[0] = 0.0f;
        o[1] = 0.0f;
        int delta = 5;
        int angleDif = 0;
        float speedDif = 0.0f;
        float ratio = 90.0f;
        //Ggf Reihenfolge der if-Abfragen anpassen
        if (angle > 360 - delta || angle < 0 + delta) {
            r = -1.0f;
            l = 1.0f;
        } else if (angle > 180 - delta && angle < 180 + delta) {
            r = 1.0f;
            l = -1.0f;
        } else if (angle >= 0 + delta && angle <= 90) {
            angleDif = 90 - angle;
            speedDif = (float) angleDif / ratio;
            r = 1.0f - speedDif;
            l = 1.0f;
        } else if (angle > 90 && angle <= 180 - delta) {
            angleDif = angle - 90;
            speedDif = (float) angleDif / ratio;
            r =1.0f;
            l =1.0f - speedDif;
        } else if (angle >= 180 + delta && angle <= 270) {
            angleDif = 270 - angle;
            speedDif = (float) angleDif / ratio;
            r =-1.0f;
            l =-1.0f + speedDif;
        } else if (angle > 270 && angle <= 360 - delta){
            angleDif = angle - 270;
            speedDif = (float) angleDif / ratio;
            r =-1.0f + speedDif;
            l =-1.0f;
        } else {
            // Do nothing
        }
        o[0] = r * factor;
        o[1] = l * factor;
        return o;
    }

}