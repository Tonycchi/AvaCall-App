package com.example.model.robot;

import com.example.model.connection.ConnectionService;

import java.util.List;

public interface Robot {

    /**
     * @param id key of model settings in db
     * @return a controller using model settings specified by key
     */
    Controller getController(int id, ConnectionService service);

    void saveModel(int id, String name, String type, List<Integer[]> values);
}
