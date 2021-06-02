package com.example.model.robot;

import com.example.model.connection.BluetoothConnectionService;

public interface Robot {

    /**
     *
     * @param id key of model settings in db
     * @return a controller using model settings specified by key
     */
    Controller getController(int id, BluetoothConnectionService b);

    //TODO save new model/profile
}
