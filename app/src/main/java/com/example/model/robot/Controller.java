package com.example.model.robot;

import com.example.data.RobotModel;

public interface Controller {

    /**
     * uses given controller input to send command to robot
     *
     * @param input input
     */
    void sendInput(int... input);

    /**
     * get the output for the motors from the robot
     */
    void getOutput();

    /**
     * 
     * @return String specifying which control elements to show on website
     */
    String getControlElementString();

    /**
     *
     * @return Object representing used model
     */
    RobotModel getCurrentModel();

    void setLastUsedId(int id);

    int getLastUsedId();

    void setInputFromWebClient(boolean input);

}
