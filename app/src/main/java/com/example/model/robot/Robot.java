package com.example.model.robot;

import com.example.model.connection.ConnectionService;

public interface Robot {

    /**
     *
     * @param id key of model settings in db
     * @return a controller using model settings specified by key
     */
    Controller getController(int id, ConnectionService service);

    //TODO save new model/profile
}
