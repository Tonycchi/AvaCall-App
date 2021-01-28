package com.example.rcvc;

import android.content.Context;

public class AnalogController extends Controller {

    public AnalogController(Context context, BluetoothConnectionService b) {
        super(context, b, 75);
    }

    @Override
    public void sendPowers(int angle, int strength) {
        //0 is r, 1 is l
        float[] outputs = computePowers(angle, strength);
        COMMANDER.send(outputs[0], outputs[1]);
    }

    /**
     * converts analog input to two motor speeds
     *
     * @param angle, strength analog axes
     * @return speeds for right(o[0]) and left(o[1]) motor
     */
    public float[] computePowers(int angle, int strength) {
        float factor = (float) strength / 100.0f;

        float[] o = new float[2];
        float r = 0.0f;
        float l = 0.0f;
        o[0] = 0.0f;
        o[1] = 0.0f;
        int delta = 5;
        int angleDif;
        float speedDif;
        float ratio = 90.0f;
        if (angle >= 0 + delta && angle <= 90) { // forwards right
            angleDif = 90 - angle;
            speedDif = (float) angleDif / ratio;
            r = 1.0f - speedDif;
            l = 1.0f;
        } else if (angle > 90 && angle <= 180 - delta) { // forwards left
            angleDif = angle - 90;
            speedDif = (float) angleDif / ratio;
            r =1.0f;
            l =1.0f - speedDif;
        } else if (angle > 360 - delta || angle < 0 + delta) { // rotate right
            r = -1.0f;
            l = 1.0f;
        } else if (angle > 180 - delta && angle < 180 + delta) { // rotate left
            r = 1.0f;
            l = -1.0f;
        } else if (angle >= 180 + delta && angle <= 270) { // backwards left
            angleDif = 270 - angle;
            speedDif = (float) angleDif / ratio;
            r =-1.0f;
            l =-1.0f + speedDif;
        } else if (angle > 270 && angle <= 360 - delta){ // backwards right
            angleDif = angle - 270;
            speedDif = (float) angleDif / ratio;
            r =-1.0f + speedDif;
            l =-1.0f;
        }
        o[0] = r * factor;
        o[1] = l * factor;
        return o;
    }
}