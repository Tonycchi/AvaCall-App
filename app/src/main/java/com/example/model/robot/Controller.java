package com.example.model.robot;

public interface Controller {

    /**
     * uses given controller input to send command to robot
     *
     * @param input input
     */
    void sendInput(int... input);

    /**
     * 
     * @return String specifying which control elements to show on website
     */
    String getControlElementString();

}
