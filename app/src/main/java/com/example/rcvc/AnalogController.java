package com.example.rcvc;

import android.content.Context;
import android.util.Log;

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
        float right = 0.0f;
        float left = 0.0f;

        if (angle >= 0 && angle < 90) { //0°-89°
            left = 50 + angle*5/9.0f; //50 to 100
            right = -50 + angle*15/9.0f; //-50 to 100

        } else if (angle >= 90 && angle < 180) { //90°-179°
            left = 100 - (angle-90)*15/9.0f; //100 to -50
            right = 100 - (angle-90)*5/9.0f; //100 to 50

        } else if (angle >= 180 && angle < 270) { //180°-269°
            left = -50 - (angle-180)*5/9.0f; //-50 to -100
            right = 50 - (angle-180)*15/9.0f; //50 to -100

        } else if (angle >= 270 && angle <= 360) {//270°-359°
            left = -100 + (angle-270)*15/9.0f; //-100 to 50
            right = -100 + (angle-270)*5/9.0f; //-100 to -50
        }


        float[] output = new float[2];
        output[0] = right * strength / 10000;
        output[1] = left * strength / 10000;
        Log.d("Motorsignale", "Links:"+output[1]+" Rechts:"+output[0]);

        return output;
    }
}