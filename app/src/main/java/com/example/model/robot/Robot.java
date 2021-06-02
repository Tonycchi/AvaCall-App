package com.example.model.robot;

public interface Robot {

    /**
     *
     * @param key key of model settings in db
     * @return a controller using model settings specified by key
     */
    Controller getController(int key);

    //TODO save new model/profile
}
