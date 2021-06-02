package com.example.model.robot.ev3;

import com.example.model.connection.BluetoothConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.ControllerInput;

public class EV3Controller implements Controller {

    private DirectCommander directCommander;

    public EV3Controller(String specs, BluetoothConnectionService bluetoothConnectionService) {
        String[] s = specs.split(";");
        directCommander = new DirectCommander(bluetoothConnectionService, Integer.parseInt(s[0]), Integer.parseInt(s[1]));
    }

    @Override
    public void sendInput(ControllerInput controllerInput) {
        float[] t = computePowers(controllerInput.angle, controllerInput.strength);
        directCommander.send(t[0], t[1]);
    }

    private float[] computePowers(int angle, int strength) {
        float right = 0.0f;
        float left = 0.0f;

        if (angle >= 0 && angle < 90) { //0°-89°
            left = 100; //100 to 100
            right = -100 + angle*20/9.0f; //-100 to 100

        } else if (angle >= 90 && angle < 180) { //90°-179°
            left = 100 - (angle-90)*20/9.0f; //100 to -100
            right = 100; //100 to 100

        } else if (angle >= 180 && angle < 270) { //180°-269°
            left = -100; //-100 to -100
            right = 100 - (angle-180)*20/9.0f; //50 to -100

        } else if (angle >= 270 && angle <= 360) {//270°-359°
            left = -100 + (angle-270)*20/9.0f; //-100 to 100
            right = -100; //-100 to -100
        }

        float[] output = new float[2];
        output[0] = right * strength / 10000;
        output[1] = left * strength / 10000;
        //Log.d("Motorsignale", "Links:"+output[1]+" Rechts:"+output[0]);

        return output;
    }
}
