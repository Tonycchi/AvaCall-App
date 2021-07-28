package com.example.model.robot;

import com.example.data.RobotModel;
import com.example.model.connection.ConnectionService;

import java.util.List;

public interface Robot {

    /**
     * @param robotModel
     * @return a controller using model settings specified by key
     */
    Controller getController(RobotModel robotModel, ConnectionService service);

    void saveModel(int id, String name, String type, List<Integer[]> values);
}
