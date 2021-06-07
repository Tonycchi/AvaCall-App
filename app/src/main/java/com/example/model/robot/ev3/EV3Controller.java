package com.example.model.robot.ev3;

import android.util.Log;

import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.ControllerInput;

import java.util.HashMap;

public class EV3Controller implements Controller {

    private final String TAG = "EV3Controller";

    private DirectCommander directCommander;

    public EV3Controller(String specs, ConnectionService service) {
        String[] s = {"1", "8"};

        Log.d(TAG, specs);

        /*
        joystick:50;1,8|slider:30;4|button:20;2;2000
         */
        String[] tmp = specs.split("\\|");
        HashMap<String, String> elements = new HashMap<>();
        for (String t : tmp) {
            String[] a = t.split(":");
            elements.put(a[0], a[1]);
        }
        String[] ports = new String[2];
        if (elements.containsKey("joystick"))
            ports = elements.get("joystick").split(";")[1].split(",");

        Log.d(TAG, ports[0] + " " + ports[1]);

        directCommander = new DirectCommander(service, Integer.parseInt(ports[0]), Integer.parseInt(ports[1]));
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
