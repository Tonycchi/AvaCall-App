package com.example.model.robot;

public interface Controller {

    /**
     * uses given controller input to send command to robot
     *
     * @param input input
     */
    void sendInput(String input);

}
