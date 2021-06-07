package com.example.model.robot.ev3;

import com.example.data.RobotModelDAO;
import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.Robot;

public class EV3 implements Robot {

    private RobotModelDAO db;

    public EV3(RobotModelDAO db) {
        this.db = db;
    }

    @Override
    public Controller getController(int id, ConnectionService service) {
        return new EV3Controller(db.getSpecs(id), service);
    }
}
