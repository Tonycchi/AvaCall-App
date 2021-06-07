package com.example.model.robot;

public interface Controller {

    /**
     * uses given controller input to send command to robot
     * @param controllerInput input
     */
    void sendInput(ControllerInput input);

}
