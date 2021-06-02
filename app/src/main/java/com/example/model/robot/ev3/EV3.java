package com.example.model.robot.ev3;

import com.example.model.robot.Controller;
import com.example.model.robot.Robot;

public class EV3 implements Robot {

    public EV3() {
        //TODO constructor gets dao for models
    }

    @Override
    public Controller getController(int key) {
        //TODO get specs string from db w/ key and construct EV3Controller
        return null;
    }
}
