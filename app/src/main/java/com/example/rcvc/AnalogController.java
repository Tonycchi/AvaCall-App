package com.example.rcvc;

import android.content.Context;

public class AnalogController {

    private final DirectCommander COMMANDER;

    public AnalogController(Context context, BluetoothConnectionService b) {
        COMMANDER = new DirectCommander(context, b);
    }

    /**
     * sends the correct powers for the motors to the DirectCommander
     * @param angle the angle of they joystick
     * @param strength the deflection of the joystick
     */
    public void sendPowers(int angle, int strength) {
        int str = strength;
        if (str > 100) {
            str = 100;
        }
        if (str < 0) {
            str = 0;
        }
        //0 is r, 1 is l
        float[] outputs = computePowers(angle, str);
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

        float[] output = new float[2];
        float right = 0.0f;
        float left = 0.0f;
        output[0] = 0.0f;
        output[1] = 0.0f;
        int delta = 10;
        int angleDif;
        float speedDif;
        float ratio = 90.0f;
        if (angle >= 0 + delta && angle <= 90) { // forwards right
            angleDif = 90 - angle;
            speedDif = (float) angleDif / ratio;
            right = 1.0f - speedDif;
            left = 1.0f;
        } else if (angle > 90 && angle <= 180 - delta) { // forwards left
            angleDif = angle - 90;
            speedDif = (float) angleDif / ratio;
            right =1.0f;
            left =1.0f - speedDif;
        } else if (angle > 360 - delta || angle < 0 + delta) { // rotate right
            right = -0.5f;
            left = 0.5f;
        } else if (angle > 180 - delta && angle < 180 + delta) { // rotate left
            right = 0.5f;
            left = -0.5f;
        } else if (angle >= 180 + delta && angle <= 270) { // backwards left
            angleDif = 270 - angle;
            speedDif = (float) angleDif / ratio;
            right =-1.0f;
            left =-1.0f + speedDif;
        } else if (angle > 270 && angle <= 360 - delta){ // backwards right
            angleDif = angle - 270;
            speedDif = (float) angleDif / ratio;
            right =-1.0f + speedDif;
            left =-1.0f;
        }
        output[0] = right * factor;
        output[1] = left * factor;
        return output;
    }
}