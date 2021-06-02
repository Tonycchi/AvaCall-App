package com.example.model.robot.ev3;

import com.example.model.connection.BluetoothConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.ControllerInput;

public class EV3Controller implements Controller {

    public EV3Controller(String specs, BluetoothConnectionService bluetoothConnectionService) {
        //TODO specs determines ports
    }

    @Override
    public void sendInput(ControllerInput controllerInput) {
        //TODO functionality from EV3.java, DirectCommander in this package
    }
}
